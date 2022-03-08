package com.michaelpohl.wifitool.common.util

import android.content.Context
import com.michaelpohl.wifiservice.looper.WifiSituation
import com.michaelpohl.wifitool.R

fun WifiSituation.asString(context: Context) : String {
    return when (this) {
        WifiSituation.WIFI_ON -> context.getString(R.string.wifi_situation_on)
        WifiSituation.WIFI_CONNECTED -> context.getString(R.string.wifi_situation_connected)
        WifiSituation.WIFI_OFF -> context.getString(R.string.wifi_situation_off)
        WifiSituation.UNKNOWN -> context.getString(R.string.wifi_situation_unknown)
    }
}