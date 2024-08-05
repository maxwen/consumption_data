import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.maxwen.consumption_data.ui.App
import com.maxwen.consumption_data.ui.theme.AppTheme

fun MainViewController() = ComposeUIViewController {
    AppTheme {
        App(prefs = remember {
            createDataStore()
        })
    }
}