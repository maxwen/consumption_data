package com.maxwen.consumption_data.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.maxwen.consumption_data.charts.ChartProperties
import com.maxwen.consumption_data.models.ChartDisplay
import com.maxwen.consumption_data.models.ChartStyle
import com.maxwen.consumption_data.models.ConsumptionEntity
import com.maxwen.consumption_data.models.ConsumptionSelector
import com.maxwen.consumption_data.models.MainViewModel
import consumption_data.composeapp.generated.resources.Res
import consumption_data.composeapp.generated.resources.bar_chart
import consumption_data.composeapp.generated.resources.month_display
import consumption_data.composeapp.generated.resources.year_display
import org.jetbrains.compose.resources.vectorResource
import kotlin.math.min

@Composable
fun ConsumptionScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val selector by viewModel.selector.collectAsState()
    val consumptions = viewModel.getConsumptionOfUnit(selector)
    val years = viewModel.yearListOfConsumptionData(selector)

    if (!viewModel.isShowYearsInit && years.isNotEmpty()) {
        viewModel.setShowYears(
            years.reversed()
                .subList(0, min(MainViewModel.MAX_SHOW_YEARS, years.size))
                .reversed()
        )
        viewModel.isShowYearsInit = true
    }

    val graphState by viewModel.graphState.collectAsState()

    val showYearsCopy = mutableListOf<String>()
    showYearsCopy.addAll(graphState.showYears)
    var showYearPopup by rememberSaveable {
        mutableStateOf(false)
    }
    val yearColors = ChartProperties.yearColors

    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = graphState.focusPeriodPosition) {
            scrollState.animateScrollTo(graphState.focusPeriodPosition)
    }

    Column(
        modifier
            .fillMaxHeight()
            .verticalScroll(state = scrollState)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
//        Row(modifier = modifier.fillMaxWidth()) {
//            Button(
//                contentPadding = PaddingValues(start = 15.dp, end = 15.dp),
//                shape = if (chartStyle == ChartStyle.Vertical) ButtonDefaults.shape else ButtonDefaults.filledTonalShape,
//                colors = if (chartStyle == ChartStyle.Vertical) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
//                elevation = if (chartStyle == ChartStyle.Vertical) ButtonDefaults.buttonElevation() else ButtonDefaults.filledTonalButtonElevation(),
//                onClick = { viewModel.setChartStyle(ChartStyle.Vertical) }) {
//                Icon(
//                    imageVector = vectorResource(Res.drawable.bar_chart),
//                    contentDescription = null
//                )
//            }
//            Spacer(modifier = Modifier.width(10.dp))
//            Button(
//                contentPadding = PaddingValues(start = 15.dp, end = 15.dp),
//                shape = if (chartStyle == ChartStyle.Horizontal) ButtonDefaults.shape else ButtonDefaults.filledTonalShape,
//                colors = if (chartStyle == ChartStyle.Horizontal) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
//                elevation = if (chartStyle == ChartStyle.Horizontal) ButtonDefaults.buttonElevation() else ButtonDefaults.filledTonalButtonElevation(),
//                onClick = { viewModel.setChartStyle(ChartStyle.Horizontal) }) {
//                Icon(
//                    imageVector = vectorResource(Res.drawable.bar_chart),
//                    contentDescription = null,
//                    modifier = Modifier.rotate(90F)
//                )
//            }
//        }
//        Spacer(modifier = Modifier.height(5.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                contentPadding = PaddingValues(start = 15.dp, end = 15.dp),
                shape = if (graphState.display == ChartDisplay.Yearly) ButtonDefaults.shape else ButtonDefaults.filledTonalShape,
                colors = if (graphState.display == ChartDisplay.Yearly) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
                elevation = if (graphState.display == ChartDisplay.Yearly) ButtonDefaults.buttonElevation() else ButtonDefaults.filledTonalButtonElevation(),
                onClick = { viewModel.setChartDisplay(ChartDisplay.Yearly) }) {
//                Text(
//                    "Yearly",
//                )
                Icon(
                    imageVector = vectorResource(Res.drawable.year_display),
                    contentDescription = "Yearly"
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                contentPadding = PaddingValues(start = 15.dp, end = 15.dp),
                shape = if (graphState.display == ChartDisplay.Monthly) ButtonDefaults.shape else ButtonDefaults.filledTonalShape,
                colors = if (graphState.display == ChartDisplay.Monthly) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
                elevation = if (graphState.display == ChartDisplay.Monthly) ButtonDefaults.buttonElevation() else ButtonDefaults.filledTonalButtonElevation(),
                onClick = { viewModel.setChartDisplay(ChartDisplay.Monthly) }) {
//                Text(
//                    "Monthly",
//                )
                Icon(
                    imageVector = vectorResource(Res.drawable.month_display),
                    contentDescription = "Monthly"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                contentPadding = PaddingValues(start = 15.dp, end = 15.dp),
                shape = if (graphState.style == ChartStyle.Vertical) ButtonDefaults.shape else ButtonDefaults.filledTonalShape,
                colors = if (graphState.style == ChartStyle.Vertical) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
                elevation = if (graphState.style == ChartStyle.Vertical) ButtonDefaults.buttonElevation() else ButtonDefaults.filledTonalButtonElevation(),
                onClick = { viewModel.setChartStyle(ChartStyle.Vertical) }) {
                Icon(
                    imageVector = vectorResource(Res.drawable.bar_chart),
                    contentDescription = "Vertical"
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                contentPadding = PaddingValues(start = 15.dp, end = 15.dp),
                shape = if (graphState.style == ChartStyle.Horizontal) ButtonDefaults.shape else ButtonDefaults.filledTonalShape,
                colors = if (graphState.style == ChartStyle.Horizontal) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
                elevation = if (graphState.style == ChartStyle.Horizontal) ButtonDefaults.buttonElevation() else ButtonDefaults.filledTonalButtonElevation(),
                onClick = { viewModel.setChartStyle(ChartStyle.Horizontal) }) {
                Icon(
                    imageVector = vectorResource(Res.drawable.bar_chart),
                    contentDescription = "Horizontal",
                    modifier = Modifier.rotate(90F)
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
        Row(modifier = Modifier.fillMaxWidth()) {
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
                        color = Color.White
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

        if (graphState.showYears.isNotEmpty()) {
            ConsumptionYearsScreen(
                viewModel,
                navHostController,
                selector,
                consumptions,
                graphState.style,
                years,
                graphState.showYears,
            )
        }
        Spacer(
            Modifier.windowInsetsBottomHeight(
                WindowInsets.navigationBars
            )
        )
    }
}