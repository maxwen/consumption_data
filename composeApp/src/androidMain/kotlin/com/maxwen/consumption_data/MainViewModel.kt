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
    private val _loadError = MutableStateFlow(false)
    val loadError: StateFlow<Boolean> = _loadError.asStateFlow()

    private val _selector = MutableStateFlow(ConsumptionSelector())
    val selector: StateFlow<ConsumptionSelector> = _selector.asStateFlow()
    private val data = ConsumptionHub()
    private val _squashResidentialUnits = MutableStateFlow(false)
    val squashResidentialUnits: StateFlow<Boolean> = _squashResidentialUnits.asStateFlow()
    val password = MutableStateFlow("")
    val username = MutableStateFlow("")
    val baseurl = MutableStateFlow("")
    private val isSetupDone = MutableStateFlow(true)
    private val _isConfigComplete = MutableStateFlow(false)
    val isConfigComplete: StateFlow<Boolean> = _isConfigComplete.asStateFlow()

    init {
        viewModelScope.launch {
            password.update { Settings.getPasword() }
            username.update { Settings.getUsername() }
            baseurl.update { Settings.getBaseUrl() }
            isSetupDone.update { Settings.isSetupDone() }
            _isConfigComplete.update { isConfigComplete() }

            try {
                data.load(baseurl.value, username.value, password.value)
                _loaded.value = true
            } catch (e: Throwable) {
                _loadError.value = true
            }
        }
    }

    fun reload() {
        viewModelScope.launch {
            try {
                data.load(baseurl.value, username.value, password.value)
                _loadError.value = false
                _loaded.value = true
            } catch (e: Throwable) {
                _loadError.value = true
                _loaded.value = false
            }
        }
    }

    fun isSetupDone(): Boolean {
        return isSetupDone.value
    }

    private fun setSetupDone() {
        if (!isSetupDone()) {
            isSetupDone.update { true }
            viewModelScope.launch {
                Settings.setSetupDone()
            }
        }
    }

    private fun isConfigComplete(): Boolean {
        return baseurl.value.isNotEmpty() && username.value.isNotEmpty() && password.value.isNotEmpty()
    }

    fun setBaseUrl(value: String) {
        baseurl.update { value }
        _isConfigComplete.update { isConfigComplete() }
        setSetupDone()
        viewModelScope.launch {
            Settings.setBaseUrl(value)
        }
    }

    fun setPassword(value: String) {
        password.update { value }
        _isConfigComplete.update { isConfigComplete() }
        setSetupDone()
        viewModelScope.launch {
            Settings.setPassword(value)
        }
    }

    fun setUsername(value: String) {
        username.update { value }
        _isConfigComplete.update { isConfigComplete() }
        setSetupDone()
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

    fun getBillingUnitData(mscnumber: String): ServiceConfigurationBillingUnit? {
        return data.getBillingUnitData(mscnumber)
    }

    fun getBillingUntitServices(mscnumber: String): Set<Service> {
        return data.getBillingUnitServices(mscnumber)
    }

    fun getBillingUntitResidentialUnits(mscnumber: String): Set<ResidentialUnitReference> {
        return data.getBillingUnitResidentialUnits(mscnumber)
    }


    fun getConsumptionOfUnit(
        selector: ConsumptionSelector,
    ): List<ConsumptionEntity> {
        val consumptions = mutableListOf<ConsumptionEntity>()
        data.consumptionOfTypeOfUnit(selector)
            ?.let { consumptions.addAll(it) }
        return consumptions;
    }

    fun minConsumptionOfUnit(
        selector: ConsumptionSelector,
    ): Pair<String, Double>? {
        return data.minConsumptionOfTypeOfUnit(selector)
    }

    fun maxConsumptionOfUnit(
        selector: ConsumptionSelector,
    ): Pair<String, Double>? {
        return data.maxConsumptionOfTypeOfUnit(selector)
    }

    fun avgConsumptionOfUnit(
        selector: ConsumptionSelector,
    ): Double? {
        return data.avgConsumptionOfTypeOfUnit(selector)
    }

    fun sumConsumptionOfUnit(
        selector: ConsumptionSelector,
    ): Double? {
        return data.sumConsumptionOfTypeOfUnit(selector)
    }

    fun yearSumConsumptionOfUnit(selector: ConsumptionSelector, year: String): Double? {
        val selectorPeriod = ConsumptionSelector(selector.billingUnit, selector.service, selector.residentialUnit, "$year-01", "$year-12")
        return data.sumConsumptionOfTypeOfUnit(selectorPeriod)
    }

    fun yearListOfConsumptionData(selector: ConsumptionSelector): List<String> {
        val set = mutableSetOf<String>()
        getConsumptionOfUnit(selector).forEach { consumption ->
            set.add(Period(consumption.period).year())
        }
        return set.sorted()
    }
}