import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.maxwen.consumption_data.DATA_STORE_FILE_NAME
import com.maxwen.consumption_data.createDataStore
import com.maxwen.consumption_data.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "consumption_data",
    ) {
        App(prefs = remember {
            createDataStore { DATA_STORE_FILE_NAME }
        })
    }
}