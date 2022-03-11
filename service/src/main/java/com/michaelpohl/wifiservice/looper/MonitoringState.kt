package com.michaelpohl.wifiservice.looper

import com.michaelpohl.wifiservice.model.WifiData

data class MonitoringState(
    val lastChecked: Long? = null,
    val lastConnected: Long? = null,
    val wifiTurnedOffAt: Long? = null,
    val firstCellSeen: Long? = null,
    val connectedToKnownCell: Boolean = false,
    val isWifiOn: Boolean = false,
    val instruction: WifiInstruction = WifiInstruction.WAIT,
    val connectedWifi: WifiData? = null
) {
    val wifiSituation: WifiSituation = when {
        connectedWifi != null -> WifiSituation.WIFI_CONNECTED
        isWifiOn -> WifiSituation.WIFI_ON
        else -> WifiSituation.WIFI_OFF
    }
}
