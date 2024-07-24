package com.maxwen.consumption_data.models

import kotlinx.serialization.Serializable
import kotlin.math.pow
import kotlin.math.roundToInt

@Serializable
data class Period(val period: String) {
    companion object {
        fun make(year: String, month: Int): Period {
            val monthString = month.toString(2)
            return Period("$year-$monthString")
        }
        fun month(month: Int): String {
            return month.toString(2)
        }
    }

    fun year(): String {
        val parts = period.split("-");
        return if (parts.size == 2) {
            parts[0]
        } else {
            "0"
        }
    }

    fun yearInt(): Int {
        return year().toInt()
    }

    fun month(): String {
        val parts = period.split("-");
        return if (parts.size == 2) {
            parts[1]
        } else {
            "0"
        }
    }
    fun monthInt(): Int {
        return month().toInt()
    }

    override fun toString(): String {
        return period
    }
}
