package com.maxwen.consumption.models

import org.openapitools.client.models.BillingUnitReference
import org.openapitools.client.models.ResidentialUnitReference
import org.openapitools.client.models.Service

data class ConsumptionSelector(
    val billingUnit: BillingUnitReference = BillingUnitReference(""),
    val service: Service = Service.HOT_WATER,
    val residentialUnit: ResidentialUnitReference? = null
)
