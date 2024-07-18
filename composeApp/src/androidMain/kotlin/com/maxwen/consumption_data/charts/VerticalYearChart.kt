package com.maxwen.consumption_data.charts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VerticalYearChart(
    yearChart: YearChartData,
    modifier: Modifier = Modifier
) {
    val yearColors = ChartProperties.yearColors

    val barWith = 40.dp
    val gridLineColor = MaterialTheme.colorScheme.onBackground
    val yearLabelHeight = 20.dp

    Column(
        modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {

        Text(
            "Years",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
        Text(text = yearChart.unitOfMeassure.toString(), modifier = Modifier.padding(top = 10.dp))

        Row(
            modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .drawWithContent {
                    drawLine(
                        strokeWidth = 1.dp.toPx(),
                        color = gridLineColor,
                        start = Offset(x = 0f, y = 0f),
                        end = Offset(x = 0f, y = size.height - yearLabelHeight.toPx()),
                    )

                    drawLine(
                        strokeWidth = 1.dp.toPx(),
                        color = gridLineColor,
                        start = Offset(x = 0f, y = size.height - yearLabelHeight.toPx()),
                        end = Offset(
                            x = size.width,
                            y = size.height - yearLabelHeight.toPx()
                        ),
                    )
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
                                false
                            )
                        } else {
                            VerticalBar(
                                chartConsumption.label,
                                chartConsumption.amount,
                                0.0,
                                yearChart.maxAmount(),
                                yearColors[year.toInt() % yearColors.size],
                                barWith,
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