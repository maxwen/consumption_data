package com.maxwen.consumption_data.charts

import org.openapitools.client.models.BillingUnitReference
import org.openapitools.client.models.ResidentialUnitReference
import org.openapitools.client.models.Service

class ChartConsumption(
    val amount: Double,
    val label: String,
    val period: String,
    val service: Service,
    val billingUnit: BillingUnitReference,
    val residenatilUnit: ResidentialUnitReference?,
) {

}