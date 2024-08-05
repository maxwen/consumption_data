package com.maxwen.consumption_data.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
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
import getPlatform
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
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
    val scrollBehavior =
        if (getPlatform().type != PlatformType.Desktop) TopAppBarDefaults.enterAlwaysScrollBehavior(
            rememberTopAppBarState()
        )
        else TopAppBarDefaults.pinnedScrollBehavior(
            rememberTopAppBarState()
        )


    println("displayCutout = " + WindowInsets.displayCutout.toString())

    val displayCutoutLeft = LocalDensity.current.run {
        WindowInsets.displayCutout.getLeft(
            LocalDensity.current,
            LayoutDirection.Ltr
        ).toDp()
    }

    val displayCutoutRight = LocalDensity.current.run {
        WindowInsets.displayCutout.getRight(
            LocalDensity.current,
            LayoutDirection.Ltr
        ).toDp()
    }

    Scaffold(modifier = Modifier
        .nestedScroll(scrollBehavior.nestedScrollConnection)
        .fillMaxSize(), topBar = {
        AppBar(
            viewModel,
            navController,
            currentScreen = currentScreen,
            canNavigateBack = navController.previousBackStackEntry != null,
            scrollBehavior,
            navigateUp = { navController.navigateUp() }
        )
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.BillingUnitsScreen.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = displayCutoutLeft + 10.dp,
                    end = displayCutoutRight + 10.dp,
                    /* we want edge to edge behind the navbar
                     on the sub layout we must add on the bottom
                    Spacer(
                        Modifier.windowInsetsBottomHeight(
                            WindowInsets.navigationBars
                        )
                    )*/
                    bottom = 0.dp,
                )
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