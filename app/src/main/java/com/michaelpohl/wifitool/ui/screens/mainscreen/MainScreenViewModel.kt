package com.michaelpohl.wifitool.ui.screens.mainscreen

import com.michaelpohl.wifiservice.MonitoringServiceConnection
import com.michaelpohl.wifiservice.looper.MonitoringState
import com.michaelpohl.wifiservice.looper.TimingThresholds
import com.michaelpohl.wifiservice.model.WifiData
import com.michaelpohl.wifiservice.storage.LocalStorage
import com.michaelpohl.wifitool.ui.common.UIStateFlowViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class MainScreenViewModel(private val connection: MonitoringServiceConnection) :
    UIStateFlowViewModel<MainScreenState>(), KoinComponent {

    private val localStorage: LocalStorage by inject()

    override fun initUIState(): MainScreenState {
        return MainScreenState()
    }

    fun getSavedWifis() {
        val isServiceEnabled = localStorage.loadEnabledState()
        updateState(
            currentState.copy(
                wifis = localStorage.savedKnownWifis,
                isServiceEnabled = isServiceEnabled
            )
        )
        toggleServiceEnabled(isServiceEnabled)
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

    fun toggleShowSavedWifis(shouldShow: Boolean) {
        updateState(currentState.copy(showSavedWifis = shouldShow))
    }

    fun toggleShowLogs(shouldShow: Boolean) {
        Timber.d("ShouldShow: $shouldShow")
        updateState(currentState.copy(showLogs = shouldShow))
    }

    fun toggleServiceEnabled(isEnabled: Boolean) {
        Timber.d("Toggling ServiceEnabled: $isEnabled")
        connection.monitoringService?.let {
            it.isEnabled = isEnabled
            localStorage.saveEnabledState(isEnabled = isEnabled)
            updateState(currentState.copy(isServiceEnabled = isEnabled))
        }
            ?: error("Service not accessible from ViewModel!")
    }

    fun onTimingsChanged(timings: TimingThresholds) {
    }
}

