package com.michaelpohl.wifiservice.looper

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TimingThresholds(
    val scanInterval: Long = DEFAULT_SCAN_INTERVAL_MILLIS,
    val turnOffThreshold: Long = DEFAULT_TURN_OFF_THRESHOLD_MILLIS,
    val turnOnThreshold: Long = DEFAULT_TURN_ON_THRESHOLD_MILLIS,
    val turnedOffMinThreshold: Long = DEFAULT_TURNED_OFF_MIN_THRESHOLD_MILLIS
) {

    companion object {

        // all the default values
        const val DEFAULT_SCAN_INTERVAL_MILLIS = (3 * 60 * 1000).toLong() // 3min
        const val DEFAULT_TURN_OFF_THRESHOLD_MILLIS = (3.5 * 60 * 1000).toLong() // 3.5min
        const val DEFAULT_TURN_ON_THRESHOLD_MILLIS = (3.5 * 60 * 1000).toLong() // 3.5 min
        const val DEFAULT_TURNED_OFF_MIN_THRESHOLD_MILLIS = (7 * 60 * 1000).toLong() // 7 min
        const val GENERAL_MIN_VALUE = (6 * 1000).toLong() // 6 sec
    }
}
