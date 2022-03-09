package com.michaelpohl.wifiservice.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WifiData(
    val ssid: String,
    val cellIDs: List<String>
) {
    override fun equals(other: Any?): Boolean {
        return other is WifiData && other.ssid == this.ssid
    }
}

//TODO replace with simple list
@JsonClass(generateAdapter = true)
data class WifiList(val wifis: List<WifiData>)
