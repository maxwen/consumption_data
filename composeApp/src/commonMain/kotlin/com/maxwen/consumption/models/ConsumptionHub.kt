package com.maxwen.consumption.models

import io.ktor.client.statement.*
import org.openapitools.client.apis.EedConsumptionApi
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.infrastructure.ServerException
import org.openapitools.client.infrastructure.isInformational
import org.openapitools.client.models.*

class ConsumptionHub {

    private val billingunits: MutableMap<String, ServiceConfigurationBillingUnit> = mutableMapOf()
    private val unitData: MutableMap<String, MutableMap<String, ConsumptionDataBillingUnit>> =
        mutableMapOf()

    @Throws(
        IllegalStateException::class,
        UnsupportedOperationException::class,
        ClientException::class,
        ServerException::class
    )
    suspend fun load(baseUrl: String, username: String, password: String) {
        billingunits.clear()
        unitData.clear()
        val client = EedConsumptionApi(baseUrl, username, password)
        var offset = 0
        val limit = 20
        while (true) {
            val u = client.getBillingunits(limit, offset).billingunits
            if (u.isEmpty()) {
                break
            }
            u.forEach { unit ->
                this.billingunits[unit.reference.mscnumber] = unit
            }
            // TODO maybe check and filter for only those with consumptiondata
            offset += limit
        }

        for (mscnumber in this.billingunits.keys) {
            val periods = client.getConsumptionSummary(mscnumber)
            if (periods.billingunit.periods.isNotEmpty()) {
                val periodMap = mutableMapOf<String, ConsumptionDataBillingUnit>()
                for (period in periods.billingunit.periods) {
                    val periodData = client.getConsumptionData(mscnumber, period.period)
                    periodMap[period.period] = periodData.billingunit
                }
                if (periodMap.isNotEmpty()) {
                    this.unitData[mscnumber] = periodMap
                }
            }
        }
    }

    override fun toString(): String {
        return this.unitData.toString()
    }

    fun getBillingUnits(): List<ServiceConfigurationBillingUnit> {
        val list = mutableListOf<ServiceConfigurationBillingUnit>()
        list.addAll(this.billingunits.values)
        return list
    }

    fun getBillingUnitData(mscnumber: String) : ServiceConfigurationBillingUnit? {
        return billingunits[mscnumber]
    }

    fun getBillingUnitServiceStart(mscnumber: String) : String? {
        return billingunits[mscnumber]?.servicestart
    }

    fun getBillingUnitLastPeriod(mscnumber: String) : String? {
        return billingunits[mscnumber]?.lastperiod
    }

    fun getBillingUnitServices(mscnumber: String): Set<Service> {
        val set = mutableSetOf<Service>()
        unitData[mscnumber]?.forEach { billingUnitData ->
            billingUnitData.value.residentialunits.forEach {
                it.consumptions.forEach { consumption ->
                    set.add(consumption.service)
                }
            }
        }
        return set
    }

    fun getBillingUnitServicesUnitOfMeasure(mscnumber: String, service: Service): Set<UnitOfMeasure> {
        val set = mutableSetOf<UnitOfMeasure>()
        unitData[mscnumber]?.forEach { billingUnitData ->
            billingUnitData.value.residentialunits.forEach {
                it.consumptions.forEach { consumption ->
                    set.add(consumption.unitofmeasure)
                }
            }
        }
        return set
    }

    fun getBillingUnitResidentialUnits(mscnumber: String): Set<ResidentialUnitReference> {
        val set = mutableSetOf<ResidentialUnitReference>()
        unitData[mscnumber]?.forEach { billingUnitData ->
            billingUnitData.value.residentialunits.forEach {
                set.add(it.reference)
            }
        }
        return set
    }

