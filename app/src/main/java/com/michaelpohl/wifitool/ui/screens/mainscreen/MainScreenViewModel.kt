package com.michaelpohl.wifitool.ui.screens.mainscreen

import com.michaelpohl.wifiservice.CommandRunner
import com.michaelpohl.wifiservice.looper.MonitoringLooper
import com.michaelpohl.wifiservice.model.WifiData
import com.michaelpohl.wifiservice.storage.LocalStorage
import com.michaelpohl.wifitool.ui.common.UIStateFlowViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainScreenViewModel : UIStateFlowViewModel<MainScreenState>(), KoinComponent {

    private val localStorage: LocalStorage by inject()
    private val commandRunner: CommandRunner by inject()
    override fun initUIState(): MainScreenState {
        return MainScreenState()
    }

    fun getSavedWifis() {
        updateState(currentState.copy(localStorage.savedKnownWifis))
    }

    fun getCurrentConnectedWifi() {
        updateState(currentState.copy(currentConnectedWifi = commandRunner.getCurrentConnectedWifi()))
    }

    fun saveWifi(wifi: WifiData) {
        localStorage.saveWifi(wifi)
    }

    fun deleteWifi(wifi: WifiData) {
        localStorage.deleteWifi(wifi)
    }
    fun onMonitoringStateChanged(state: MonitoringLooper.State) {
        getCurrentConnectedWifi()
    }
}
