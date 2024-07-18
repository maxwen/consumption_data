package com.maxwen.consumption.models

import kotlinx.serialization.Serializable

@Serializable
data class Period(val period: String) {
    companion object {
        fun make(year: String, month: Int): Period {
            return Period("$year-%02d".format(month))
        }
        fun month(month: Int): String {
            return "%02d".format(month)
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
