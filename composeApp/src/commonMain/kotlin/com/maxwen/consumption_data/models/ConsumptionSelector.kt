package com.maxwen.consumption_data.models

import org.openapitools.client.models.BillingUnitReference
import org.openapitools.client.models.ResidentialUnitReference
import org.openapitools.client.models.Service
import org.openapitools.client.models.UnitOfMeasure

data class ConsumptionSelector(
    val billingUnit: BillingUnitReference = BillingUnitReference(""),
    val service: Service = Service.HOT_WATER,
    val unitOfMeasure: UnitOfMeasure = UnitOfMeasure.KWH,
    val residentialUnit: ResidentialUnitReference? = null,
    val periodStart: String? = null,
    val periodEnd: String? = null,
    val years: List<String>? = null
)
