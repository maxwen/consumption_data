package com.maxwen.consumption_data.models

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update
import org.openapitools.client.models.ResidentialUnitReference
import org.openapitools.client.models.Service
import org.openapitools.client.models.ServiceConfigurationBillingUnit
import org.openapitools.client.models.UnitOfMeasure

class MainViewModel(prefs: DataStore<Preferences>) : ViewModel() {
    companion object {
        const val MAX_SHOW_YEARS = 4
    }

    private val _loaded = MutableStateFlow(false)
    private val _loadError = MutableStateFlow(false)
    private val _isSetupDone = MutableStateFlow(true)
    private val _isConfigComplete = MutableStateFlow(false)

    private val _progress = MutableStateFlow(false)
    val progress: StateFlow<Boolean> = _progress.asStateFlow()

    private val _selector = MutableStateFlow(ConsumptionSelector())
    val selector: StateFlow<ConsumptionSelector> = _selector.asStateFlow()

    private val data = ConsumptionHub()
    private val _squashResidentialUnits = MutableStateFlow(false)
    val squashResidentialUnits: StateFlow<Boolean> = _squashResidentialUnits.asStateFlow()
    val password = MutableStateFlow("")
    val username = MutableStateFlow("")
    val baseurl = MutableStateFlow("")

    private val _showServices = MutableStateFlow(mutableListOf<Service>())
    val showServices: StateFlow<List<Service>> = _showServices.asStateFlow()

    private val _chartStyle = MutableStateFlow(ChartStyle.Vertical)
    private val _showYears = MutableStateFlow(mutableListOf<String>())
    private val _chartDisplay = MutableStateFlow(ChartDisplay.Yearly)
    private val _focusPeriod = MutableStateFlow("")
    private val _focusPeriodPosition = MutableStateFlow(0)

    var isTwoPaneMode = false
    var isShowYearsInit = false
    var isShowServicesInit = false

    val dataState = combine(
        _loaded,
        _loadError,
        _isSetupDone,
        _isConfigComplete
    ) { loaded, loadError, isSetupDone, isConfigComplete ->
        DataState(loaded, loadError, isSetupDone, isConfigComplete)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(300), DataState(
            loaded = false,
            loadError = false,
            isSetupDone = false,
            isConfigComplete = false
        )
    )

    val graphState = combine(
        _chartStyle,
        _chartDisplay,
        _showYears,
        _focusPeriod,
        _focusPeriodPosition
    ) { chartStyle, chartDisplay, showYears, focusPeriod, focusPeriodPosition ->
        GraphState(chartStyle, chartDisplay, showYears, focusPeriod, focusPeriodPosition)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(300),
        GraphState(ChartStyle.Vertical, ChartDisplay.Yearly, listOf(), "", 0)
    )

    init {
        Settings.myDataStore = prefs

        viewModelScope.launch {
            startProgress()

            password.update { Settings.getPasword() }
            username.update { Settings.getUsername() }
            baseurl.update { Settings.getBaseUrl() }
            _chartStyle.update { Settings.getCharStyle() }
            _isSetupDone.update { Settings.isSetupDone() }
            _isConfigComplete.update { isConfigComplete() }

            try {
                data.load(baseurl.value, username.value, password.value)
                _loaded.update { true }
            } catch (e: Throwable) {
                if (_isConfigComplete.value) {
                    _loadError.update { true }
                }
            } finally {
                stopProgress()
            }
        }
    }

    fun reload() {
        viewModelScope.launch {
            startProgress()
            try {
                _loadError.update { false }
                data.load(baseurl.value, username.value, password.value)
                setSelector(ConsumptionSelector())
                _loaded.update { true }
            } catch (e: Throwable) {
                _loadError.update { true }
                _loaded.update { false }
            } finally {
                stopProgress()
            }
        }
    }

    fun setShowYears(showYears: List<String>) {
        this._showYears.update {
            showYears.toMutableList()
        }
    }

    fun setShowServices(showServices: List<Service>) {
        this._showServices.update {
            showServices.toMutableList()
        }
    }

    fun setFocusPeriodPosition(focusPeriodPosition: Int) {
        this._focusPeriodPosition.update {
            focusPeriodPosition
        }
    }

    fun setChartStyle(style: ChartStyle) {
        _chartStyle.update { style }
        viewModelScope.launch {
            Settings.setChartStyle(style)
        }
    }

    fun setChartDisplay(display: ChartDisplay) {
        _chartDisplay.update { display }
    }

    private fun startProgress() {
        _progress.update { true }
    }

    private fun stopProgress() {
        _progress.update { false }
    }

    private fun setSetupDone() {
        if (!_isSetupDone.value) {
            _isSetupDone.update { true }
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


    fun setSelector(selector: ConsumptionSelector, focusPeriod: String = "") {
        _focusPeriodPosition.update {
            0
        }
        _focusPeriod.update {
            focusPeriod
        }
        _selector.update {
            selector
        }
    }

    fun getBillingUnits(): List<ServiceConfigurationBillingUnit> {
        return data.getBillingUnits()
    }

    fun getBillingUntitServices(billingUnits: List<ServiceConfigurationBillingUnit>): Set<Service> {
        val set = mutableSetOf<Service>()
        billingUnits.forEach { billingUnitData ->
            set.addAll(getBillingUntitServices(billingUnitData.reference.mscnumber))
        }
        return set
    }

    fun getBillingUntitServices(mscnumber: String): Set<Service> {
        return data.getBillingUnitServices(mscnumber)
    }

    fun getBillingUntitResidentialUnits(mscnumber: String): Set<ResidentialUnitReference> {
        return data.getBillingUnitResidentialUnits(mscnumber)
    }

    fun getBillingUnitServicesUnitOfMeasure(
        mscnumber: String,
        service: Service
    ): Set<UnitOfMeasure> {
        return data.getBillingUnitServicesUnitOfMeasure(mscnumber, service)
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
        val selectorPeriod = ConsumptionSelector(
            selector.billingUnit,
            selector.service,
            selector.unitOfMeasure,
            selector.residentialUnit,
            "$year-01",
            "$year-12"
        )
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