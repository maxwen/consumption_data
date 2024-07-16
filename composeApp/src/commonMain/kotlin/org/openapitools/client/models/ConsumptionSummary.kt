/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package org.openapitools.client.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.openapitools.client.models.ConsumptionSummaryBillingUnit

/**
 * Summary of available consumption data.
 *
 * @param billingunit 
 */


@Serializable
data class ConsumptionSummary (

    @SerialName("billingunit")
    val billingunit: ConsumptionSummaryBillingUnit

) {


}

