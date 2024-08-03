import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.maxwen.consumption_data.ui.App

fun MainViewController() = ComposeUIViewController {
    App(prefs = remember {
        createDataStore()
    })
}