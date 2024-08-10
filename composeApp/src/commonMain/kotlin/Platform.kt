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
