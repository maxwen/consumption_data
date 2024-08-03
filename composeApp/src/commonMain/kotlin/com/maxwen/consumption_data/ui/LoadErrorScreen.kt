package com.maxwen.consumption_data.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.maxwen.consumption_data.models.MainViewModel

@Composable
fun LoadErrorScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.weight(0.5f))
        Text(
            "Load error.. blablabla", fontWeight = FontWeight.Bold, fontSize = 18.sp
        )
        Row(
            modifier = Modifier.padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                viewModel.reload()
            }) {
                Text("Reload")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = {
                navHostController.navigate(Screens.SettingsScreen.name)
            }) {
                Text("Settings")
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))
    }
}