package com.michaelpohl.wifitool

import android.app.Application
import com.michaelpohl.wifitool.common.util.CallbackTimberTree
import com.michaelpohl.wifitool.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import timber.log.Timber

class WifiToolApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initTimber()
        startKoin { androidContext(this@WifiToolApp) }
        loadKoinModules(appModule)
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
        Timber.d("Timber is on, using DebugTree")
    }
}
