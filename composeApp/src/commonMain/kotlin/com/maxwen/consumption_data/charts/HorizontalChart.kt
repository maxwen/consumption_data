package com.maxwen.consumption_data.charts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.maxwen.consumption_data.models.MainViewModel
import com.maxwen.consumption_data.models.ChartDisplay
import com.maxwen.consumption_data.models.ConsumptionEntity
import com.maxwen.consumption_data.models.ConsumptionSelector
import com.maxwen.consumption_data.models.Period

@Composable
fun HorizontalChart(
    viewModel: MainViewModel,
    selector: ConsumptionSelector,
    consumptions: List<ConsumptionEntity>,
    years: List<String>,
    showYears: List<String>
) {
    val graphState by viewModel.graphState.collectAsState()

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

    if (graphState.display == ChartDisplay.Yearly) {
        HorizontalYearChart(yearChart)
    } else {
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
            viewModel,
            monthChart,
            yearChart.sortedYears(),
            ChartProperties.maxBarHeightMonthly
        )

        yearChart.sortedYears().forEach { year ->
            Column(modifier = Modifier
                .then(
                    if (graphState.focusPeriod.isNotEmpty() && graphState.focusPeriodPosition == 0 && year == Period(
                            graphState.focusPeriod
                        ).year()
                    )
                        Modifier.onGloballyPositioned { layoutCoordinates ->
                            viewModel.setFocusPeriodPosition(layoutCoordinates.positionInParent().y.toInt())
                        } else Modifier
                )) {
                HorizontalMonthChart(
                    viewModel,
                    monthChart,
                    listOf(year),
                    ChartProperties.maxBarHeightMonthly
                )
            }
        }
    }
}