package com.maxwen.consumption_data.models

data class DataState(
    val loaded: Boolean,
    val loadError: Boolean,
    val isSetupDone: Boolean,
    val isConfigComplete: Boolean
)

