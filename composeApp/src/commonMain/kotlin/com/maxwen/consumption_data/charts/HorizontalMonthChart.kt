package com.maxwen.consumption_data.charts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxwen.consumption_data.models.Period
import com.maxwen.consumption_data.ui.PopupBox
import com.maxwen.consumption_data.ui.TextWithIcon
import consumption_data.composeapp.generated.resources.Res
import consumption_data.composeapp.generated.resources.avg_amount
import consumption_data.composeapp.generated.resources.max_amount
import consumption_data.composeapp.generated.resources.min_amount
import consumption_data.composeapp.generated.resources.sum_amount

@Composable
fun HorizontalMonthChart(
    monthChart: MonthChartData,
    years: List<String>,
    maxBarHeight: Dp,
    modifier: Modifier = Modifier
) {
    val yearColors = ChartProperties.yearColors
    val monthLabelWith = 40.dp
    val gridMainLineProperties = gridMainLineProperties()
    val gridScaleineProperties = gridScaleLineProperties()
    var showMultiMonthPopup by rememberSaveable {
        mutableIntStateOf(0)
    }
    BoxWithConstraints {
        val barWith = maxWidth - monthLabelWith

        Column(
            modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            if (years.size == 1) {
                Text(
                    years.joinToString(separator = ","),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }
            Row(
                modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    monthChart.unitOfMeassure.value,
                )
            }
            if (years.size == 1) {
                val year = years.first()
                Row(modifier = Modifier.padding(top = 10.dp)) {
                    TextWithIcon(monthChart.minAmount(year).toString(), Res.drawable.min_amount)
                    TextWithIcon(monthChart.maxAmount(year).toString(), Res.drawable.max_amount)
                    TextWithIcon(monthChart.sumAmount(year).toString(), Res.drawable.sum_amount)
                    TextWithIcon(monthChart.avgAmount(year).toString(), Res.drawable.avg_amount)
                }
            } else {
                Row(modifier = Modifier.padding(top = 10.dp)) {
                    TextWithIcon(monthChart.minAmountOfYears(years).toString(), Res.drawable.min_amount)
                    TextWithIcon(monthChart.maxAmountOfYears(years).toString(), Res.drawable.max_amount)
                    TextWithIcon(monthChart.sumAmountOfYears(years).toString(), Res.drawable.sum_amount)
                    TextWithIcon(monthChart.avgAmountOfYears(years).toString(), Res.drawable.avg_amount)
                }
            }

            val maxAmount = monthChart.maxAmountOfAllYears()
            val barHeight = (maxBarHeight / years.size)
            val scaleUnit = monthChart.scaleUnit()
            val scaleUnitFraction = if (scaleUnit == 0.0) {
                0.0
            } else {
                scaleUnit / maxAmount
            }
            val scaleUnitWith = Dp((barWith.value * scaleUnitFraction).toFloat())

            Column(
                modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .drawWithContent {
                        drawLine(
                            strokeWidth = gridMainLineProperties.first,
                            color = gridMainLineProperties.second,
                            start = Offset(x = monthLabelWith.toPx(), y = 0f),
                            end = Offset(x = monthLabelWith.toPx(), y = size.height),
                        )
                        var scaleUnitLine = monthLabelWith.toPx()
                        while (scaleUnitLine + scaleUnitWith.toPx() < size.width) {
                            scaleUnitLine += scaleUnitWith.toPx()

                            drawLine(
                                strokeWidth = gridScaleineProperties.first,
                                color = gridScaleineProperties.second,
                                start = Offset(
                                    x = scaleUnitLine,
                                    y = 0f
                                ),
                                end = Offset(
                                    x = scaleUnitLine,
                                    y = size.height
                                ),
                                pathEffect = gridScaleLinePathEffect()
                            )
                        }
                        drawLine(
                            strokeWidth = gridMainLineProperties.first,
                            color = gridMainLineProperties.second,
                            start = Offset(x = monthLabelWith.toPx(), y = size.height),
                            end = Offset(
                                x = size.width,
                                y = size.height
                            ),
                        )
                        this@drawWithContent.drawContent()
                    }
            ) {
                for (month in 1..12) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box {
                            Text(
                                Period.month(month),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .width(monthLabelWith)
                                    .clickable {
                                        if (years.size > 1) {
                                            if (showMultiMonthPopup != 0) {
                                                showMultiMonthPopup = 0
                                            }
                                            showMultiMonthPopup = month
                                        }
                                    }
                            )
                            PopupBox(
                                offset = IntOffset(x = LocalDensity.current.run { 60.dp.toPx() }
                                    .toInt(), y = 0),
                                showPopup = showMultiMonthPopup == month,
                                onClickOutside = { showMultiMonthPopup = 0 },
                                content = {
                                    Spacer(modifier = Modifier.height(5.dp))
                                    for (year in years) {
                                        val chartConsumption =
                                            monthChart.monthConsumption(Period.make(year, month))
                                        Row(
                                            modifier = Modifier.padding(
                                                start = 15.dp,
                                                end = 15.dp,
                                                top = 5.dp,
                                                bottom = 5.dp
                                            )
                                        ) {
                                            Text(
                                                chartConsumption?.amount?.toString() ?: "0.0",
                                                color = yearColors[year.toInt() % yearColors.size],
                                            )
                                        }
                                    }
                                })
                        }
                        Column {
                            years.forEach { year ->
                                val chartConsumption =
                                    monthChart.monthConsumption(Period.make(year, month))
                                if (chartConsumption == null) {
                                    HorizontalBar(
                                        "",
                                        0.0,
                                        0.0,
                                        0.0,
                                        yearColors[year.toInt() % yearColors.size],
                                        barHeight,
                                        barWith,
                                        false
                                    )
                                } else {
                                    HorizontalBar(
                                        chartConsumption.label,
                                        chartConsumption.amount,
                                        0.0,
                                        maxAmount,
                                        yearColors[year.toInt() % yearColors.size],
                                        barHeight,
                                        barWith,
                                        years.size == 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}