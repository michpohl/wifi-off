package com.michaelpohl.wifiservice.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WifiInfo(
    val ssid: String,
    val cellID: String
)

@JsonClass(generateAdapter = true)
data class WifiList(val wifis: List<WifiInfo>)
