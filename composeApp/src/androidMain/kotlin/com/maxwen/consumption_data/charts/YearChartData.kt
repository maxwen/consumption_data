package com.maxwen.consumption_data.charts

import org.openapitools.client.models.Service
import org.openapitools.client.models.UnitOfMeasure


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

}