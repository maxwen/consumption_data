import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface Platform {
    val name: String
    val type: PlatformType
}

enum class PlatformType {
    Android,
    Desktop,
    iOS,
    wasm
}

expect fun getPlatform(): Platform

expect fun encode(text: String): String

expect fun decode(text: String): String


