package com.maxwen.consumption_data

import MainViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
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
import com.maxwen.consumption_data.charts.MonthChart
import com.maxwen.consumption_data.charts.YearChart
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.openapitools.client.models.ServiceConfigurationBillingUnit
import createDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

enum class Screens() {
    BillingUnitsScreen,
    ConsumptionScreen,
    SettingsScreen
}

class MainActivity() : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
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
        Scaffold(topBar = {
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
    Column(modifier) {
        val baseUrl by viewModel.baseurl.collectAsState()
        val username by viewModel.username.collectAsState()
        val password by viewModel.password.collectAsState()

        var passwordVisibility by remember { mutableStateOf(false) }

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
                        imageVector = Icons.Outlined.AddCircle,
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
        }
        if (loadError) {
            LoadErrorScreen(viewModel, modifier)
        }
    }
}

@Composable
fun LoadErrorScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    Text(
        "Load error", fontWeight = FontWeight.Bold, fontSize = 18.sp
    )
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
            Text("Last period " + (billingUnit.lastperiod ?: ""))
            Text(selector.service.toString())
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
fun HorizontalBar(
    label: String,
    amount: Double,
    minAmount: Double,
    maxAmount: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val offset = 50.dp
    val screenRange = screenWidth - offset
    val amountRange = maxAmount - minAmount
    val amountFraction = if (amount == 0.0) {
        0.0
    } else {
        (amount - minAmount) / amountRange
    }
    val screenFraction = offset + Dp((screenRange.value * amountFraction).toFloat())

    Row(
        modifier
            .width(screenFraction)
            .height(40.dp)
            .clip(RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp))
            .padding(5.dp)
            .background(
                color,
                shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, modifier = Modifier.padding(start = 5.dp))
        Spacer(modifier = Modifier.weight(1.0F))
        if (amount != 0.0) {
            Text(amount.toString(), color = Color.White, modifier = Modifier.padding(end = 5.dp))
        }
    }
}

@Composable
fun VerticalBar(
    label: String,
    amount: Double,
    minAmount: Double,
    maxAmount: Double,
    color: Color,
    maxWith: Dp,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val chartHeight = 300.dp
    val screenWidth = configuration.screenWidthDp.dp

    val offset = 50.dp
    val screenRange = chartHeight - offset
    val amountRange = maxAmount - minAmount
    val amountFraction = if (amount == 0.0) {
        0.0
    } else {
        (amount - minAmount) / amountRange
    }
    val screenFraction = offset + Dp((screenRange.value * amountFraction).toFloat())

    Column(
        modifier
            .width(maxWith)
            .height(chartHeight),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1.0F))
        if (amount != 0.0) {
            Text(amount.toString(), color = Color.Black, modifier = Modifier.padding(top = 5.dp))
        }
        Column(
            modifier
                .width(maxWith)
                .height(screenFraction)
                .padding(5.dp)
                .clip(RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp))
                .background(
                    color,
                    shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1.0F))
            Text(label, color = Color.White, modifier = Modifier.padding(bottom = 5.dp))
        }
    }
}

@Composable
fun HorizontalChart(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    selector: ConsumptionSelector,
    consumptions: List<ConsumptionEntity>,
    modifier: Modifier = Modifier
) {
    Text(
        "Years", fontWeight = FontWeight.Bold, fontSize = 18.sp
    )
    val years = viewModel.yearListOfConsumptionData(selector)
    val yearChart = YearChart()
    years.forEach { year ->
        val sum = viewModel.yearSumConsumptionOfUnit(selector, year)
        val chartConsumption = ChartConsumption(
            sum ?: 0.0,
            year,
            year,
            selector.service,
            selector.billingUnit,
            selector.residentialUnit
        )

        yearChart.addConsumption(
            year, chartConsumption
        )
    }

    Column(
        modifier
            .fillMaxWidth()
    ) {
        yearChart.sortedYears().forEach { year ->
            val chartConsumption = yearChart.yearData(year)
            if (chartConsumption == null) {
                HorizontalBar(
                    "",
                    0.0,
                    0.0,
                    0.0,
                    Color(25, 170, 209)
                )
            } else {
                HorizontalBar(
                    chartConsumption.label,
                    chartConsumption.amount,
                    0.0,
                    yearChart.maxAmount(),
                    Color(25, 170, 209)
                )
            }
        }
    }

    val monthChart = MonthChart()
    consumptions.forEach { consumption ->
        val period = Period(consumption.period)
        monthChart.addConsumption(
            period.year(), period.month(), ChartConsumption(
                consumption.amount ?: 0.0,
                period.month(),
                consumption.period,
                selector.service,
                selector.billingUnit,
                selector.residentialUnit
            )
        )
    }

    years.forEach { year ->
        Text(
            year, fontWeight = FontWeight.Bold, fontSize = 18.sp
        )
        Column(
            modifier
                .fillMaxWidth()
        ) {
//            val minAmount = monthChart.minAmount()
            val maxAmount = monthChart.maxAmount()
            var month = 1
            monthChart.yearData(year).forEach { chartConsumption ->
                if (chartConsumption == null) {
                    HorizontalBar(
                        "",
                        0.0,
                        0.0,
                        0.0,
                        Color(209, 25, 25),
                    )
                    month += 1
                } else {
                    val monthInt = Period(chartConsumption.period).month().toInt()
                    while (monthInt != month) {
                        HorizontalBar(
                            Period.make(year, month).month(),
                            0.0,
                            0.0,
                            0.0,
                            Color(209, 25, 25),
                        )
                        month += 1
                    }
                    HorizontalBar(
                        chartConsumption.label,
                        chartConsumption.amount,
                        0.0,
                        maxAmount,
                        Color(209, 25, 25)
                    )
                    month += 1
                }
            }
        }
    }
}

