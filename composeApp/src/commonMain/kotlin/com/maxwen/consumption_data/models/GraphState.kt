package com.maxwen.consumption_data.models

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.openapitools.client.models.Service

data class GraphState(
    val style: ChartStyle,
    val display: ChartDisplay,
    val showYears: List<String>,
    val focusPeriod: String,
    val focusPeriodPosition: Int
)
