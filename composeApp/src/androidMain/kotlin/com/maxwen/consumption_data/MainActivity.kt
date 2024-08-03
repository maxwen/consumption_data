package com.maxwen.consumption_data

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.remember
import com.maxwen.consumption_data.models.MainViewModel
import com.maxwen.consumption_data.ui.App
import ui.theme.AppTheme

class MainActivity() : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AppTheme {
                App(prefs = remember {
                    createDataStore(applicationContext)
                })
            }
        }
    }
}



