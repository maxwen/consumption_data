package com.maxwen.consumption.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.openapitools.client.models.*

@Serializable
class ConsumptionEntity(
    val period: String,
    val billingUnit: BillingUnitReference,
    val residentialUnit: ResidentialUnitReference?,
    val service: Service,
    val unitofmeasure: UnitOfMeasure,
    val errors: Boolean,
    var amount: Double? = null,
) : Comparable<ConsumptionEntity> {
    override fun compareTo(other: ConsumptionEntity): Int {
        return period.compareTo(other.period)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ConsumptionEntity) return false
        return other.period == period &&
                other.billingUnit == billingUnit &&
                other.service == service &&
                other.unitofmeasure == unitofmeasure &&
                if (other.residentialUnit != null && residentialUnit != null) {
                    other.residentialUnit == residentialUnit
                } else {
                    true
                }
    }

    override fun toString(): String {
        val prettyJson = Json { // this returns the JsonBuilder
            prettyPrint = true
            // optional: specify indent
            prettyPrintIndent = " "
        }
        return prettyJson.encodeToString(this)
    }

    override fun hashCode(): Int {
        var result = period.hashCode()
        result = 31 * result + billingUnit.hashCode()
        return result
    }

    fun add(consumption: ConsumptionEntity) {
        consumption.amount?.let {
            amount = amount?.plus(it) }
    }
}