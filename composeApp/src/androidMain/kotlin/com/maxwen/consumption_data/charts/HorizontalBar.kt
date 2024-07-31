package com.maxwen.consumption_data.charts

import androidx.annotation.Px
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
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
    maxWith: Dp,
    showAmount: Boolean,
    modifier: Modifier = Modifier
) {

    val amountRange = maxAmount - minAmount
    val amountFraction = if (amount == 0.0) {
        0.0
    } else {
        (amount - minAmount) / amountRange
    }
    val screenFraction = Dp((maxWith.value * amountFraction).toFloat())
    val availSpace = maxWith - screenFraction

    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult: TextLayoutResult =
        textMeasurer.measure(
            text = amount.toString()
        )
    val textSize = textLayoutResult.size.width
    val amountInline = with(LocalDensity.current) { availSpace.toPx() } < textSize * 1.5
    val expandedState = remember { MutableTransitionState(false) }
    expandedState.targetState = true
    if (expandedState.currentState || expandedState.targetState || !expandedState.isIdle) {

        AnimatedVisibility(
            visibleState = expandedState, enter = expandHorizontally(),
            exit = fadeOut(),
        ) {
            Row(
                modifier
                    .height(maxHeight),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier
                        .width(screenFraction)
                        .height(maxHeight)
                        .padding(top = 1.dp, bottom = 1.dp)
                        .background(
                            color,
                            shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (showAmount && amountInline) {
                        Spacer(modifier = Modifier.weight(1.0F))
                        Text(amount.toString(), modifier = Modifier.padding(end = 5.dp))
                    }
                }
                if (showAmount && !amountInline) {
                    Text(amount.toString(), modifier = Modifier.padding(start = 5.dp))
                }

                Spacer(modifier = Modifier.weight(1.0F))
            }
        }
    }
}