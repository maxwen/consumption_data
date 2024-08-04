import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.maxwen.consumption_data.DATA_STORE_FILE_NAME
import com.maxwen.consumption_data.createDataStore
import com.maxwen.consumption_data.ui.App
import consumption_data.composeapp.generated.resources.Res
import consumption_data.composeapp.generated.resources.billing_unit_screen
import consumption_data.composeapp.generated.resources.settings_screen
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    Window(
        state = WindowState(width = 800.dp, height = 800.dp),
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.billing_unit_screen),
    ) {
        App(prefs = remember {
            createDataStore()
        })
    }
}