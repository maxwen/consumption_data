package com.maxwen.consumption_data.charts

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke

class ChartProperties {
    companion object {
        val yearColors = listOf<Color>(
            Color(0x31, 0x40, 0xa5),
            Color(0xd8, 0x53, 0x53),
            Color(0x1c, 0x95, 0x88),
            Color(0xdf, 0x7e, 0x2b)
        )
    }
}

@Composable
fun gridMainLineProperties(): Pair<Float, Color> {
    return Pair(Stroke.HairlineWidth, MaterialTheme.colorScheme.onBackground)
}


@Composable
fun gridScaleLineProperties(): Pair<Float, Color> {
    return Pair(Stroke.HairlineWidth, MaterialTheme.colorScheme.onBackground)
}

fun getScaleLinePathEffect() : PathEffect {
    return PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
}