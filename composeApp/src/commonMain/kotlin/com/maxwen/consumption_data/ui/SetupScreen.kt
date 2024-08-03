package com.maxwen.consumption_data.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
fun SetupScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.weight(0.5f))
        Text(
            "Hello please fill settings .. blablabla",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Button(modifier = Modifier.padding(top = 10.dp), onClick = {
            navHostController.navigate(Screens.SettingsScreen.name)
        }) {
            Text("Settings")
        }
        Spacer(modifier = Modifier.weight(0.5f))
    }
}