package com.maxwen.consumption_data.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.maxwen.consumption_data.models.MainViewModel
import consumption_data.composeapp.generated.resources.Res
import consumption_data.composeapp.generated.resources.eye_off_outline
import consumption_data.composeapp.generated.resources.eye_outline
import consumption_data.composeapp.generated.resources.get_credentials_activity_header
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(10.dp),
    ) {
        val baseUrl by viewModel.baseurl.collectAsState()
        val username by viewModel.username.collectAsState()
        val password by viewModel.password.collectAsState()
        val dataState by viewModel.dataState.collectAsState()
        var testDone by remember {
            mutableStateOf(false)
        }

        var passwordVisibility by remember { mutableStateOf(false) }

        val icon = if (passwordVisibility)
            vectorResource(Res.drawable.eye_off_outline)
        else
            vectorResource(Res.drawable.eye_outline)

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = baseUrl,
            onValueChange = {
                viewModel.setBaseUrl(it)
            },
            label = { Text("Url") })
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            stringResource(Res.string.get_credentials_activity_header),
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = {
                viewModel.setUsername(it)
            },
            label = { Text("Username") })
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = {
                viewModel.setPassword(it)
            },
            label = { Text("Password") },
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisibility = !passwordVisibility
                }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (passwordVisibility) VisualTransformation.None
            else PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(20.dp))
            Button(onClick = {
                viewModel.setPassword("")
                viewModel.setUsername("")
            }) {
                Text("Reset")
            }
            Spacer(modifier = Modifier.weight(1f))
            if (dataState.isConfigComplete) {
                Button(onClick = {
                    viewModel.reload()
                    testDone = true
                }) {
                    Text("Test")
                }
                Spacer(modifier = Modifier.width(20.dp))
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!dataState.isConfigComplete) {
                Text("Please fill all config blabla")
            }
            if (testDone) {
                if (dataState.loadError) {
                    Text("Load error")
                } else if (dataState.loaded) {
                    Text("Load Ok")
                }
            }
        }
    }
}