@Composable
fun VerticalChart(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    selector: ConsumptionSelector,
    consumptions: List<ConsumptionEntity>,
    modifier: Modifier = Modifier
) {
    Text(
        "Years", fontWeight = FontWeight.Bold, fontSize = 18.sp
    )
    val years = viewModel.yearListOfConsumptionData(selector)
    val yearChart = YearChart()
    years.forEach { year ->
        val sum = viewModel.yearSumConsumptionOfUnit(selector, year)
        val chartConsumption = ChartConsumption(
            sum ?: 0.0,
            year,
            year,
            selector.service,
            selector.billingUnit,
            selector.residentialUnit
        )

        yearChart.addConsumption(
            year, chartConsumption
        )
    }


    BoxWithConstraints(
        modifier
            .fillMaxWidth()
    ) {
        val barWith = maxWidth / years.size
        Row(
            modifier
                .fillMaxWidth()
        ) {
            yearChart.sortedYears().forEach { year ->
                val chartConsumption = yearChart.yearData(year)
                if (chartConsumption == null) {
                    VerticalBar(
                        "",
                        0.0,
                        0.0,
                        0.0,
                        Color(25, 170, 209),
                        barWith
                    )
                } else {
                    VerticalBar(
                        chartConsumption.label,
                        chartConsumption.amount,
                        0.0,
                        yearChart.maxAmount(),
                        Color(25, 170, 209),
                        barWith
                    )
                }
            }
        }
    }

    val monthChart = MonthChart()
    consumptions.forEach { consumption ->
        val period = Period(consumption.period)
        monthChart.addConsumption(
            period.year(), period.month(), ChartConsumption(
                consumption.amount ?: 0.0,
                period.month(),
                consumption.period,
                selector.service,
                selector.billingUnit,
                selector.residentialUnit
            )
        )
    }
    years.forEach { year ->
        Text(
            year, fontWeight = FontWeight.Bold, fontSize = 18.sp
        )
        BoxWithConstraints(
            modifier
                .fillMaxWidth()
        ) {
            val barWith = maxWidth / 12
            Row(
                modifier
                    .fillMaxWidth()
            ) {
                val maxAmount = monthChart.maxAmount()
                var month = 1
                monthChart.yearData(year).forEach { chartConsumption ->
                    if (chartConsumption == null) {
                        VerticalBar(
                            "",
                            0.0,
                            0.0,
                            0.0,
                            Color(209, 25, 25),
                            barWith
                        )
                        month += 1
                    } else {
                        val monthInt = Period(chartConsumption.period).month().toInt()
                        while (monthInt != month) {
                            VerticalBar(
                                Period.make(year, month).month(),
                                0.0,
                                0.0,
                                0.0,
                                Color(209, 25, 25),
                                barWith
                            )
                            month += 1
                        }
                        VerticalBar(
                            chartConsumption.label,
                            chartConsumption.amount,
                            0.0,
                            maxAmount,
                            Color(209, 25, 25),
                            barWith
                        )
                        month += 1
                    }
                }
            }
        }
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
        HorizontalChart(viewModel, navHostController, selector, consumptions)
        VerticalChart(viewModel, navHostController, selector, consumptions)
    }
}
