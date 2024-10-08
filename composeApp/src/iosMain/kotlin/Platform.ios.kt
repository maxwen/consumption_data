import platform.UIKit.UIDevice
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val type: PlatformType = PlatformType.iOS
}

actual fun getPlatform(): Platform = IOSPlatform()

@OptIn(ExperimentalEncodingApi::class)
actual fun encode(text: String): String {
    return Base64.encode(text.encodeToByteArray())
}

@OptIn(ExperimentalEncodingApi::class)
actual fun decode(text: String): String {
    return Base64.decode(text).decodeToString()
}