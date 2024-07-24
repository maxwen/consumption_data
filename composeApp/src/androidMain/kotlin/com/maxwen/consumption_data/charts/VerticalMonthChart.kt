package com.maxwen.consumption_data.charts

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
import com.maxwen.consumption_data.models.Period

@Composable
fun VerticalMonthChart(
    monthChart: MonthChartData,
    years: List<String>,
    modifier: Modifier = Modifier
) {
    val yearColors = ChartProperties.yearColors
    val montLabelHeight = 20.dp
    val maxHeight = 300.dp
    val gridMainLineProperties = gridMainLineProperties()
    val gridScaleineProperties = gridScaleLineProperties()

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
        Text(text = monthChart.unitOfMeassure.toString(), modifier = Modifier.padding(top = 10.dp))

        val maxAmount = monthChart.maxAmount()
        val scaleUnit = monthChart.scaleUnit()
        val scaleUnitFraction = if (scaleUnit == 0.0) {
            0.0
        } else {
            scaleUnit / maxAmount
        }
        val scaleUnitHeight = Dp((maxHeight.value * scaleUnitFraction).toFloat())

        BoxWithConstraints(
            modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            val barWith = ((maxWidth / 12) / years.size)
            Row(
                modifier
                    .fillMaxWidth()
                    .drawWithContent {
                        drawLine(
                            strokeWidth = gridMainLineProperties.first,
                            color = gridMainLineProperties.second,
                            start = Offset(x = 0f, y = 0f),
                            end = Offset(x = 0f, y = size.height - montLabelHeight.toPx()),
                        )

                        drawLine(
                            strokeWidth = gridMainLineProperties.first,
                            color = gridMainLineProperties.second,
                            start = Offset(x = 0f, y = size.height - montLabelHeight.toPx()),
                            end = Offset(
                                x = size.width,
                                y = size.height - montLabelHeight.toPx()
                            ),
                        )
                        var scaleUnitLine = size.height - montLabelHeight.toPx()
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
                Spacer(modifier = Modifier.width(5.dp))
                for (month in 1..12) {
                    var i = 0
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row {
                            years.forEach { year ->
                                val chartConsumption =
                                    monthChart.monthConsumption(Period.make(year, month))
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
                                        years.size == 1
                                    )
                                }
                                i += 1
                            }
                        }
                        Text(
                            Period.month(month),
                            modifier = Modifier.height(montLabelHeight)
                        )
                    }
                }
            }
        }
    }
}