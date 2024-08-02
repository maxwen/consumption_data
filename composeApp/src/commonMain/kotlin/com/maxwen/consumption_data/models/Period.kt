package com.maxwen.consumption_data.models

import kotlinx.serialization.Serializable

@Serializable
data class Period(val period: String) {
    companion object {
        fun make(year: String, month: Int): Period {
            val monthString = month.toStringWithLength(2)
            return Period("$year-$monthString")
        }
        fun month(month: Int): String {
            return month.toStringWithLength(2)
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
