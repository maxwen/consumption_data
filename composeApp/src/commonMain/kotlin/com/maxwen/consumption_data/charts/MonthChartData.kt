package com.maxwen.consumption_data.charts

import com.maxwen.consumption_data.models.Period
import com.maxwen.consumption_data.models.toStringWithDec
import org.openapitools.client.models.Service
import org.openapitools.client.models.UnitOfMeasure
import kotlin.math.pow

class MonthChartData(
    private val monthData: MutableMap<String, MutableMap<String, ChartConsumption>> = mutableMapOf(),
    private var minAmount: Double = Double.MAX_VALUE,
    private var maxAmount: Double = Double.MIN_VALUE,
    val service: Service,
    val unitOfMeassure: UnitOfMeasure
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

    fun avgAmount(year: String): Double {
        var sumAmount: Double = Double.MIN_VALUE
        var numAmount = 0

        monthData[year]?.values?.forEach { consumption ->
            sumAmount += consumption.amount
            numAmount += 1
        }
        return (sumAmount / numAmount).toStringWithDec(2).toDouble()
    }

    fun sumAmount(year: String): Double {
        var sumAmount: Double = Double.MIN_VALUE

        monthData[year]?.values?.forEach { consumption ->
            sumAmount += consumption.amount
        }
        return sumAmount.toStringWithDec(2).toDouble()
    }

    fun numAmount(year: String): Int {
        var numAmount: Int = 0
        numAmount += monthData[year]?.values?.size ?: 0
        return numAmount
    }

    fun maxAmountOfAllYears(): Double {
        return maxAmount
    }

    fun numAmountOfYears(years: List<String>): Int {
        var numAmount: Int = 0
        years.forEach { year ->
            numAmount += numAmount(year)
        }
        return numAmount
    }

    fun maxAmountOfYears(years: List<String>): Double {
        var maxAmount: Double = Double.MIN_VALUE
        years.forEach { year ->
            val max = maxAmount(year)
            if (max > maxAmount) {
                maxAmount = max
            }
        }
        return maxAmount
    }

    fun minAmountOfYears(years: List<String>): Double {
        var minAmount: Double = Double.MAX_VALUE
        years.forEach { year ->
            val min = minAmount(year)
            if (min < minAmount) {
                minAmount = min
            }
        }
        return minAmount
    }

    fun sumAmountOfYears(years: List<String>): Double {
        var sumAmount: Double = Double.MIN_VALUE

        years.forEach { year ->
            sumAmount += sumAmount(year)
        }

        return sumAmount.toStringWithDec(2).toDouble()
    }

    fun avgAmountOfYears(years: List<String>): Double {
        val sumAmount: Double = sumAmountOfYears(years)
        val numAmount = numAmountOfYears(years)
        return (sumAmount / numAmount).toStringWithDec(2).toDouble()
    }

    fun scaleUnit(): Double {
        var amountScale = maxAmount
        var divNum = 0
        while (amountScale / 10 > 1) {
            divNum++
            amountScale /= 10
        }
        val scaleUnit = 10.toDouble().pow(divNum)
        return scaleUnit
    }
}