package com.michaelpohl.wifiservice.looper

import com.michaelpohl.wifiservice.CommandRunner
import com.michaelpohl.wifiservice.storage.LocalStorage
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.*

class MonitoringLooper(
    private val commandRunner: CommandRunner,
    private val localStorageRepo: LocalStorage,
    val onStateChanged: (MonitoringState) -> Unit,
) {

    private var shouldStop = false
    private var currentState = MonitoringState()
        set(value) {
            println("new state: $value")
            field = value
            onStateChanged(value)
        }

    var scanInterval = DEFAULT_SCAN_INTERVAL_MILLIS
    var turnOffThreshold = DEFAULT_TURN_OFF_THRESHOLD_MILLIS
    var turnOnThreshold = DEFAULT_TURN_ON_THRESHOLD_MILLIS
    var turnedOffMinThreshold = DEFAULT_TURNED_OFF_MIN_THRESHOLD_MILLIS
    suspend fun loop() {
        Timber.d("\n***\nloop\n***")
        if (shouldStop) return
        if (commandRunner.isWifiOn()) {
            handleWifiOn()
        } else {
            handleWifiOff()
        }
        if (!shouldStop) {
            delay(scanInterval)
            loop()
        }
    }

    private fun handleWifiOn() {
        Timber.d("handleWifiOn")
        val isConnected = commandRunner.isConnectedToAnyValidSSIDs(localStorageRepo.savedKnownWifis.wifis.map { it.ssid })
        if (isConnected) {
            handleConnectedToWifi()
        } else {
            handleNotConnectedToWifi()
        }
    }

    private fun handleConnectedToWifi() {
        Timber.d("HandleConnectedToWifi")
        val now = Date().time
        currentState = currentState.copy(lastChecked = now, lastConnected = now, instruction = WifiInstruction.WAIT)
    }

    private fun handleNotConnectedToWifi() {
        Timber.d("HandleNotConnectedToWifi")
        // check threshold, disconnect if qualified
        val now = Date().time
        currentState.lastConnected?.let {
            if (now - turnOffThreshold > it) {
                currentState =
                    currentState.copy(lastChecked = now, wifiTurnedOffAt = now, instruction = WifiInstruction.TURN_OFF)
            }
        } ?: run { currentState.copy(lastChecked = now, lastConnected = now) }
    }

    private fun handleWifiOff() {
        Timber.d("HandleWifiOff")
        // if turnedOffThreshold passed
        val now = Date().time
        currentState.wifiTurnedOffAt?.let {
            Timber.d("Difference: ${now - turnedOffMinThreshold - it}")
            if (now - turnedOffMinThreshold > it) {
                handlePastDisconnectedThreshold()
            } else {
                currentState = currentState.copy(lastChecked = now, lastConnected = null, instruction = WifiInstruction.WAIT)
            }
        } ?: run { currentState = currentState.copy(lastChecked = now, lastConnected = null, wifiTurnedOffAt = now) }
    }

    private fun handlePastDisconnectedThreshold() {
        Timber.d("HandlePastDisconnectThreshold")
        val isConnectedToKnownCell =
            commandRunner.isWithinReachOfKnownCellTowers(localStorageRepo.savedKnownWifis.wifis.map { it.cellID })
        if (isConnectedToKnownCell) {
            handleConnectedToKnownCell()
        } else {
            currentState =
                currentState.copy(
                    lastChecked = Date().time,
                    wifiTurnedOffAt = null,
                    instruction = WifiInstruction.TURN_ON
                )
        }
    }

    private fun handleConnectedToKnownCell() {
        Timber.d("HandleConnectedToKnownCell")
        val now = Date().time
        currentState.wifiTurnedOffAt?.let {

            if (now - turnOnThreshold > it) {
                currentState = currentState.copy(lastChecked = now, instruction = WifiInstruction.TURN_ON)
            }
        } ?: run { currentState = currentState.copy(lastChecked = now, wifiTurnedOffAt = now) }
    }

    fun stop() {
        println("stop")
        shouldStop = true
    }

    companion object {

        // all the default values
        const val DEFAULT_SCAN_INTERVAL_MILLIS = (1 * 10 * 100).toLong()
        const val DEFAULT_TURN_OFF_THRESHOLD_MILLIS = (1 * 60 * 500).toLong()
        const val DEFAULT_TURN_ON_THRESHOLD_MILLIS = (1 * 60 * 500).toLong()
        const val DEFAULT_TURNED_OFF_MIN_THRESHOLD_MILLIS = (1 * 60 * 100).toLong()
    }
}

data class MonitoringState(
    val lastChecked: Long? = null,
    val lastConnected: Long? = null,
    val wifiTurnedOffAt: Long? = null,
    val firstCellSeen: Long? = null,
    val isWifiOn: Boolean = false,
    val instruction: WifiInstruction = WifiInstruction.WAIT
)

enum class WifiInstruction {
    TURN_OFF, TURN_ON, WAIT
}
