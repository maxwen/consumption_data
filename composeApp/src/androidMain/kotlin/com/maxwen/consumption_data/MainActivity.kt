package com.maxwen.consumption_data

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.maxwen.consumption_data.models.ChartDisplay
import com.maxwen.consumption_data.models.ChartStyle
import com.maxwen.consumption_data.models.ConsumptionEntity
import com.maxwen.consumption_data.models.ConsumptionSelector
import com.maxwen.consumption_data.models.Period
import com.maxwen.consumption_data.models.Settings
import com.maxwen.consumption_data.charts.ChartConsumption
import com.maxwen.consumption_data.charts.ChartProperties
import com.maxwen.consumption_data.charts.HorizontalMonthChart
import com.maxwen.consumption_data.charts.HorizontalYearChart
import com.maxwen.consumption_data.charts.MonthChartData
import com.maxwen.consumption_data.charts.PopupBox
import com.maxwen.consumption_data.charts.VerticalMonthChart
import com.maxwen.consumption_data.charts.VerticalYearChart
import com.maxwen.consumption_data.charts.YearChartData
import org.openapitools.client.models.ServiceConfigurationBillingUnit
import consumption_data.composeapp.generated.resources.Res
import consumption_data.composeapp.generated.resources.avg_amount
import consumption_data.composeapp.generated.resources.billing_unit_screen
import consumption_data.composeapp.generated.resources.consumption_screen
import consumption_data.composeapp.generated.resources.eye_off_outline
import consumption_data.composeapp.generated.resources.eye_outline
import consumption_data.composeapp.generated.resources.heat_device
import consumption_data.composeapp.generated.resources.max_amount
import consumption_data.composeapp.generated.resources.min_amount
import consumption_data.composeapp.generated.resources.settings_screen
import consumption_data.composeapp.generated.resources.sum_amount
import consumption_data.composeapp.generated.resources.water_device
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.openapitools.client.models.Service
import ui.theme.AppTheme
import kotlin.math.min

enum class Screens(val title: StringResource) {
    BillingUnitsScreen(title = Res.string.billing_unit_screen),
    ConsumptionScreen(title = Res.string.consumption_screen),
    SettingsScreen(title = Res.string.settings_screen)
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
                            .padding(start = 10.dp, end = 10.dp)
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
                            .padding(start = 10.dp, end = 10.dp)
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
        title = { Text(text = stringResource(currentScreen.title)) },
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
    val isSetupDone by viewModel.isSetupDone.collectAsState()
    val progress by viewModel.progress.collectAsState()

    val squashResidentialUnits by viewModel.squashResidentialUnits.collectAsState()

