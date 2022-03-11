package com.michaelpohl.wifiservice.di

import com.michaelpohl.wifiservice.looper.MonitoringLooper
import com.michaelpohl.wifiservice.looper.MonitoringState
import com.michaelpohl.wifiservice.looper.TimingThresholds
import org.koin.dsl.module

val serviceModule = module {
    single { (onStateChanged: (MonitoringState) -> Unit) ->
        MonitoringLooper(get(), get(), TimingThresholds(), onStateChanged)
    }
}

const val SHARED_PREFS_KEY = "wifitoolprefs"
