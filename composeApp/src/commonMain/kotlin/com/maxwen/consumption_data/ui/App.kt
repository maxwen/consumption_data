package com.maxwen.consumption_data.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.maxwen.consumption_data.models.MainViewModel
import kotlin.math.min

@Composable
fun App(
    prefs: DataStore<Preferences>,
    viewModel: MainViewModel = viewModel { MainViewModel(prefs) },
    navController: NavHostController = rememberNavController(),
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = Screens.valueOf(
        backStackEntry?.destination?.route ?: Screens.BillingUnitsScreen.name
    )
    Scaffold(modifier = Modifier
        .fillMaxSize(), topBar = {
        AppBar(
            viewModel,
            navController,
            currentScreen = currentScreen,
            canNavigateBack = navController.previousBackStackEntry != null,
            navigateUp = { navController.navigateUp() }
        )
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.BillingUnitsScreen.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding(), start = 10.dp, end = 10.dp)
        ) {
            composable(route = Screens.BillingUnitsScreen.name) {
                BillingUnitsScreen(
                    viewModel,
                    navController,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
            composable(route = Screens.ConsumptionScreen.name) {
                val consumptions = viewModel.getConsumptionOfUnit(viewModel.getSelector())
                val years = viewModel.yearListOfConsumptionData(viewModel.getSelector())

                viewModel.setShowYears(
                    years.reversed().subList(0, min(MainViewModel.MAX_SHOW_YEARS, years.size))
                        .reversed()
                )
                ConsumptionScreen(
                    viewModel,
                    navController,
                    viewModel.getSelector(),
                    consumptions,
                    years,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
            composable(route = Screens.SettingsScreen.name) {
                SettingsScreen(
                    viewModel,
                    navController,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}