    Column(
        modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isSetupDone) {
            SetupScreen(viewModel, navHostController, modifier)
        } else if (progress) {
            Progress()
        } else if (loaded) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                viewModel.getBillingUnits().forEach { billingUnit ->
                    val serviceStart = billingUnit.servicestart
                    val lastUpdate = billingUnit.lastperiod
                    if (serviceStart != null && lastUpdate != null) {

                        // TODO here we can limit to max years before
                        val periodStart =
                            Period.make((Period(lastUpdate).yearInt() - 1).toString(), 1).period

                        viewModel.getBillingUntitServices(billingUnit.reference.mscnumber)
                            .forEach { service ->
                                val unitOfMeasureSet =
                                    viewModel.getBillingUnitServicesUnitOfMeasure(
                                        billingUnit.reference.mscnumber,
                                        service
                                    )
                                unitOfMeasureSet.forEach { unitOfMeasure ->
                                    if (squashResidentialUnits) {
                                        val selector =
                                            ConsumptionSelector(
                                                billingUnit.reference,
                                                service,
                                                unitOfMeasure,
                                                null,
                                                periodStart = serviceStart
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
                                                        unitOfMeasure,
                                                        residentialUnit,
                                                        periodStart = serviceStart
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
                }
            }
        } else if (loadError) {
            LoadErrorScreen(viewModel, navHostController, modifier)
        } else {
            //back from setgtings without test
            viewModel.reload()
        }
    }
}

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

@Composable
fun Progress(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.weight(0.5f))
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@Composable
fun BillingUnitCardRow(
    key: String,
    value: String,
    modifier: Modifier = Modifier,
    value2: String? = null,
    icon: DrawableResource? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
    ) {
        if (value2 != null) {
            Row(modifier = Modifier.weight(0.6f), verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(imageVector = vectorResource(icon), contentDescription = null)
                    Spacer(modifier = Modifier.width(5.dp))
                }
                Text(key, style = MaterialTheme.typography.titleMedium)
            }
            Row(modifier = Modifier.weight(0.4f), verticalAlignment = Alignment.CenterVertically) {
                Text(text = value, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = value2, style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            Row(modifier = Modifier.weight(0.6f), verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(imageVector = vectorResource(icon), contentDescription = null)
                    Spacer(modifier = Modifier.width(5.dp))
                }
                Text(key, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = value, style = MaterialTheme.typography.bodyMedium)
            }
        }
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
    val service = selector.service

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Text(mscnumber, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.weight(1f))
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
                    Text(
                        modifier = Modifier.padding(start = 5.dp),
                        text = selector.service.toString() + " " + selector.unitOfMeasure.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            }

            BillingUnitCardRow("Start", (billingUnit.servicestart ?: ""))
            BillingUnitCardRow("Last", (billingUnit.lastperiod ?: ""))

            if (selector.residentialUnit != null) {
                BillingUnitCardRow("Residential", selector.residentialUnit.mscnumber)
            }

            Text("Consumption", style = MaterialTheme.typography.titleMedium)
            if (minConsumption != null) {
                BillingUnitCardRow(
                    "Min",
                    minConsumption.first,
                    value2 = minConsumption.second.toString(), icon = Res.drawable.min_amount
                )
            }
            if (maxConsumption != null) {
                BillingUnitCardRow(
                    "Max",
                    maxConsumption.first,
                    value2 = maxConsumption.second.toString(), icon = Res.drawable.max_amount
                )
            }
            BillingUnitCardRow("Average", avgConsumption.toString(), icon = Res.drawable.avg_amount)
            BillingUnitCardRow("Total", sumConsumption.toString(), icon = Res.drawable.sum_amount)
        }
    }
}

