package com.maxwen.consumption_data.charts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
            viewModel,
            monthChart,
            yearChart.sortedYears(),
            ChartProperties.maxBarHeightMonthly
        )

        yearChart.sortedYears().forEach { year ->
            HorizontalMonthChart(
                viewModel,
                monthChart,
                listOf(year),
                ChartProperties.maxBarHeightMonthly
            )
        }
    }
}