package com.michaelpohl.wifitool.ui.screens.mainscreen

import com.michaelpohl.wifiservice.model.WifiData
import com.michaelpohl.wifiservice.model.WifiList

data class MainScreenState(
    val wifis: WifiList = WifiList(listOf()),
    val currentConnectedWifi: WifiData? = null
) {
    val isInitialState = wifis.wifis.isEmpty() && currentConnectedWifi == null
}
