package com.michaelpohl.wifitool

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WifiToolApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {androidContext(this@WifiToolApp) }
    }
}
