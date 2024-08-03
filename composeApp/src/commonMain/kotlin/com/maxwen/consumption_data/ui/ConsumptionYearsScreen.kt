package com.maxwen.consumption_data.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.maxwen.consumption_data.charts.HorizontalChart
import com.maxwen.consumption_data.charts.VerticalChart
import com.maxwen.consumption_data.models.ChartStyle
import com.maxwen.consumption_data.models.ConsumptionEntity
import com.maxwen.consumption_data.models.ConsumptionSelector
import com.maxwen.consumption_data.models.MainViewModel

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