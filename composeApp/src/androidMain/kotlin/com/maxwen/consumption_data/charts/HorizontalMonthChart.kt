package com.maxwen.consumption_data.charts

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxwen.consumption.models.Period

@Composable
fun HorizontalMonthChart(
    monthChart: MonthChartData,
    years: List<String>,
    maxBarHeight: Dp,
    modifier: Modifier = Modifier
) {
    val yearColors = listOf<Color>(Color(209, 25, 25), Color(25, 170, 209), Color(25, 170, 25))
    val gridLineColor = MaterialTheme.colorScheme.onBackground
    val monthLabelWith = 40.dp

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
        val barHeight = (maxBarHeight / years.size)
        Column(
            modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .drawWithContent {
                    drawLine(
                        strokeWidth = 1.dp.toPx(),
                        color = gridLineColor,
                        start = Offset(x = monthLabelWith.toPx(), y = 0f),
                        end = Offset(x = monthLabelWith.toPx(), y = size.height),
                    )

                    drawLine(
                        strokeWidth = 1.dp.toPx(),
                        color = gridLineColor,
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
                                    barHeight
                                )
                            } else {
                                HorizontalBar(
                                    chartConsumption.label,
                                    chartConsumption.amount,
                                    0.0,
                                    maxAmount,
                                    yearColors[year.toInt() % yearColors.size],
                                    barHeight
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}