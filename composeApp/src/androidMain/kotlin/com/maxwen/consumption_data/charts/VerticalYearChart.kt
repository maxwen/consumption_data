package com.maxwen.consumption_data.charts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun VerticalYearChart(
    yearChart: YearChartData,
    modifier: Modifier = Modifier
) {
    val yearColors = ChartProperties.yearColors

    val barWith = ChartProperties.maxBarWidthYearly
    val maxHeight = 300.dp
    val yearLabelHeight = 20.dp
    val gridMainLineProperties = gridMainLineProperties()
    val gridScaleineProperties = gridScaleLineProperties()

    val maxAmount = yearChart.maxAmount()
    val scaleUnit = yearChart.scaleUnit()
    val scaleUnitFraction = if (scaleUnit == 0.0) {
        0.0
    } else {
        scaleUnit / maxAmount
    }
    val scaleUnitHeight = Dp((maxHeight.value * scaleUnitFraction).toFloat())

    Column(
        modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {

//        Text(
//            "Years",
//            fontWeight = FontWeight.Bold,
//            fontSize = 18.sp,
//        )
        Text(text = yearChart.unitOfMeassure.toString(), modifier = Modifier.padding(top = 10.dp))

        Row(
            modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .drawWithContent {
                    drawLine(
                        strokeWidth = gridMainLineProperties.first,
                        color = gridMainLineProperties.second,
                        start = Offset(x = 0f, y = 0f),
                        end = Offset(x = 0f, y = size.height - yearLabelHeight.toPx()),
                    )

                    drawLine(
                        strokeWidth = gridMainLineProperties.first,
                        color = gridMainLineProperties.second,
                        start = Offset(x = 0f, y = size.height - yearLabelHeight.toPx()),
                        end = Offset(
                            x = size.width,
                            y = size.height - yearLabelHeight.toPx()
                        ),
                    )
                    var scaleUnitLine = size.height - yearLabelHeight.toPx()
                    while (scaleUnitLine - scaleUnitHeight.toPx() > 0) {
                        scaleUnitLine -= scaleUnitHeight.toPx()

                        drawLine(
                            strokeWidth = gridScaleineProperties.first,
                            color = gridScaleineProperties.second,
                            start = Offset(
                                x = 0f,
                                y = scaleUnitLine
                            ),
                            end = Offset(
                                x = size.width,
                                y = scaleUnitLine
                            ),
                            pathEffect = getScaleLinePathEffect()
                        )
                    }
                    this@drawWithContent.drawContent()
                }
        ) {
            Spacer(modifier = Modifier.weight(1.0f))
            yearChart.sortedYears().forEach { year ->
                val chartConsumption = yearChart.yearData(year)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row {
                        if (chartConsumption == null) {
                            VerticalBar(
                                "",
                                0.0,
                                0.0,
                                0.0,
                                yearColors[year.toInt() % yearColors.size],
                                barWith,
                                maxHeight,
                                false
                            )
                        } else {
                            VerticalBar(
                                chartConsumption.label,
                                chartConsumption.amount,
                                0.0,
                                maxAmount,
                                yearColors[year.toInt() % yearColors.size],
                                barWith,
                                maxHeight,
                                true
                            )
                        }
                    }
                    Text(
                        year,
                        modifier = Modifier.height(yearLabelHeight)
                    )
                }
                Spacer(modifier = Modifier.weight(1.0f))
            }
        }
    }
}