package com.michaelpohl.wifitool.ui.screens.mainscreen

import com.michaelpohl.wifiservice.MonitoringServiceConnection
import com.michaelpohl.wifiservice.looper.MonitoringState
import com.michaelpohl.wifiservice.model.WifiData
import com.michaelpohl.wifiservice.storage.LocalStorage
import com.michaelpohl.wifitool.ui.common.UIStateFlowViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainScreenViewModel(private val connection: MonitoringServiceConnection) :
    UIStateFlowViewModel<MainScreenState>(), KoinComponent {

    private val localStorage: LocalStorage by inject()

    override fun initUIState(): MainScreenState {
        return MainScreenState()
    }

    fun getSavedWifis() {
        updateState(
            currentState.copy(
                wifis = localStorage.savedKnownWifis,
                isServiceEnabled = localStorage.loadEnabledState()
            )
        )
    }


    fun saveWifi(wifi: WifiData) {
        localStorage.saveWifi(wifi)
        updateState(currentState.copy(wifis = localStorage.savedKnownWifis))
    }

    fun deleteWifi(wifi: WifiData) {
        localStorage.deleteWifi(wifi)
        updateState(currentState.copy(wifis = localStorage.savedKnownWifis))
    }

    fun onMonitoringStateChanged(state: MonitoringState) {
        updateState(
            currentState.copy(
                currentConnectedWifi = state.connectedWifi,
                wifiSituation = state.wifiSituation
            )
        )
    }

    fun onTimberMessage(message: String) {
        updateState(currentState.copy(timberMessages = currentState.timberMessages.apply {
            addMessage(message)
        }))
    }

    fun toggleServiceEnabled(isEnabled: Boolean) {
        connection.monitoringService?.let {
            it.isEnabled = isEnabled
            localStorage.saveEnabledState(isEnabled = isEnabled)
            updateState(currentState.copy(isServiceEnabled = isEnabled))
        }
            ?: error("Service not accessible from ViewModel!")
    }
}

