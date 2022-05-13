package com.abhinavmarwaha.walletx.models

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Money constructor(private val dataStore: DataStore<Preferences>){

    private val CASH = intPreferencesKey("cash")
    private val CHANGE = intPreferencesKey("change")
    val prefs = EncryptedSharedPreferences()
    val cashflow: Flow<List<Int>> = dataStore.data
        .map { preferences ->
            listOf(preferences[CASH] ?: 0, preferences[CHANGE] ?: 0)
        }

    suspend fun setCash(cash: Int) {
        dataStore.edit { money ->
            money[CASH] = cash
        }
    }

    suspend fun setChange(change: Int) {
        dataStore.edit { money ->
            money[CHANGE] = change
        }
    }

}