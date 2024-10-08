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

import org.openapitools.client.models.Consumption
import org.openapitools.client.models.ResidentialUnitReference

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Consumption data for a residential unit and specific period.
 *
 * @param reference 
 * @param consumptions Consumption values for different services and measurement units.
 * @param benchmarks Benchmarks for different services.
 */


@Serializable
data class ConsumptionDataResidentialUnit (

    @SerialName("reference")
    val reference: ResidentialUnitReference,

    /* Consumption values for different services and measurement units. */
    @SerialName("consumptions")
    val consumptions: kotlin.collections.List<Consumption>,

) {


}

