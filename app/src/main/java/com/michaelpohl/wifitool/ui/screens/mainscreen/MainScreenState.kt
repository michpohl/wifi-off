package com.michaelpohl.wifitool.ui.screens.mainscreen

import com.michaelpohl.wifiservice.looper.WifiSituation
import com.michaelpohl.wifiservice.model.WifiData
import com.michaelpohl.wifiservice.model.WifiList

data class MainScreenState(
    val wifis: WifiList = WifiList(listOf()),
    val currentConnectedWifi: WifiData? = null,
    val timberMessages: StringBuffer = StringBuffer(),
    val wifiSituation: WifiSituation = WifiSituation.UNKNOWN,
    val isServiceEnabled: Boolean = false
) {
    val isInitialState = wifis.wifis.isEmpty() && currentConnectedWifi == null
    val isCurrentWifiAlreadySaved = wifis.wifis.contains(currentConnectedWifi)
}

// TODO move to better place
class StringBuffer {
    private val list = mutableListOf<String>()

    override fun equals(other: Any?): Boolean {
        return false
    }

    fun addMessage(string: String) {
        if (list.size > 24) list.removeAt(0)
        list.add(string)
    }

    fun get(): List<String> {
        return list
    }
}