package com.michaelpohl.wifitool.di

import android.content.Context
import com.michaelpohl.wifiservice.CommandRunner
import com.michaelpohl.wifiservice.storage.LocalStorage
import com.squareup.moshi.Moshi
import org.koin.dsl.module

val appModule = module {
    single { get<Context>().getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE) }
    single { Moshi.Builder().build() }
    single { CommandRunner() }
    single { LocalStorage(get(), get()) }
}

const val SHARED_PREFS_KEY = "wifitoolprefs"
