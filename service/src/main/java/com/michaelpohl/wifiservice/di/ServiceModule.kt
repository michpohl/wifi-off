package com.michaelpohl.wifiservice.di

import com.michaelpohl.wifiservice.CommandRunner
import com.michaelpohl.wifiservice.looper.MonitoringLooper
import org.koin.dsl.module

val serviceModule = module {
    single { CommandRunner() }
    single { (onStateChanged: (MonitoringLooper.State) -> Unit) -> MonitoringLooper(get(),get(), onStateChanged) }
}
