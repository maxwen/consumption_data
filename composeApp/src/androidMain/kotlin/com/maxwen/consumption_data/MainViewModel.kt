package com.maxwen.consumption_data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxwen.consumption.models.ConsumptionEntity
import com.maxwen.consumption.models.ConsumptionHub
import com.maxwen.consumption.models.ConsumptionSelector
import com.maxwen.consumption.models.Period
import com.maxwen.consumption.models.Settings
import com.maxwen.consumption_data.charts.ChartConsumption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.openapitools.client.apis.EedConsumptionApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.openapitools.client.models.BillingUnitReference
import org.openapitools.client.models.ResidentialUnitReference
import org.openapitools.client.models.Service
import org.openapitools.client.models.ServiceConfigurationBillingUnit

class MainViewModel : ViewModel() {
    private val _loaded = MutableStateFlow(false)
    val loaded: StateFlow<Boolean> = _loaded.asStateFlow()
    private val _selector = MutableStateFlow(ConsumptionSelector())
    val selector: StateFlow<ConsumptionSelector> = _selector.asStateFlow()
    private val data = ConsumptionHub()
    private val _squashResidentialUnits = MutableStateFlow(false)
    val squashResidentialUnits: StateFlow<Boolean> = _squashResidentialUnits.asStateFlow()
    val password = MutableStateFlow("")
    val username = MutableStateFlow("")
    val baseurl = MutableStateFlow("")

    init {
        viewModelScope.launch {
            data.load(Settings.getBaseUrl(), Settings.getUsername(), Settings.getPasword())
            password.update { Settings.getPasword() }
            username.update { Settings.getUsername() }
            baseurl.update { Settings.getBaseUrl() }
            _loaded.value = true
        }
    }

    fun setBaseUrl(value: String) {
        baseurl.update { value }
        viewModelScope.launch {
            Settings.setBaseUrl(value)
        }
    }

    fun setPassword(value: String) {
        password.update { value }
        viewModelScope.launch {
            Settings.setPassword(value)
        }
    }

    fun setUsername(value: String) {
        username.update { value }
        viewModelScope.launch {
            Settings.setUsername(value)
        }
    }


    fun setSelector(selector: ConsumptionSelector) {
        _selector.value = selector
    }

    fun getBillingUnits(): List<ServiceConfigurationBillingUnit> {
        return data.getBillingUnits()
    }

    fun getBillingUntitReferences(): List<BillingUnitReference> {
        return data.getBillingUnitReferences()
    }

    fun getBillingUntitServices(mscnumber: String): Set<Service> {
        return data.getBillingUnitServices(mscnumber)
    }

    fun getBillingUntitResidentialUnits(mscnumber: String): Set<ResidentialUnitReference> {
        return data.getBillingUnitResidentialUnits(mscnumber)
    }


    fun getConsumptionOfUnit(
        selector: ConsumptionSelector,
        periodStart: String? = null,
        periodEnd: String? = null
    ): List<ConsumptionEntity> {
        val consumptions = mutableListOf<ConsumptionEntity>()
        data.consumptionOfTypeOfUnit(selector, periodStart, periodEnd)
            ?.let { consumptions.addAll(it) }
        return consumptions;
    }

    fun minConsumptionOfUnit(
        selector: ConsumptionSelector,
        periodStart: String? = null,
        periodEnd: String? = null
    ): Pair<String, Double>? {
        return data.minConsumptionOfTypeOfUnit(selector, periodStart, periodEnd)
    }

    fun maxConsumptionOfUnit(
        selector: ConsumptionSelector,
        periodStart: String? = null,
        periodEnd: String? = null
    ): Pair<String, Double>? {
        return data.maxConsumptionOfTypeOfUnit(selector, periodStart, periodEnd)
    }

    fun avgConsumptionOfUnit(
        selector: ConsumptionSelector,
        periodStart: String? = null,
        periodEnd: String? = null
    ): Double? {
        return data.avgConsumptionOfTypeOfUnit(selector, periodStart, periodEnd)
    }

    fun sumConsumptionOfUnit(
        selector: ConsumptionSelector,
        periodStart: String? = null,
        periodEnd: String? = null
    ): Double? {
        return data.sumConsumptionOfTypeOfUnit(selector, periodStart, periodEnd)
    }

    fun yearSumConsumptionOfUnit(selector: ConsumptionSelector, year: String) : Double? {
        return data.sumConsumptionOfTypeOfUnit(selector, "$year-01", "$year-12")
    }

    fun yearListOfConsumptionData(selector: ConsumptionSelector) : List<String> {
        val set = mutableSetOf<String>()
        getConsumptionOfUnit(selector).forEach { consumption ->
            set.add(Period(consumption.period).year())
        }
        return set.sorted()
    }
}