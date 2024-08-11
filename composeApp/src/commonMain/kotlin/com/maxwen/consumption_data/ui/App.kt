package com.maxwen.consumption_data.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
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

    val focusPeriod by viewModel.focusPeriod.collectAsState()

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
                BoxWithConstraints {
                    viewModel.isTwoPaneMode =
                        maxWidth > 800.dp /* TODO && getPlatform().type == PlatformType.Desktop*/
                    if (viewModel.isTwoPaneMode) {
                        val loaded by viewModel.loaded.collectAsState()
                        Row {
                            BillingUnitsScreen(
                                viewModel,
                                navController,
                                modifier = if (loaded) Modifier
                                    .fillMaxHeight()
                                    .weight(0.5F) else Modifier
                                    .fillMaxSize()
                            )
                            if (loaded) {
                                ConsumptionScreen(
                                    viewModel,
                                    navController,
                                    focusPeriod,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(0.5F)
                                        .padding(start = 10.dp, end = 10.dp)
                                )
                            }
                        }
                    } else {
                        BillingUnitsScreen(
                            viewModel,
                            navController,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }
            }
            composable(route = Screens.ConsumptionScreen.name) {
                ConsumptionScreen(
                    viewModel,
                    navController,
                    focusPeriod,
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