@Composable
fun VerticalChart(
    viewModel: MainViewModel,
    selector: ConsumptionSelector,
    consumptions: List<ConsumptionEntity>,
    years: List<String>,
    showYears: List<String>
) {
    val yearChart = YearChartData(
        service = selector.service,
        unitOfMeassure = consumptions.first().unitofmeasure
    )
    val chartDisplay by viewModel.chartDisplay.collectAsState()

    years.forEach { year ->
        if (showYears.contains(year)) {
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
    }
    if (chartDisplay == ChartDisplay.Yearly) {
        VerticalYearChart(yearChart)
    }

    if (chartDisplay == ChartDisplay.Monthly) {
        val monthChart = MonthChartData(
            service = selector.service,
            unitOfMeassure = consumptions.first().unitofmeasure
        )
        consumptions.forEach { consumption ->
            val period = Period(consumption.period)
            if (showYears.contains(period.year())) {
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
}

@Composable
fun HorizontalChart(
    viewModel: MainViewModel,
    selector: ConsumptionSelector,
    consumptions: List<ConsumptionEntity>,
    years: List<String>,
    showYears: List<String>
) {
    val chartDisplay by viewModel.chartDisplay.collectAsState()

    val yearChart = YearChartData(
        service = selector.service,
        unitOfMeassure = consumptions.first().unitofmeasure
    )

    years.forEach { year ->
        if (showYears.contains(year)) {
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
    }

    if (chartDisplay == ChartDisplay.Yearly) {
        HorizontalYearChart(yearChart)
    }

    if (chartDisplay == ChartDisplay.Monthly) {
        val monthChart = MonthChartData(
            service = selector.service,
            unitOfMeassure = consumptions.first().unitofmeasure
        )
        consumptions.forEach { consumption ->
            val period = Period(consumption.period)
            if (showYears.contains(period.year())) {
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
        }

        HorizontalMonthChart(
            monthChart,
            yearChart.sortedYears(),
            ChartProperties.maxBarHeightMonthly
        )

        yearChart.sortedYears().forEach { year ->
            HorizontalMonthChart(
                monthChart,
                listOf(year),
                ChartProperties.maxBarHeightMonthly
            )
        }
    }
}

@Composable
fun ConsumptionScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    selector: ConsumptionSelector,
    consumptions: List<ConsumptionEntity>,
    years: List<String>,
    modifier: Modifier = Modifier
) {
    val chartStyle by viewModel.chartStle.collectAsState()
    val chartDisplay by viewModel.chartDisplay.collectAsState()
    val showYears by viewModel.showYears.collectAsState()
    val showYearsCopy = mutableListOf<String>()
    showYearsCopy.addAll(showYears)
    var showYearPopup by rememberSaveable {
        mutableStateOf(false)
    }
    val yearColors = ChartProperties.yearColors

    Column(
        modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = modifier.fillMaxWidth()) {
            Button(
                shape = if (chartStyle == ChartStyle.Vertical) ButtonDefaults.shape else ButtonDefaults.filledTonalShape,
                colors = if (chartStyle == ChartStyle.Vertical) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
                elevation = if (chartStyle == ChartStyle.Vertical) ButtonDefaults.buttonElevation() else ButtonDefaults.filledTonalButtonElevation(),
                onClick = { viewModel.setChartStyle(ChartStyle.Vertical) }) {
                Text(
                    "Vertical",
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                shape = if (chartStyle == ChartStyle.Horizontal) ButtonDefaults.shape else ButtonDefaults.filledTonalShape,
                colors = if (chartStyle == ChartStyle.Horizontal) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
                elevation = if (chartStyle == ChartStyle.Horizontal) ButtonDefaults.buttonElevation() else ButtonDefaults.filledTonalButtonElevation(),
                onClick = { viewModel.setChartStyle(ChartStyle.Horizontal) }) {
                Text(
                    "Horizontal",
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(modifier = modifier.fillMaxWidth()) {
            Button(
                shape = if (chartDisplay == ChartDisplay.Yearly) ButtonDefaults.shape else ButtonDefaults.filledTonalShape,
                colors = if (chartDisplay == ChartDisplay.Yearly) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
                elevation = if (chartDisplay == ChartDisplay.Yearly) ButtonDefaults.buttonElevation() else ButtonDefaults.filledTonalButtonElevation(),
                onClick = { viewModel.setChartDisplay(ChartDisplay.Yearly) }) {
                Text(
                    "Yearly",
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                shape = if (chartDisplay == ChartDisplay.Monthly) ButtonDefaults.shape else ButtonDefaults.filledTonalShape,
                colors = if (chartDisplay == ChartDisplay.Monthly) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
                elevation = if (chartDisplay == ChartDisplay.Monthly) ButtonDefaults.buttonElevation() else ButtonDefaults.filledTonalButtonElevation(),
                onClick = { viewModel.setChartDisplay(ChartDisplay.Monthly) }) {
                Text(
                    "Monthly",
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        if (years.size > MainViewModel.MAX_SHOW_YEARS) {
            Text(
                text = "You can only show " + MainViewModel.MAX_SHOW_YEARS + " years at the same time",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(5.dp))
        }
        Row(modifier = modifier.fillMaxWidth()) {
            val yearsList = if (years.size > MainViewModel.MAX_SHOW_YEARS) showYearsCopy else years
            for (year in yearsList) {
                val inShowYEars = showYearsCopy.contains(year)
                val yearButtonColors = ButtonColors(
                    yearColors[year.toInt() % yearColors.size] /*uttonDefaults.buttonColors().containerColor*/,
                    MaterialTheme.colorScheme.onBackground,
                    ButtonDefaults.buttonColors().disabledContainerColor,
                    ButtonDefaults.buttonColors().disabledContentColor
                )

                Button(
                    contentPadding = PaddingValues(start = 15.dp, end = 15.dp),
                    shape = if (inShowYEars) ButtonDefaults.shape else ButtonDefaults.filledTonalShape,
                    colors = if (inShowYEars) yearButtonColors else ButtonDefaults.filledTonalButtonColors(),
                    elevation = if (inShowYEars) ButtonDefaults.buttonElevation() else ButtonDefaults.filledTonalButtonElevation(),
                    onClick = {
                        if (showYearsCopy.contains(year)) {
                            showYearsCopy.remove(year)
                        } else {
                            if (showYearsCopy.size < MainViewModel.MAX_SHOW_YEARS) {
                                showYearsCopy.add(year)
                                showYearsCopy.sort()
                            }
                        }
                        viewModel.setShowYears(showYearsCopy)
                    }) {
                    Text(
                        year,
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
            if (years.size > MainViewModel.MAX_SHOW_YEARS) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    contentPadding = PaddingValues(start = 15.dp, end = 15.dp),
                    shape = ButtonDefaults.filledTonalShape,
                    colors = ButtonDefaults.filledTonalButtonColors(),
                    elevation = ButtonDefaults.filledTonalButtonElevation(),
                    onClick = {
                        showYearPopup = true
                    }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null
                        )
                        Icon(
                            imageVector = if (showYearPopup) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                    PopupBox(
                        offset = IntOffset(
                            x = 0,
                            y = 0, /*LocalDensity.current.run { years.size * 30.dp.toPx() }.toInt()*/
                        ),
                        showPopup = showYearPopup,
                        onClickOutside = { showYearPopup = false },
                        content = {
                            Spacer(modifier = Modifier.height(5.dp))
                            for (year in years) {
                                val yearButtonColors = ButtonColors(
                                    yearColors[year.toInt() % yearColors.size] /*uttonDefaults.buttonColors().containerColor*/,
                                    MaterialTheme.colorScheme.onBackground,
                                    ButtonDefaults.buttonColors().disabledContainerColor,
                                    ButtonDefaults.buttonColors().disabledContentColor
                                )
                                val inShowYEars = showYearsCopy.contains(year)
                                Row(
                                    modifier = Modifier.padding(
                                        start = 20.dp,
                                        end = 20.dp,
                                        top = 2.dp,
                                        bottom = 2.dp
                                    )
                                ) {
                                    Button(
                                        contentPadding = PaddingValues(start = 15.dp, end = 15.dp),
                                        shape = if (inShowYEars) ButtonDefaults.shape else ButtonDefaults.filledTonalShape,
                                        colors = if (inShowYEars) yearButtonColors else ButtonDefaults.filledTonalButtonColors(),
                                        elevation = if (inShowYEars) ButtonDefaults.buttonElevation() else ButtonDefaults.filledTonalButtonElevation(),
                                        onClick = {
                                            if (showYearsCopy.contains(year)) {
                                                showYearsCopy.remove(year)
                                            } else {
                                                if (showYearsCopy.size < MainViewModel.MAX_SHOW_YEARS) {
                                                    showYearsCopy.add(year)
                                                    showYearsCopy.sort()
                                                }
                                            }
                                            viewModel.setShowYears(showYearsCopy)
                                        }) {
                                        Text(
                                            year,
                                        )
                                    }
                                }
                            }
//                            Spacer(modifier = Modifier.height(15.dp))
//                            Button(onClick = {
//                                showYearPopup = false
//                            }) {
//                                Text(text = "Close")
//                            }
                            Spacer(modifier = Modifier.height(5.dp))
                        })

                }
            }
        }

        if (showYears.isNotEmpty()) {
            ConsumptionYearsScreen(
                viewModel,
                navHostController,
                selector,
                consumptions,
                chartStyle,
                years,
                showYears
            )
        }
    }
}

@Composable
fun ConsumptionYearsScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    selector: ConsumptionSelector,
    consumptions: List<ConsumptionEntity>,
    chartStyle: ChartStyle,
    years: List<String>,
    showYears: List<String>,
    modifier: Modifier = Modifier
) {
    if (consumptions.isNotEmpty()) {
        if (chartStyle == ChartStyle.Horizontal) {
            HorizontalChart(viewModel, selector, consumptions, years, showYears)
        }
        if (chartStyle == ChartStyle.Vertical) {
            VerticalChart(viewModel, selector, consumptions, years, showYears)
        }
    }
}

