@file:Suppress("unused")
package org.openapitools.client.infrastructure

open class ClientException(message: kotlin.String? = null, val statusCode: Int = -1, val response: Response? = null) : Throwable(message) {

    companion object {
        private const val serialVersionUID: Long = 123L
    }
}

open class ServerException(message: kotlin.String? = null, val statusCode: Int = -1, val response: Response? = null) : Throwable(message) {

    companion object {
        private const val serialVersionUID: Long = 456L
    }
}
