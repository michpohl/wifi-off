package com.michaelpohl.wifitool

import android.app.Application
import com.michaelpohl.wifitool.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

class WifiToolApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin { androidContext(this@WifiToolApp) }
        loadKoinModules(appModule)
    }
}