    fun consumptionOfTypeOfUnit(
        selector: ConsumptionSelector,
    ): List<ConsumptionEntity>? {
        val mscnumber = selector.billingUnit.mscnumber
        val service = selector.service
        val unitOfMeasure = selector.unitOfMeasure
        val residentialUnit = selector.residentialUnit
        if (unitData.containsKey(mscnumber)) {
            val billingUnit = selector.billingUnit
            val consumptions = mutableListOf<ConsumptionEntity>()
            unitData[mscnumber]?.keys?.forEach { period ->
                var outOfPeriod = false
                if (selector.periodStart != null) {
                    outOfPeriod = period < selector.periodStart
                }
                if (selector.periodEnd != null && !outOfPeriod) {
                    outOfPeriod = period > selector.periodEnd
                }
                if (selector.years != null && !outOfPeriod) {
                    outOfPeriod = !selector.years.contains(Period(period).year())
                }
                if (!outOfPeriod) {
                    val billingUnitData = unitData[mscnumber]?.get(period)
                    billingUnitData?.residentialunits?.forEach { unit ->
                        var ignoreResidential = false
                        if (residentialUnit == null) {
                            ignoreResidential = true
                        }
                        if (ignoreResidential || unit.reference == residentialUnit) {
                            unit.consumptions.filter { c -> c.service == service && c.unitofmeasure == unitOfMeasure && c.amount != null && !c.errors }
                                .forEach { c ->
                                    val e = ConsumptionEntity(
                                        period,
                                        billingUnit,
                                        residentialUnit,
                                        service,
                                        c.unitofmeasure,
                                        c.errors,
                                        "%.2f".format(c.amount).toDouble()
                                    )
                                    val i = consumptions.indexOf(e)
                                    if (i != -1) {
                                        val f = consumptions[i]
                                        f.add(e)
                                    } else {
                                        consumptions.add(e)
                                    }
                                }
                        }
                    }
                }
            }
            return consumptions.sorted()
        }
        return null
    }

    fun minConsumptionOfTypeOfUnit(
        selector: ConsumptionSelector,
    ): Pair<String, Double>? {
        val consumptions = consumptionOfTypeOfUnit(selector)
        if (consumptions?.isNotEmpty() == true) {
            var minPeriod = ""
            var minAmount = Double.MAX_VALUE
            consumptions.filter { it.amount != null && !it.errors }.forEach {
                if (it.amount!! < minAmount) {
                    minAmount = it.amount!!
                    minPeriod = it.period
                }
            }
            return Pair(minPeriod, minAmount)
        }
        return null
    }

    fun maxConsumptionOfTypeOfUnit(
        selector: ConsumptionSelector,
    ): Pair<String, Double>? {
        val consumptions = consumptionOfTypeOfUnit(selector)
        if (consumptions?.isNotEmpty() == true) {
            var minPeriod = ""
            var maxAmount = Double.MIN_VALUE
            consumptions.filter { it.amount != null && !it.errors }.forEach {
                if (it.amount!! > maxAmount) {
                    maxAmount = it.amount!!
                    minPeriod = it.period
                }
            }
            return Pair(minPeriod, maxAmount)
        }
        return null
    }

    fun avgConsumptionOfTypeOfUnit(
        selector: ConsumptionSelector,
    ): Double? {
        val consumptions = consumptionOfTypeOfUnit(selector)
        if (consumptions?.isNotEmpty() == true) {
            var sumAmount = 0.0
            var numAmount = 0
            consumptions.filter { it.amount != null && !it.errors }.forEach {
                sumAmount += it.amount!!
                numAmount += 1
            }
            return "%.2f".format(sumAmount / numAmount).toDouble()
        }
        return null
    }

    fun sumConsumptionOfTypeOfUnit(
        selector: ConsumptionSelector,
    ): Double? {
        val consumptions = consumptionOfTypeOfUnit(selector)
        if (consumptions?.isNotEmpty() == true) {
            var sumAmount = 0.0
            consumptions.filter { it.amount != null && !it.errors }.forEach {
                sumAmount += it.amount!!
            }
            return "%.2f".format(sumAmount).toDouble()
        }
        return null
    }
}
