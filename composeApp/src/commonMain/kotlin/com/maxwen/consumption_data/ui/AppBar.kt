package com.maxwen.consumption_data.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.navigation.NavHostController
import com.maxwen.consumption_data.models.ChartStyle
import com.maxwen.consumption_data.models.MainViewModel
import consumption_data.composeapp.generated.resources.Res
import consumption_data.composeapp.generated.resources.bar_chart
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    currentScreen: Screens,
    canNavigateBack: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chartStyle by viewModel.chartStyle.collectAsState()

    TopAppBar(
        title = { Text(text = stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
        actions = {
            if (currentScreen != Screens.SettingsScreen) {
                IconButton(onClick = { viewModel.reload() }) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Refresh"
                    )
                }


//                IconButton(onClick = {
//                    if (chartStyle == ChartStyle.Vertical) viewModel.setChartStyle(
//                        ChartStyle.Horizontal
//                    ) else viewModel.setChartStyle(ChartStyle.Vertical)
//                }) {
//                    if (chartStyle == ChartStyle.Horizontal) {
//                        Icon(
//                            imageVector = vectorResource(Res.drawable.bar_chart),
//                            contentDescription = "Horizontal"
//                        )
//                    } else {
//                        Icon(
//                            imageVector = vectorResource(Res.drawable.bar_chart),
//                            contentDescription = "Vertical",
//                            modifier = Modifier.rotate(90F)
//                        )
//                    }
//                }

                IconButton(onClick = { navHostController.navigate(Screens.SettingsScreen.name) }) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}