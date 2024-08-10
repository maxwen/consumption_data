import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maxwen.consumption_data.DATA_STORE_FILE_NAME
import com.maxwen.consumption_data.createDataStore
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import java.io.File

enum class PlatformTypeDesktop {
    Linux,
    MacOS,
    Windows,
    Unknown
}

@OptIn(ExperimentalForeignApi::class)
fun createDataStore(): DataStore<Preferences> {
    return createDataStore {
        when (getPlatformTypeDesktop(getPlatform())) {
            PlatformTypeDesktop.Linux -> {
                var configHome: String? = System.getenv("XDG_CONFIG_HOME")
                if (configHome.isNullOrEmpty()) {
                    configHome = File(
                        System.getProperty("user.home"),
                        ".config"
                    ).absolutePath
                }

                File(
                    configHome,
                    File("ConsumptionData", DATA_STORE_FILE_NAME).path
                ).absolutePath
            }

            PlatformTypeDesktop.MacOS -> {
                val configHome = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null
                )
                File(
                    configHome.path,
                    File("ConsumptionData", DATA_STORE_FILE_NAME).path
                ).absolutePath
            }

            PlatformTypeDesktop.Windows -> {
                var profileHome = System.getenv("USERPROFILE")
                if (profileHome.isNullOrEmpty()) {
                    profileHome =  System.getProperty("user.home")
                }
                val configHome = File(
                    profileHome,
                    "AppData/Local"
                ).absolutePath

                File(
                    configHome,
                    File("ConsumptionData", DATA_STORE_FILE_NAME).path
                ).absolutePath
            }

            PlatformTypeDesktop.Unknown -> DATA_STORE_FILE_NAME
        }
    }
}

fun getPlatformTypeDesktop(platform: Platform): PlatformTypeDesktop {
    if (isLinuxDesktop(platform)) {
        return PlatformTypeDesktop.Linux
    }
    if (isWindowsDesktop(platform)) {
        return PlatformTypeDesktop.Windows
    }
    if (isMacOsDesktop(platform)) {
        return PlatformTypeDesktop.MacOS
    }
    return PlatformTypeDesktop.Unknown
}

private fun isLinuxDesktop(platform: Platform): Boolean {
    return System.getProperty("os.name", "generic").lowercase().indexOf("nux") >= 0
}

private fun isWindowsDesktop(platform: Platform): Boolean {
    return System.getProperty("os.name", "generic").lowercase().indexOf("win") >= 0
}

private fun isMacOsDesktop(platform: Platform): Boolean {
    val os = System.getProperty("os.name", "generic").lowercase()
    return os.indexOf("mac") >= 0 || os.indexOf("darwin") >= 0
}