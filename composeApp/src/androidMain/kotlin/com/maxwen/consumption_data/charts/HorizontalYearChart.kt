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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.theme.AppTheme

@Composable
fun HorizontalYearChart(
    yearChart: YearChartData,
    modifier: Modifier = Modifier
) {
    val yearColors = listOf<Color>(Color(209, 25, 25), Color(25, 170, 209), Color(25, 170, 25))
    val barHeight = 40.dp
    val gridLineColor = MaterialTheme.colorScheme.onBackground
    val yearLabelWith = 50.dp

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


        Column(
            modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .drawWithContent {
                    drawLine(
                        strokeWidth = 1.dp.toPx(),
                        color = gridLineColor,
                        start = Offset(x = yearLabelWith.toPx(), y = 0f),
                        end = Offset(x = yearLabelWith.toPx(), y = size.height),
                    )

                    drawLine(
                        strokeWidth = 1.dp.toPx(),
                        color = gridLineColor,
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
                            barHeight
                        )
                    } else {
                        HorizontalBar(
                            chartConsumption.label,
                            chartConsumption.amount,
                            0.0,
                            yearChart.maxAmount(),
                            yearColors[year.toInt() % yearColors.size],
                            barHeight
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}