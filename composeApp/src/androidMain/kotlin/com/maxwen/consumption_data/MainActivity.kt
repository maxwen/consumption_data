package com.maxwen.consumption_data

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import android.graphics.Color
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.maxwen.consumption_data.models.MainViewModel
import com.maxwen.consumption_data.ui.App
import com.maxwen.consumption_data.ui.theme.AppTheme
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.RequestCanceledException
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.launch

class MainActivity() : ComponentActivity() {
    var postNotificationState = PermissionState.NotDetermined

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AppTheme {
                val factory = rememberPermissionsControllerFactory()
                val controller = remember(factory) {
                    factory.createPermissionsController()
                }

                BindEffect(controller)
                lifecycleScope.launch {
                    postNotificationState =
                        controller.getPermissionState(Permission.REMOTE_NOTIFICATION)

                    if (postNotificationState != PermissionState.DeniedAlways) {
                        try {
                            controller.providePermission(Permission.REMOTE_NOTIFICATION)
                            postNotificationState = PermissionState.Granted
                        } catch (e: DeniedAlwaysException) {
                            postNotificationState = PermissionState.DeniedAlways
                        } catch (e: DeniedException) {
                            postNotificationState = PermissionState.Denied
                        } catch (e: RequestCanceledException) {
                            e.printStackTrace()
                        }
                    }
                }
                App(prefs = remember {
                    createDataStore(applicationContext)
                })
            }
        }
    }
}



