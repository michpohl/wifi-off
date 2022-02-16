package com.michaelpohl.wifiservice.looper

import com.michaelpohl.wifiservice.CommandRunner
import com.michaelpohl.wifiservice.storage.LocalStorage
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.*

// TODO define only one now per loop, not one per function
class MonitoringLooper(
    private val commandRunner: CommandRunner,
    private val localStorageRepo: LocalStorage,
    val onStateChanged: (MonitoringState) -> Unit,
) {

    private var shouldStop = false
    private var currentState = MonitoringState()
        set(value) {
            Timber.d("new state: $value")
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
        // first we check if wifi is on
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

    // if wifi is on, we have to check if we are connected to a known ssid
    private fun handleWifiOn() {
        Timber.d("handleWifiOn")
        val isConnected =
            commandRunner.isConnectedToAnyValidSSIDs(localStorageRepo.savedKnownWifis.wifis.map { it.ssid })
        if (isConnected) {
            handleConnectedToWifi()
        } else {
            handleNotConnectedToWifi()
        }
    }

    // if connected to a known ssid, we update our timestamps and instruct to WAIT
    private fun handleConnectedToWifi() {
        Timber.d("HandleConnectedToWifi")
        val now = Date().time
        currentState = currentState.copy(
            lastChecked = now,
            lastConnected = now,
            instruction = WifiInstruction.WAIT
        )
    }

    // if wifi is on, but we are not connected to a known ssid, then we want to
    // turn wifi off if we are past our turnOffThreshold
    private fun handleNotConnectedToWifi() {
        Timber.d("HandleNotConnectedToWifi")
        // check threshold, disconnect if qualified
        val now = Date().time
        currentState.lastConnected?.let { lastConnectedTime ->
            if (now - turnOffThreshold > lastConnectedTime) {
                Timber.d("Turning wifi off, past threshold")
                currentState =
                    currentState.copy(
                        lastChecked = now,
                        wifiTurnedOffAt = now,
                        instruction = WifiInstruction.TURN_OFF
                    )
            } else {
                currentState = currentState.copy(
                    lastChecked = now,
                    instruction = WifiInstruction.WAIT
                )
            }
        } ?: run {
            // in case there is no lastConnected timeStamp, we set it now,
            // so we can calculate our times on the next go
            currentState.copy(
                lastChecked = now,
                lastConnected = now,
                instruction = WifiInstruction.WAIT
            )
        }
    }

    // if wifi is turned off, we check if the off duration has already passed our
    // turnedOffThreshold. If so, we call the according function, otherwise just update the state.
    private fun handleWifiOff() {
        Timber.d("HandleWifiOff")
        // if turnedOffThreshold passed
        val now = Date().time
        currentState.wifiTurnedOffAt?.let {
            Timber.d("Difference: ${now - turnedOffMinThreshold - it}")
            if (now - turnedOffMinThreshold > it) {
                handlePastWifiOffThreshold()
            } else {
                currentState = currentState.copy(
                    lastChecked = now,
                    lastConnected = null,
                    instruction = WifiInstruction.WAIT
                )
            }
        } ?: run {
            currentState =
                currentState.copy(
                    lastChecked = now,
                    lastConnected = null,
                    wifiTurnedOffAt = now,
                    instruction = WifiInstruction.WAIT
                )
        }
    }

    // if wifi is off long enough already, we check if we can see any known cell tower
    // if so, we handle that case. Otherwise, we just update our state
    private fun handlePastWifiOffThreshold() {
        Timber.d("HandlePastWifiOffThreshold")
        val isConnectedToKnownCell =
            commandRunner.isWithinReachOfKnownCellTowers(localStorageRepo.savedKnownWifis.wifis.map { it.cellID })
        if (isConnectedToKnownCell) {
            handleConnectedToKnownCell()
        } else {
            currentState =
                currentState.copy(
                    lastChecked = Date().time,
                    instruction = WifiInstruction.WAIT
                )
        }
    }

    // if wifi is off, and we are connected to a known cell past our turnOffThreshold,
    // we instruct to turn the wifi back on
    private fun handleConnectedToKnownCell() {
        Timber.d("HandleConnectedToKnownCell")
        val now = Date().time
        currentState.wifiTurnedOffAt?.let { wifiTurnedOffTime ->

            if (now - turnOnThreshold > wifiTurnedOffTime) {
                Timber.d("Past threshold, turning wifi on")
                currentState =
                    currentState.copy(
                        lastChecked = now,
                        wifiTurnedOffAt = null,
                        instruction = WifiInstruction.TURN_ON
                    )
            }
        } ?: run {
            // if we have no wifiTurnedOffAt set, we do it now, so it is present on the next run
            currentState = currentState.copy(
                lastChecked = now,
                wifiTurnedOffAt = now,
                instruction = WifiInstruction.WAIT
            )
        }
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
