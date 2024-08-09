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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxwen.consumption_data.models.MainViewModel
import com.maxwen.consumption_data.models.Period
import com.maxwen.consumption_data.ui.PopupBox
import com.maxwen.consumption_data.ui.TextWithIcon
import consumption_data.composeapp.generated.resources.Res
import consumption_data.composeapp.generated.resources.avg_amount
import consumption_data.composeapp.generated.resources.max_amount
import consumption_data.composeapp.generated.resources.min_amount
import consumption_data.composeapp.generated.resources.sum_amount

@Composable
fun VerticalMonthChart(
    viewModel: MainViewModel,
    monthChart: MonthChartData,
    years: List<String>,
    modifier: Modifier = Modifier
) {
    val yearColors = ChartProperties.yearColors
    val montLabelHeight = 20.dp
    val maxHeight = 300.dp
    val gridMainLineProperties = gridMainLineProperties()
    val gridScaleineProperties = gridScaleLineProperties()
    var showMultiMonthPopup by rememberSaveable {
        mutableIntStateOf(0)
    }

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

        if (years.size == 1) {
            val year = years.first()
            Row(
                modifier = Modifier.padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    monthChart.unitOfMeassure.value,
                )
                Spacer(modifier = Modifier.width(10.dp))
                TextWithIcon(monthChart.minAmount(year).toString(), Res.drawable.min_amount)
                TextWithIcon(monthChart.maxAmount(year).toString(), Res.drawable.max_amount)
                TextWithIcon(monthChart.sumAmount(year).toString(), Res.drawable.sum_amount)
                TextWithIcon(monthChart.avgAmount(year).toString(), Res.drawable.avg_amount)
            }
        } else {
            Row(
                modifier = Modifier.padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    monthChart.unitOfMeassure.value,
                )
                Spacer(modifier = Modifier.width(10.dp))
                TextWithIcon(monthChart.minAmountOfYears(years).toString(), Res.drawable.min_amount)
                TextWithIcon(monthChart.maxAmountOfYears(years).toString(), Res.drawable.max_amount)
                TextWithIcon(monthChart.sumAmountOfYears(years).toString(), Res.drawable.sum_amount)
                TextWithIcon(monthChart.avgAmountOfYears(years).toString(), Res.drawable.avg_amount)
            }
        }
        val maxAmount = monthChart.maxAmountOfAllYears()
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
            val spacerWidth = 5.dp
            val monthWidth = (maxWidth / 12) - spacerWidth
            val barWith = monthWidth / years.size

            Row(
                modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    // must include top padding
                    .height(maxHeight + 20.dp)
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
                                pathEffect = gridScaleLinePathEffect()
                            )
                        }
                        this@drawWithContent.drawContent()
                    }
            ) {
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
                        Box {
                            Text(Period.month(month),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .width(monthWidth)
                                    .height(montLabelHeight)
                                    .clickable {
                                        if (years.size > 1) {
                                            if (showMultiMonthPopup != 0) {
                                                showMultiMonthPopup = 0
                                            }
                                            showMultiMonthPopup = month
                                        }
                                    })
                            PopupBox(
                                offset = IntOffset(y = LocalDensity.current.run { years.size * -30.dp.toPx() }
                                    .toInt(), x = 0),
                                showPopup = showMultiMonthPopup == month,
                                onClickOutside = { showMultiMonthPopup = 0 },
                                content = {
                                    Spacer(modifier = Modifier.height(5.dp))
                                    for (year in years) {
                                        val chartConsumption =
                                            monthChart.monthConsumption(
                                                Period.make(
                                                    year,
                                                    month
                                                )
                                            )
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
                    }
                    Spacer(modifier = Modifier.width(spacerWidth))
                }
            }
        }
    }
}