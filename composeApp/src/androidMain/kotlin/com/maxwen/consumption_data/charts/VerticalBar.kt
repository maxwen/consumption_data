package com.maxwen.consumption_data.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VerticalBar(
    label: String,
    amount: Double,
    minAmount: Double,
    maxAmount: Double,
    color: Color,
    maxWith: Dp,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val chartHeight = 300.dp
    val screenWidth = configuration.screenWidthDp.dp

    val offset = 0.dp
    val screenRange = chartHeight - offset
    val amountRange = maxAmount - minAmount
    val amountFraction = if (amount == 0.0) {
        0.0
    } else {
        (amount - minAmount) / amountRange
    }
    val screenFraction = offset + Dp((screenRange.value * amountFraction).toFloat())

    Column(
        modifier
            .width(maxWith),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        if (amount != 0.0) {
//            Text(amount.toString(), color = Color.Black, modifier = Modifier.padding(top = 5.dp))
//        }
        Column(
            modifier
                .width(maxWith)
                .height(chartHeight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1.0F))
            Box(
                modifier
                    .width(maxWith)
                    .height(screenFraction)
                    .padding(1.dp)
                    .clip(RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp))
                    .background(
                        color,
                        shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp)
                    ),
            )
        }
    }
}