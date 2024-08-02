package com.maxwen.consumption_data.charts

import com.maxwen.consumption_data.models.toStringWithDec
import org.openapitools.client.models.Service
import org.openapitools.client.models.UnitOfMeasure
import kotlin.math.pow


class YearChartData(
    private val yearData: MutableMap<String, ChartConsumption> = mutableMapOf(),
    private var minAmount: Double = Double.MAX_VALUE, private var maxAmount: Double = Double.MIN_VALUE,
    val service: Service, val unitOfMeassure: UnitOfMeasure
) {
    fun addConsumption(year: String, consumption: ChartConsumption) {
        yearData[year] = consumption
        if (consumption.amount < minAmount) {
            minAmount = consumption.amount
        }
        if (consumption.amount > maxAmount) {
            maxAmount = consumption.amount
        }
    }

    fun sortedYears(): List<String> {
        return yearData.keys.sorted()
    }

    fun yearData(year: String): ChartConsumption? {
        return yearData[year]
    }

    fun minAmount(): Double {
        return minAmount
    }

    fun maxAmount(): Double {
        return maxAmount
    }

    fun avgAmount(): Double {
        var sumAmount: Double = Double.MIN_VALUE
        var numAmount = 0

        yearData.values.forEach { consumption ->
            sumAmount += consumption.amount
            numAmount += 1
        }
        return (sumAmount / numAmount).toStringWithDec(2).toDouble()
    }

    fun sumAmount(): Double {
        var sumAmount: Double = Double.MIN_VALUE

        yearData.values.forEach { consumption ->
            sumAmount += consumption.amount
        }
        return sumAmount.toStringWithDec(2).toDouble()
    }

    fun scaleUnit() : Double {
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