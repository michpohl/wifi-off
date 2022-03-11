package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.lifecycle.ViewModelProvider
import com.michaelpohl.wifiservice.MonitoringServiceConnection

class MainScreenViewModelFactory(private val serviceConnection: MonitoringServiceConnection) :
    ViewModelProvider.NewInstanceFactory() {
    fun build(): MainScreenViewModel = MainScreenViewModel(serviceConnection)
}
