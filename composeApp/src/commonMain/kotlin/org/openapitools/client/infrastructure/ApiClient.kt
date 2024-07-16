package org.openapitools.client.infrastructure

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


open class ApiClient(
    val baseUrl: String,
    val client: HttpClient = defaultClient,
    val username: String,
    val password: String
) {
    companion object {
        protected const val ContentType = "Content-Type"
        protected const val Accept = "Accept"
        protected const val JsonMediaType = "application/json"

        val defaultClient: HttpClient by lazy {
            HttpClient()
        }
    }

    protected inline fun <reified T : Any?> responseBody(body: String?, mediaType: String? = JsonMediaType): T? {
        if (body == null) {
            return null
        }

        return when {
            mediaType == null || (mediaType.startsWith("application/") && mediaType.endsWith("json")) -> {
                val bodyContent = body
                if (bodyContent.isEmpty()) {
                    return null
                }
                Json.decodeFromString<T>(bodyContent)
            }

            else -> throw UnsupportedOperationException("responseBody currently only supports JSON body.")
        }
    }

    protected suspend inline fun <reified I, reified T : Any?> request(requestConfig: RequestConfig<I>): ApiResponse<T?> {
        val url = URLBuilder(baseUrl).run {
            appendEncodedPathSegments(requestConfig.path.trimStart('/'))
            requestConfig.query.forEach { query ->
                query.value.forEach { queryValue ->
                    parameters.append(query.key, queryValue)
                }
            }
            build()
        }

        // take content-type/accept from spec or set to default (application/json) if not defined
        if (requestConfig.body != null && requestConfig.headers[ContentType].isNullOrEmpty()) {
            requestConfig.headers[ContentType] = JsonMediaType
        }
        if (requestConfig.headers[Accept].isNullOrEmpty()) {
            requestConfig.headers[Accept] = JsonMediaType
        }
        val headers = requestConfig.headers

        if (headers[Accept].isNullOrEmpty()) {
            throw kotlin.IllegalStateException("Missing Accept header. This is required.")
        }

        val response: HttpResponse = when (requestConfig.method) {
            RequestMethod.POST -> {
                client.post(url) {
                    setBody(requestConfig.body)
                    headers { headers.forEach { header -> append(header.key, header.value) } }
                    basicAuth(username, password)
                }
            }

            RequestMethod.DELETE -> {
                client.delete(url) {
                    setBody(requestConfig.body)
                    headers { headers.forEach { header -> append(header.key, header.value) } }
                    basicAuth(username, password)
                }
            }

            RequestMethod.GET -> {
                client.get(url) {
                    headers { headers.forEach { header -> append(header.key, header.value) } }
                    basicAuth(username, password)
                }
            }

            RequestMethod.HEAD -> {
                client.head(url) {
                    headers { headers.forEach { header -> append(header.key, header.value) } }
                    basicAuth(username, password)
                }
            }

            RequestMethod.OPTIONS -> {
                client.options(url) {
                    headers { headers.forEach { header -> append(header.key, header.value) } }
                    basicAuth(username, password)
                }
            }

            RequestMethod.PATCH -> {
                client.patch(url) {
                    setBody(requestConfig.body)
                    headers { headers.forEach { header -> append(header.key, header.value) } }
                    basicAuth(username, password)
                }
            }

            RequestMethod.PUT -> {
                client.put(url) {
                    setBody(requestConfig.body)
                    headers { headers.forEach { header -> append(header.key, header.value) } }
                    basicAuth(username, password)
                }
            }
        }

        when {
            response.isSuccessful ->
                return Success(
                    responseBody(response.body<String?>().toString(), response.headers[ContentType]),
                    response.status.value,
                    toMultimap(response.headers)
                )

            response.isClientError ->
                return ClientError(
                    response.status.description,
                    response.bodyAsText(),
                    response.status.value,
                    toMultimap(response.headers)
                )

            response.isInformational ->
                return Informational(
                    response.status.description,
                    response.status.value,
                    toMultimap(response.headers)
                )

            response.isInformational ->
                return Informational(
                    response.status.description,
                    response.status.value,
                    toMultimap(response.headers)
                )

            else -> return ServerError(
                response.status.description,
                response.bodyAsText(),
                response.status.value,
                toMultimap(response.headers)
            )
        }
    }

    protected fun toMultimap(headers: Headers): Map<String, MutableList<String>> {
        val result: MutableMap<String, MutableList<String>> = LinkedHashMap()
        var i = 0
        for (name in headers.names()) {
            val value = headers[name];
            var values = result[name]
            if (values == null) {
                values = ArrayList(2)
                result[name] = values
            }
            values.add(value!!)
        }
        return result
    }
}
