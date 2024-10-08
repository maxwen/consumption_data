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

/**
 * The measured unit:  * `KWH` * `HCU`: Units of heat cost allocators. * `M3` 
 *
 * Values: KWH,HCU,M3
 */

@Serializable
enum class UnitOfMeasure(val value: kotlin.String) {

    @SerialName("KWH")
    KWH("KWH"),

    @SerialName("HCU")
    HCU("HCU"),

    @SerialName("M3")
    M3("M3");

    /**
     * Override [toString()] to avoid using the enum variable name as the value, and instead use
     * the actual value defined in the API spec file.
     *
     * This solves a problem when the variable name and its value are different, and ensures that
     * the client sends the correct enum values to the server always.
     */
    override fun toString(): kotlin.String = value
}

