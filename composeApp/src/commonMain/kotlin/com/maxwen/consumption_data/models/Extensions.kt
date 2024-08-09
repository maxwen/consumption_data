package com.maxwen.consumption_data.models

import consumption_data.composeapp.generated.resources.Res
import consumption_data.composeapp.generated.resources.cold_water_device
import consumption_data.composeapp.generated.resources.cooling_device
import consumption_data.composeapp.generated.resources.heating_device
import consumption_data.composeapp.generated.resources.hot_water_device
import org.jetbrains.compose.resources.DrawableResource
import org.openapitools.client.models.Service
import kotlin.math.pow
import kotlin.math.roundToInt


/**
 * Return the float receiver as a string display with numOfDec after the decimal (rounded)
 * (e.g. 35.72 with numOfDec = 1 will be 35.7, 35.78 with numOfDec = 2 will be 35.80)
 *
 * @param numOfDec number of decimal places to show (receiver is rounded to that number)
 * @return the String representation of the receiver up to numOfDec decimal places
 */
fun Double.toStringWithDec(numOfDec: Int): String {
    val integerDigits = this.toInt()
    val floatDigits = ((this - integerDigits) * 10f.pow(numOfDec)).roundToInt()
    return "${integerDigits}.${floatDigits}"
}

fun Int.toStringWithLength(length: Int): String {
    return this.toString().padStart(length, '0')
}

fun Service.icon(): DrawableResource {
    return when (this) {
        Service.COOLING -> {
            Res.drawable.cooling_device
        }
        Service.HEATING -> {
            Res.drawable.heating_device
        }
        Service.HOT_WATER -> {
            Res.drawable.hot_water_device
        }
        Service.COLD_WATER -> {
            Res.drawable.cold_water_device

        }
    }
}
