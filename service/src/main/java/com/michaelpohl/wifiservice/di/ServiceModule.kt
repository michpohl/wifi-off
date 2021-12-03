package com.michaelpohl.wifiservice.di

import com.michaelpohl.wifiservice.looper.MonitoringLooper
import com.michaelpohl.wifiservice.repository.CellInfoRepository
import com.michaelpohl.wifiservice.repository.WifiRepository
import org.koin.dsl.module

val serviceModule = module {
    single { WifiRepository() }
    single { CellInfoRepository() }
    single { (onStateChanged: (MonitoringLooper.State) -> Unit) -> MonitoringLooper(get(), get(), onStateChanged) }
}
