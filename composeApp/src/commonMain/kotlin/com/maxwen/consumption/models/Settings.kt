package com.maxwen.consumption.models

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.first

object Settings {
    private val TAG = "Settings"

    lateinit var myDataStore: DataStore<Preferences>
    private val BASEURL = stringPreferencesKey("baseurl")
    private val USERNAME = stringPreferencesKey("username")
    private val PASSWORD = stringPreferencesKey("password")


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
                settings[USERNAME] = value
            }
        }
    }

    suspend fun getUsername(): String {
        val settings = myDataStore.data.first().toPreferences()
        val value = settings[USERNAME] ?: ""
        return value
    }

    suspend fun setPassword(value: String) {
        Result.runCatching {
            myDataStore.edit { settings ->
                settings[PASSWORD] = value
            }
        }
    }

    suspend fun getPasword(): String {
        val settings = myDataStore.data.first().toPreferences()
        val value = settings[PASSWORD] ?: ""
        return value
    }
}
