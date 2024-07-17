package com.maxwen.consumption_data.charts

import com.maxwen.consumption.models.Period

class MonthChartData(private val monthData: MutableMap<String, MutableMap<String, ChartConsumption>> = mutableMapOf(),
                     private var minAmount: Double = Double.MAX_VALUE, private var maxAmount: Double = Double.MIN_VALUE
) {

    fun addConsumption(year: String, month: String, consumption: ChartConsumption) {
        var yearData = monthData[year]
        if (yearData == null) {
            yearData = mutableMapOf<String, ChartConsumption>()
            monthData[year] = yearData
        }
        yearData[month] = consumption
        if (consumption.amount < minAmount) {
            minAmount = consumption.amount
        }
        if (consumption.amount > maxAmount) {
            maxAmount = consumption.amount
        }
    }

    override fun toString(): String {
        return monthData.toString()
    }

    fun sortedYears(): List<String> {
        return monthData.keys.sorted()
    }

    private fun sortedMonths(year: String): List<String> {
        return monthData[year]?.keys?.sorted() ?: mutableListOf<String>()
    }

    fun monthConsumption(year: String, month: String): ChartConsumption? {
        return monthData[year]?.get(month)
    }

    fun monthConsumption(period: Period): ChartConsumption? {
        return monthData[period.year()]?.get(period.month())
    }

    fun yearData(year: String): List<ChartConsumption?> {
        val list = mutableListOf<ChartConsumption?>()
        sortedMonths(year).forEach { month ->
            val consumption = monthConsumption(year, month)
            list.add(consumption)
        }
        return list
    }

    fun minAmount(year: String): Double {
        var minAmount: Double = Double.MAX_VALUE

        monthData[year]?.values?.forEach { consumption ->
            if (consumption.amount < minAmount) {
                minAmount = consumption.amount
            }
        }
        return minAmount
    }

    fun maxAmount(year: String): Double {
        var maxAmount: Double = Double.MIN_VALUE

        monthData[year]?.values?.forEach { consumption ->
            if (consumption.amount > maxAmount) {
                maxAmount = consumption.amount
            }
        }
        return maxAmount
    }

    fun minAmount(): Double {
        return minAmount
    }

    fun maxAmount(): Double {
        return maxAmount
    }
}