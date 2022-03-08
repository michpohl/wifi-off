package com.michaelpohl.wifiservice.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WifiData(
    val ssid: String,
    val cellIDs: List<String>
)

@JsonClass(generateAdapter = true)
data class WifiList(val wifis: List<WifiData>)
