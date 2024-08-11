import android.os.Build
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val type: PlatformType = PlatformType.Android
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun encode(text: String): String {
    return text
}

actual fun decode(text: String): String {
    return text
}
