package com.maxwen.consumption_data.charts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VerticalBar(
    label: String,
    amount: Double,
    minAmount: Double,
    maxAmount: Double,
    color: Color,
    maxWith: Dp,
    maxHeight: Dp,
    showAmount: Boolean,
    modifier: Modifier = Modifier
) {
    val amountRange = maxAmount - minAmount
    val amountFraction = if (amount == 0.0) {
        0.0
    } else {
        (amount - minAmount) / amountRange
    }
    val screenFraction = Dp((maxHeight.value * amountFraction).toFloat())
    val availSpace = maxHeight - screenFraction

    val textMeasurer = rememberTextMeasurer()
    val fontSize = 16.sp

    val textLayoutResult: TextLayoutResult = textMeasurer.measure(
        text = amount.toString(), style = TextStyle(
            fontSize = fontSize,
            textAlign = TextAlign.Center,
        )
    )
    val textSize = textLayoutResult.size
    val amountInline = with(LocalDensity.current) { availSpace.toPx() } < textSize.width * 1.5
    val textColor = MaterialTheme.colorScheme.onBackground

    val expandedState = remember { MutableTransitionState(false) }
    expandedState.targetState = true

    Column(
        modifier
            .width(maxWith)
            .height(maxHeight),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1.0f))

        if (showAmount && !amountInline) {
            Canvas(
                modifier = Modifier.requiredSize(height = maxWith, width = maxWith)
            ) {
                withTransform({
                    rotate(degrees = -90F)
                }) {
                    val yOffset = (size.width - textSize.height) / 2f
                    drawText(
                        textMeasurer,
                        amount.toString(),
                        softWrap = false,
                        overflow = TextOverflow.Visible,
                        style = TextStyle(
                            fontSize = fontSize,
                            color = textColor,
                            textAlign = TextAlign.Center,
                        ),
                        topLeft = Offset(x = 0f, y = yOffset)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        if (expandedState.currentState || expandedState.targetState || !expandedState.isIdle) {
            AnimatedVisibility(
                visibleState = expandedState, enter = expandVertically(),
                exit = fadeOut(),
            ) {
                Column(
                    modifier
                        .width(maxWith)
                        .height(screenFraction)
                        .padding(start = 1.dp, end = 1.dp)
                        .background(
                            getVerticalBrush(color),
                            shape = RoundedCornerShape(ChartProperties.barCornerRadius)
                        ),
                ) {
                    if (showAmount && amountInline) {
                        Spacer(modifier = Modifier.height(25.dp))
                        Canvas(
                            modifier = Modifier.requiredSize(height = maxWith, width = maxWith)
                        ) {
                            withTransform({
                                rotate(degrees = -90F)
                            }) {
                                val yOffset = (size.width - textSize.height) / 2f
                                drawText(
                                    textMeasurer,
                                    amount.toString(),
                                    softWrap = false,
                                    overflow = TextOverflow.Visible,
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                    ),
                                    topLeft = Offset(x = 0f, y = yOffset),
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))

                }
            }
        }
    }
}