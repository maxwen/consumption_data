import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.maxwen.consumption_data.DATA_STORE_FILE_NAME
import com.maxwen.consumption_data.createDataStore
import com.maxwen.consumption_data.ui.App
import com.maxwen.consumption_data.ui.theme.AppTheme
import consumption_data.composeapp.generated.resources.Res
import consumption_data.composeapp.generated.resources.billing_unit_screen
import consumption_data.composeapp.generated.resources.settings_screen
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    Window(
        state = WindowState(width = LocalDensity.current.run { Dp(1280F)}, height = LocalDensity.current.run { Dp(1024F)}),
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.billing_unit_screen),
    ) {
        AppTheme {
            App(prefs = remember {
                createDataStore()
            })
        }
    }
}