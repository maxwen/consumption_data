package com.maxwen.consumption_data.charts

import org.openapitools.client.models.BillingUnitReference
import org.openapitools.client.models.ResidentialUnitReference
import org.openapitools.client.models.Service
import org.openapitools.client.models.UnitOfMeasure

class ChartConsumption(
    val amount: Double,
    val label: String,
    val period: String,
    val service: Service,
    val unitOfMeasure: UnitOfMeasure,
    val billingUnit: BillingUnitReference,
    val residenatilUnit: ResidentialUnitReference?,
) {

}