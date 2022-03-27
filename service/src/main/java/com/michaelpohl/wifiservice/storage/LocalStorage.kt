package com.michaelpohl.wifiservice.storage

import android.content.SharedPreferences
import com.michaelpohl.wifiservice.looper.TimingThresholds
import com.michaelpohl.wifiservice.model.WifiData
import com.michaelpohl.wifiservice.model.WifiList
import com.squareup.moshi.Moshi
import timber.log.Timber

class LocalStorage(private val sharedPreferences: SharedPreferences, moshi: Moshi) {

    private val wifiAdapter = moshi.adapter(WifiList::class.java)
    private val timingsAdapter = moshi.adapter(TimingThresholds::class.java)

    // public vars for these values so we can query them often without having to go through the whole
    // loading from SharedPreferences
    var savedKnownWifis = loadSavedWifis()
        private set
    var savedTimings = loadTimings()
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
            putString(WIFIS_TAG, wifiAdapter.toJson(WifiList(knownWifis)))
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
            putString(WIFIS_TAG, wifiAdapter.toJson(WifiList(knownWifis)))
            apply()
        }
        savedKnownWifis = WifiList(knownWifis)
    }

    fun saveEnabledState(isEnabled: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(ENABLED_STATE_TAG, isEnabled)
            apply()
        }
        Timber.d("Saved: $isEnabled")
    }

    fun loadEnabledState(): Boolean {
        return sharedPreferences.getBoolean(ENABLED_STATE_TAG, false)
    }

    fun saveTimings(timings: TimingThresholds) {
        with(sharedPreferences.edit()) {
            putString(TIMINGS_TAG, timingsAdapter.toJson(timings))
            apply()
        }
    }

    private fun loadTimings(): TimingThresholds {
        val timingsJson = sharedPreferences.getString(TIMINGS_TAG, null)
        return timingsJson?.let {
            timingsAdapter.fromJson(it)
        } ?: TimingThresholds()
    }

    private fun loadSavedWifis(): WifiList {
        val wifiJson = sharedPreferences.getString(WIFIS_TAG, null)
        return wifiJson?.let {
            wifiAdapter.fromJson(wifiJson)
        } ?: WifiList(listOf())
    }

    companion object {

        private const val WIFIS_TAG = "knownwifis"
        private const val ENABLED_STATE_TAG = "isenabled"
        private const val TIMINGS_TAG = "timingstag"
    }
}
