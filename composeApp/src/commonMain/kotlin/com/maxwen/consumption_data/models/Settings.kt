package com.maxwen.consumption_data.models

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import decode
import encode
import kotlinx.coroutines.flow.first

enum class ChartStyle {
    Horizontal,
    Vertical
}

enum class ChartDisplay {
    Yearly,
    Monthly
}

object Settings {
    private val TAG = "Settings"

    lateinit var myDataStore: DataStore<Preferences>
    private val BASEURL = stringPreferencesKey("baseurl")
    private val USERNAME = stringPreferencesKey("username")
    private val PASSWORD = stringPreferencesKey("password")
    private val SETUP = intPreferencesKey("setup")
    private val CHARTSTYLE = intPreferencesKey("chartstyle")

    suspend fun setSetupDone() {
        Result.runCatching {
            myDataStore.edit { settings ->
                settings[SETUP] = 1
            }
        }
    }

    suspend fun isSetupDone(): Boolean {
        val settings = myDataStore.data.first().toPreferences()
        val value = settings[SETUP] ?: 0
        return value == 1
    }


    suspend fun setBaseUrl(value: String) {
        Result.runCatching {
            myDataStore.edit { settings ->
                settings[BASEURL] = value
            }
        }
    }

    suspend fun getBaseUrl(): String {
        val settings = myDataStore.data.first().toPreferences()
        val value = settings[BASEURL] ?: ""
        return value
    }

    suspend fun setUsername(value: String) {
        Result.runCatching {
            myDataStore.edit { settings ->
                settings[USERNAME] = encode(value)
            }
        }
    }

    suspend fun getUsername(): String {
        val settings = myDataStore.data.first().toPreferences()
        val value = settings[USERNAME] ?: ""
        return decode(value)
    }

    suspend fun setPassword(value: String) {
        Result.runCatching {
            myDataStore.edit { settings ->
                settings[PASSWORD] = encode(value)
            }
        }
    }

    suspend fun getPasword(): String {
        val settings = myDataStore.data.first().toPreferences()
        val value = settings[PASSWORD] ?: ""
        return decode(value)
    }

    suspend fun setChartStyle(chartStyle: ChartStyle) {
        Result.runCatching {
            myDataStore.edit { settings ->
                settings[CHARTSTYLE] = when (chartStyle) {
                    ChartStyle.Vertical -> 0
                    ChartStyle.Horizontal -> 1
                }
            }
        }
    }

    suspend fun getCharStyle(): ChartStyle {
        val settings = myDataStore.data.first().toPreferences()
        val value = when (settings[CHARTSTYLE]) {
            0 -> ChartStyle.Vertical
            1 -> ChartStyle.Horizontal
            else -> {
                ChartStyle.Vertical
            }
        }
        return value
    }
}
