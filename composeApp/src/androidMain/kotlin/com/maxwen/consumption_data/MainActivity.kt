package com.maxwen.consumption_data

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.maxwen.consumption.models.ConsumptionEntity
import com.maxwen.consumption.models.ConsumptionSelector
import com.maxwen.consumption.models.Period
import com.maxwen.consumption.models.Settings
import com.maxwen.consumption_data.charts.ChartConsumption
import com.maxwen.consumption_data.charts.HorizontalMonthChart
import com.maxwen.consumption_data.charts.HorizontalYearChart
import com.maxwen.consumption_data.charts.MonthChartData
import com.maxwen.consumption_data.charts.VerticalMonthChart
import com.maxwen.consumption_data.charts.VerticalYearChart
import com.maxwen.consumption_data.charts.YearChartData
import org.openapitools.client.models.ServiceConfigurationBillingUnit
import createDataStore
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.eye_off_outline
import kotlinproject.composeapp.generated.resources.eye_outline
import kotlinproject.composeapp.generated.resources.heat_device
import kotlinproject.composeapp.generated.resources.water_device
import org.jetbrains.compose.resources.vectorResource
import org.openapitools.client.models.Service
import ui.theme.AppTheme

enum class Screens() {
    BillingUnitsScreen,
    ConsumptionScreen,
    SettingsScreen
}

class MainActivity() : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

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

    @Composable
    fun App(
        navController: NavHostController = rememberNavController(),
        prefs: DataStore<Preferences>
    ) {
        Settings.myDataStore = prefs

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
                    .padding(innerPadding)
            ) {
                composable(route = Screens.BillingUnitsScreen.name) {
                    BillingUnitsScreen(
                        viewModel,
                        navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    )
                }
                composable(route = Screens.ConsumptionScreen.name) {
                    ConsumptionScreen(
                        viewModel,
                        navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    )
                }
                composable(route = Screens.SettingsScreen.name) {
                    SettingsScreen(
                        viewModel,
                        navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    currentScreen: Screens,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text("") },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
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
            if (currentScreen == Screens.BillingUnitsScreen) {
                IconButton(onClick = { viewModel.reload() }) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }
            if (currentScreen != Screens.SettingsScreen) {
                IconButton(onClick = { navHostController.navigate(Screens.SettingsScreen.name) }) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
        }
    )
}

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        val baseUrl by viewModel.baseurl.collectAsState()
        val username by viewModel.username.collectAsState()
        val password by viewModel.password.collectAsState()
        val loaded by viewModel.loaded.collectAsState()
        val loadError by viewModel.loadError.collectAsState()
        val isConfigComplete by viewModel.isConfigComplete.collectAsState()
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
        if (!isConfigComplete) {
            Text("Please fill all config blabla", modifier = Modifier.padding(top = 10.dp))
        } else {
            Button(modifier = Modifier.padding(top = 10.dp), onClick = {
                viewModel.reload()
                testDone = true
            }) {
                Text("Test")
            }
            if (testDone) {
                if (loadError) {
                    Text("Load error", modifier = Modifier.padding(top = 10.dp))
                } else if (loaded) {
                    Text("Load Ok", modifier = Modifier.padding(top = 10.dp))
                }
            }
        }
    }

}

@Composable
fun BillingUnitsScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val loaded by viewModel.loaded.collectAsState()
    val loadError by viewModel.loadError.collectAsState()
    val squashResidentialUnits by viewModel.squashResidentialUnits.collectAsState()

    Column(
        modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (loaded) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                viewModel.getBillingUnits().forEach { billingUnit ->
                    viewModel.getBillingUntitServices(billingUnit.reference.mscnumber)
                        .forEach { service ->
                            if (squashResidentialUnits) {
                                val selector =
                                    ConsumptionSelector(
                                        billingUnit.reference,
                                        service,
                                        null
                                    )

                                BillingUnitCard(
                                    viewModel,
                                    navHostController,
                                    billingUnit,
                                    selector
                                )
                            } else {
                                viewModel.getBillingUntitResidentialUnits(billingUnit.reference.mscnumber)
                                    .forEach { residentialUnit ->
                                        val selector =
                                            ConsumptionSelector(
                                                billingUnit.reference,
                                                service,
                                                residentialUnit
                                            )

                                        BillingUnitCard(
                                            viewModel,
                                            navHostController,
                                            billingUnit,
                                            selector
                                        )
                                    }
                            }
                        }
                }
            }
        } else if (loadError) {
            if (!viewModel.isSetupDone()) {
                SetupScreen(viewModel, navHostController, modifier)
            } else {
                LoadErrorScreen(viewModel, navHostController, modifier)
            }
        }
    }
}

@Composable
fun LoadErrorScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
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


@Composable
fun SetupScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
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

