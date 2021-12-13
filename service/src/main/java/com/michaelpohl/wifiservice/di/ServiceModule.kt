package com.michaelpohl.wifiservice.di

import com.michaelpohl.wifiservice.looper.MonitoringLooper
import com.michaelpohl.wifiservice.looper.MonitoringState
import org.koin.dsl.module

val serviceModule = module {
    single { (onStateChanged: (MonitoringState) -> Unit) -> MonitoringLooper(get(), get(), onStateChanged) }
}
