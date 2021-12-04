package com.michaelpohl.wifiservice.repository

import android.content.SharedPreferences
import com.michaelpohl.wifiservice.model.WifiData
import com.michaelpohl.wifiservice.model.WifiList
import com.squareup.moshi.Moshi

class PersistenceRepository(val sharedPreferences: SharedPreferences, val moshi: Moshi) {

    private val adapter = moshi.adapter(WifiList::class.java)

    fun getSavedWifis(): WifiList {
        val wifiJson = sharedPreferences.getString(WIFIS_TAG, null)
        return wifiJson?.let {
            adapter.fromJson(wifiJson)
        } ?: WifiList(listOf())
    }

    fun saveWifi(wifi: WifiData) {
        val knownWifis = getSavedWifis().wifis.toMutableList()
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
    }

    fun deleteWifi(wifi: WifiData) {
        val knownWifis = getSavedWifis().wifis.toMutableList()
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
    }

    companion object {

        private const val WIFIS_TAG = "knownwifis"
    }
}
