package com.maxwen.consumption_data.charts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxwen.consumption_data.models.Period

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
            val maxAmount = monthChart.maxAmount()
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
                                pathEffect = getScaleLinePathEffect()
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            Period.month(month),
                            modifier = Modifier.width(monthLabelWith)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
        }
    }
}