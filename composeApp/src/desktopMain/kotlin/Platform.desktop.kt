import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val type: PlatformType = PlatformType.Desktop
}

actual fun getPlatform(): Platform = JVMPlatform()

@OptIn(ExperimentalEncodingApi::class)
actual fun encode(text: String): String {
    return Base64.encode(text.encodeToByteArray())
}

@OptIn(ExperimentalEncodingApi::class)
actual fun decode(text: String): String {
    return Base64.decode(text).decodeToString()
}