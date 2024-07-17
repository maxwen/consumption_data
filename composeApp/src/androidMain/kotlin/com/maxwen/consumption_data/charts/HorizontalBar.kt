package com.maxwen.consumption_data.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalBar(
    label: String,
    amount: Double,
    minAmount: Double,
    maxAmount: Double,
    color: Color,
    maxHeight: Dp,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val offset = 0.dp
    val screenRange = screenWidth - offset
    val amountRange = maxAmount - minAmount
    val amountFraction = if (amount == 0.0) {
        0.0
    } else {
        (amount - minAmount) / amountRange
    }
    val screenFraction = offset + Dp((screenRange.value * amountFraction).toFloat())

    Row(
        modifier
            .height(maxHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier
                .height(maxHeight)
        ) {
            Box(
                modifier
                    .width(screenFraction)
                    .height(maxHeight)
                    .padding(1.dp)
                    .clip(RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp))
                    .background(
                        color,
                        shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp)
                    ),
            )
            Spacer(modifier = Modifier.weight(1.0F))
        }
//        if (amount != 0.0) {
//            Text(amount.toString(), modifier = Modifier.padding(end = 5.dp))
//        }
    }
}