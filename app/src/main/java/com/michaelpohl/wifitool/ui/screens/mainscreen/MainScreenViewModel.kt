package com.michaelpohl.wifitool.ui.screens.mainscreen

import com.michaelpohl.wifiservice.CommandRunner
import com.michaelpohl.wifiservice.looper.MonitoringLooper
import com.michaelpohl.wifiservice.model.WifiData
import com.michaelpohl.wifiservice.repository.StorageRepository
import com.michaelpohl.wifitool.ui.common.UIStateFlowViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainScreenViewModel : UIStateFlowViewModel<MainScreenState>(), KoinComponent {

    private val storage: StorageRepository by inject()
    private val commandRunner: CommandRunner by inject()
    override fun initUIState(): MainScreenState {
        return MainScreenState()
    }

    fun getSavedWifis() {
        updateState(currentState.copy(storage.savedKnownWifis))
    }

    fun getCurrentConnectedWifi() {
        updateState(currentState.copy(currentConnectedWifi = commandRunner.getCurrentConnectedWifi()))
    }

    fun saveWifi(wifi: WifiData) {
        storage.saveWifi(wifi)
    }

    fun deleteWifi(wifi: WifiData) {
        storage.deleteWifi(wifi)
    }
    fun onMonitoringStateChanged(state: MonitoringLooper.State) {
        getCurrentConnectedWifi()
    }
}
