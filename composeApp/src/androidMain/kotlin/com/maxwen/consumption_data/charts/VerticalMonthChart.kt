package com.maxwen.consumption_data.charts

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxwen.consumption.models.Period

@Composable
fun VerticalMonthChart(
    monthChart: MonthChartData,
    years: List<String>,
    modifier: Modifier = Modifier
) {
    val yearColors = listOf<Color>(Color(209, 25, 25), Color(25, 170, 209), Color(25, 170, 25))
    val yearLabelHeight = 20.dp
    val gridLineColor = MaterialTheme.colorScheme.onBackground

    Column(
        modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        Text(
            years.joinToString(separator = ","),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )

        val maxAmount = monthChart.maxAmount()
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
                                        barWith
                                    )
                                } else {
                                    VerticalBar(
                                        chartConsumption.label,
                                        chartConsumption.amount,
                                        0.0,
                                        maxAmount,
                                        yearColors[year.toInt() % yearColors.size],
                                        barWith
                                    )
                                }
                                i += 1
                            }
                        }
                        Text(
                            Period.month(month),
                            modifier = Modifier.height(yearLabelHeight)
                        )
                    }
                }
            }
        }
    }
}