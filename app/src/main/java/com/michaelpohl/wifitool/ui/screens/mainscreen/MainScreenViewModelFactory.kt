package com.michaelpohl.wifitool.ui.screens.mainscreen

import android.content.ServiceConnection
import androidx.lifecycle.ViewModelProvider

class MainScreenViewModelFactory(private val serviceConnection: ServiceConnection) :
    ViewModelProvider.NewInstanceFactory() {
    fun build(): MainScreenViewModel = MainScreenViewModel(serviceConnection)
}