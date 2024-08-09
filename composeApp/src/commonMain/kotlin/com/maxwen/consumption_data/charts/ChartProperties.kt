package com.maxwen.consumption_data.charts

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
//import androidx.compose.ui.util.lerp
import androidx.compose.ui.graphics.lerp


class ChartProperties {
    companion object {
        val yearColors = listOf<Color>(
            Color(0x31, 0x40, 0xa5),
            Color(0xd8, 0x53, 0x53),
            Color(0x1c, 0x95, 0x88),
            Color(0xdf, 0x7e, 0x2b)
        )

        val maxBarHeightMonthly = 30.dp
        val maxBarHeightYearly = 30.dp
        val maxBarWidthYearly = 30.dp
        val barCornerRadius = 2.dp
        // just approx in px for scroll to year
        val chartHeaderHeight = 300
    }
}

@Composable
fun getHorizontalBrush(color: Color): Brush {
    return Brush.horizontalGradient(
        listOf(
            lerp(MaterialTheme.colorScheme.surface, color, 0.75F),
            color
        )
    )
}

@Composable
fun getVerticalBrush(color: Color): Brush {
    return Brush.verticalGradient(
        listOf(
            color,
            lerp(MaterialTheme.colorScheme.surface, color, 0.75F)
        )
    )
}

@Composable
fun gridMainLineProperties(): Pair<Float, Color> {
    return Pair(Stroke.HairlineWidth, MaterialTheme.colorScheme.onBackground)
}


@Composable
fun gridScaleLineProperties(): Pair<Float, Color> {
    return Pair(Stroke.HairlineWidth, MaterialTheme.colorScheme.onBackground)
}

fun gridScaleLinePathEffect(): PathEffect {
    return PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
}