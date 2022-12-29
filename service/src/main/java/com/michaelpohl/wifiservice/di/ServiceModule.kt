package com.michaelpohl.wifiservice.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.michaelpohl.wifiservice.looper.MonitoringLooper
import com.michaelpohl.wifiservice.looper.MonitoringState
import com.michaelpohl.wifiservice.looper.WifiConnectionChecker
import org.koin.dsl.module

val serviceModule = module {

    single {
        val context: Context = get()
        val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        WifiConnectionChecker(connectivityManager, wifiManager)
    }

    single { (onStateChanged: (MonitoringState) -> Unit) ->
        MonitoringLooper(get(), get(), get(), onStateChanged)
    }

}
