package com.michaelpohl.wifiservice.storage

import android.content.SharedPreferences
import com.michaelpohl.wifiservice.model.WifiData
import com.michaelpohl.wifiservice.model.WifiList
import com.squareup.moshi.Moshi
import timber.log.Timber

class LocalStorage(private val sharedPreferences: SharedPreferences, moshi: Moshi) {

    private val adapter = moshi.adapter(WifiList::class.java)
    // TODO what is up with this var and the get method? No point doing it like this
    var savedKnownWifis = loadSavedWifis()
    private set

    fun saveWifi(wifi: WifiData) {
        val knownWifis = loadSavedWifis().wifis.toMutableList()
        val indexOfExistingSameWifi = knownWifis.find { it == wifi }?.let {
            knownWifis.indexOf(it)
        }
        indexOfExistingSameWifi?.let {
            knownWifis.removeAt(it)
        }
        knownWifis.add(wifi)
        with(sharedPreferences.edit()) {
            putString(WIFIS_TAG, adapter.toJson(WifiList(knownWifis)))
            apply()
        }
        savedKnownWifis = WifiList(knownWifis)
    }

    fun deleteWifi(wifi: WifiData) {
        val knownWifis = loadSavedWifis().wifis.toMutableList()
        val indexOfExistingWifiToDelete = knownWifis.find { it == wifi }?.let {
            knownWifis.indexOf(it)
        }
        indexOfExistingWifiToDelete?.let {
            knownWifis.removeAt(it)
        }
        with(sharedPreferences.edit()) {
            putString(WIFIS_TAG, adapter.toJson(WifiList(knownWifis)))
            apply()
        }
        savedKnownWifis = WifiList(knownWifis)
    }

    fun saveEnabledState(isEnabled: Boolean) {
        with (sharedPreferences.edit()) {
            putBoolean(ENABLED_STATE_TAG, isEnabled)
            apply()
        }
        Timber.d("Saved: $isEnabled")
    }

    fun loadEnabledState(): Boolean {
        return sharedPreferences.getBoolean(ENABLED_STATE_TAG, false)
    }

    private fun loadSavedWifis(): WifiList {
        val wifiJson = sharedPreferences.getString(WIFIS_TAG, null)
        return wifiJson?.let {
            adapter.fromJson(wifiJson)
        } ?: WifiList(listOf())
    }

    companion object {

        private const val WIFIS_TAG = "knownwifis"
        private const val ENABLED_STATE_TAG = "isenabled"
    }
}
