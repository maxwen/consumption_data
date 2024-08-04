interface Platform {
    val name: String
    val type: PlatformType
}

enum class PlatformType {
    Android,
    Desktop,
    iOS
}

expect fun getPlatform(): Platform