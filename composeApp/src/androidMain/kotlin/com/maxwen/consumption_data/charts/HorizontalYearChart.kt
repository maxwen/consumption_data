package com.maxwen.consumption_data.charts

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import consumption_data.composeapp.generated.resources.Res
import consumption_data.composeapp.generated.resources.avg_amount
import consumption_data.composeapp.generated.resources.max_amount
import consumption_data.composeapp.generated.resources.min_amount
import consumption_data.composeapp.generated.resources.sum_amount

@Composable
fun HorizontalYearChart(
    yearChart: YearChartData,
    modifier: Modifier = Modifier
) {
    val yearColors = ChartProperties.yearColors
    val barHeight = ChartProperties.maxBarHeightYearly
    val yearLabelWith = 45.dp
    val gridMainLineProperties = gridMainLineProperties()
    val gridScaleineProperties = gridScaleLineProperties()

    BoxWithConstraints {
        val barWith = maxWidth - yearLabelWith
        val maxAmount = yearChart.maxAmount()
        val scaleUnit = yearChart.scaleUnit()
        val scaleUnitFraction = if (scaleUnit == 0.0) {
            0.0
        } else {
            scaleUnit / maxAmount
        }
        val scaleUnitWith = Dp((barWith.value * scaleUnitFraction).toFloat())

        Column(
            modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
//            Text(
//                "Years",
//                fontWeight = FontWeight.Bold,
//                fontSize = 18.sp,
//            )
            Row(
                modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    yearChart.unitOfMeassure.value,
                )
            }
            Row(modifier = Modifier.padding(top = 10.dp)) {
                TextWithIcon(yearChart.minAmount().toString(), Res.drawable.min_amount)
                TextWithIcon(yearChart.maxAmount().toString(), Res.drawable.max_amount)
                TextWithIcon(yearChart.sumAmount().toString(), Res.drawable.sum_amount)
                TextWithIcon(yearChart.avgAmount().toString(), Res.drawable.avg_amount)
            }

            Column(
                modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .drawWithContent {
                        drawLine(
                            strokeWidth = gridMainLineProperties.first,
                            color = gridMainLineProperties.second,
                            start = Offset(x = yearLabelWith.toPx(), y = 0f),
                            end = Offset(x = yearLabelWith.toPx(), y = size.height),
                        )
                        var scaleUnitLine = yearLabelWith.toPx()
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
                            start = Offset(x = yearLabelWith.toPx(), y = size.height),
                            end = Offset(
                                x = size.width,
                                y = size.height
                            ),
                        )
                        this@drawWithContent.drawContent()
                    }
            ) {
                yearChart.sortedYears().forEach { year ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            year,
                            modifier = Modifier
                                .width(yearLabelWith)
                        )
                        val chartConsumption = yearChart.yearData(year)
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
                                true
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}