@Composable
fun BillingUnitCard(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    billingUnit: ServiceConfigurationBillingUnit,
    selector: ConsumptionSelector,
    modifier: Modifier = Modifier
) {
    val mscnumber = billingUnit.reference.mscnumber
    val minConsumption =
        viewModel.minConsumptionOfUnit(selector);
    val maxConsumption =
        viewModel.maxConsumptionOfUnit(selector);
    val avgConsumption =
        viewModel.avgConsumptionOfUnit(selector);
    val sumConsumption =
        viewModel.sumConsumptionOfUnit(selector);

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(15.dp),
        onClick = {
            viewModel.setSelector(selector)
            navHostController.navigate(Screens.ConsumptionScreen.name)
        }
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text("Billing unit $mscnumber")
            Text("Start period " + (billingUnit.servicestart ?: ""))
            Text("Last period " + (billingUnit.lastperiod ?: ""))
            val service = selector.service
            Row(verticalAlignment = Alignment.CenterVertically) {
                when (service) {
                    Service.HEATING -> {
                        Icon(
                            imageVector = vectorResource(Res.drawable.heat_device),
                            contentDescription = null
                        )
                    }

                    Service.HOT_WATER -> {
                        Icon(
                            imageVector = vectorResource(Res.drawable.water_device),
                            contentDescription = null
                        )
                    }

                    Service.COOLING -> TODO()
                    Service.COLD_WATER -> TODO()
                }
                Text(modifier = Modifier.padding(start = 5.dp), text = selector.service.toString())
            }
            if (selector.residentialUnit != null) {
                Text("Residential unit " + selector.residentialUnit.mscnumber)
            }

            if (minConsumption != null) {
                Text("Min " + minConsumption.first + " " + minConsumption.second)
            }
            if (maxConsumption != null) {
                Text("Max " + maxConsumption.first + " " + maxConsumption.second)
            }
            Text("Avg $avgConsumption")
            Text("Sum $sumConsumption")
        }
    }
}

@Composable
fun VerticalChart(
    viewModel: MainViewModel,
    selector: ConsumptionSelector,
    consumptions: List<ConsumptionEntity>,
) {
    val years = viewModel.yearListOfConsumptionData(selector)
    val yearChart = YearChartData(service = selector.service, unitOfMeassure = consumptions.first().unitofmeasure)
    years.forEach { year ->
        val sum = viewModel.yearSumConsumptionOfUnit(selector, year)
        val chartConsumption = ChartConsumption(
            sum ?: 0.0,
            year,
            year,
            selector.service,
            consumptions.first().unitofmeasure,
            selector.billingUnit,
            selector.residentialUnit
        )

        yearChart.addConsumption(
            year, chartConsumption
        )
    }

    VerticalYearChart(yearChart)

    val monthChart = MonthChartData(service = selector.service, unitOfMeassure = consumptions.first().unitofmeasure)
    consumptions.forEach { consumption ->
        val period = Period(consumption.period)
        monthChart.addConsumption(
            period.year(), period.month(), ChartConsumption(
                consumption.amount ?: 0.0,
                period.month(),
                consumption.period,
                selector.service,
                consumption.unitofmeasure,
                selector.billingUnit,
                selector.residentialUnit
            )
        )
    }
    VerticalMonthChart(
        monthChart,
        yearChart.sortedYears()
    )

    yearChart.sortedYears().forEach { year ->
        VerticalMonthChart(
            monthChart,
            listOf(year)
        )
    }
}

@Composable
fun HorizontalChart(
    viewModel: MainViewModel,
    selector: ConsumptionSelector,
    consumptions: List<ConsumptionEntity>,
) {
    val years = viewModel.yearListOfConsumptionData(selector)
    val yearChart = YearChartData(service = selector.service, unitOfMeassure = consumptions.first().unitofmeasure)
    years.forEach { year ->
        val sum = viewModel.yearSumConsumptionOfUnit(selector, year)
        val chartConsumption = ChartConsumption(
            sum ?: 0.0,
            year,
            year,
            selector.service,
            consumptions.first().unitofmeasure,
            selector.billingUnit,
            selector.residentialUnit
        )

        yearChart.addConsumption(
            year, chartConsumption
        )
    }

    HorizontalYearChart(yearChart)

    val monthChart = MonthChartData(service = selector.service, unitOfMeassure = consumptions.first().unitofmeasure)
    consumptions.forEach { consumption ->
        val period = Period(consumption.period)
        monthChart.addConsumption(
            period.year(), period.month(), ChartConsumption(
                consumption.amount ?: 0.0,
                period.month(),
                consumption.period,
                selector.service,
                consumption.unitofmeasure,
                selector.billingUnit,
                selector.residentialUnit
            )
        )
    }

    HorizontalMonthChart(monthChart, yearChart.sortedYears(), 30.dp)

    yearChart.sortedYears().forEach { year ->
        HorizontalMonthChart(
            monthChart,
            listOf(year),
            30.dp
        )
    }
}

@Composable
fun ConsumptionScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val selector by viewModel.selector.collectAsState()
    val consumptions = viewModel.getConsumptionOfUnit(selector)

    Column(
        modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        HorizontalChart(viewModel, selector, consumptions)
        VerticalChart(viewModel, selector, consumptions)
    }
}
