package com.michaelpohl.wifiservice.looper

import com.michaelpohl.wifiservice.CommandRunner
import com.michaelpohl.wifiservice.model.WifiData
import com.michaelpohl.wifiservice.storage.LocalStorage
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.*

class MonitoringLooper(
    private val commandRunner: CommandRunner,
    private val localStorageRepo: LocalStorage,
    val onStateChanged: (MonitoringState) -> Unit,
    val thresholds: TimingThresholds = TimingThresholds()

) {
    private var shouldStop = false
    private var now = 0L

    private var currentState = MonitoringState()
        set(value) {
            Timber.d("new state: $value")
            field = value
            onStateChanged(value)
        }



    suspend fun start() {
        shouldStop = false
        loop()
    }

    fun stop() {
        println("stop")
        shouldStop = true
    }

    fun stopPermanently() {
        stop()
        localStorageRepo.saveEnabledState(false)
    }

    private suspend fun loop() {
        Timber.d("\n***\nloop\n***")
        if (shouldStop) return
        // first we check if wifi is on
        now = Date().time
        if (commandRunner.isWifiOn()) {
            handleWifiOn()
        } else {
            handleWifiOff()
        }
        delay(thresholds.scanInterval)
        loop()
    }

    // if wifi is on, we have to check if we are connected to a known ssid
    private fun handleWifiOn() {
        currentState = currentState.copy(
            wifiTurnedOffAt = null
        )
        Timber.d("handleWifiOn")
        val isConnected = commandRunner.isConnectedToAnyWifi()
        Timber.d(" Is connected: $isConnected")
        if (isConnected) {
            handleConnectedToWifi()
        } else {
            handleNotConnectedToWifi()
        }
    }

    // if connected to a known ssid, we update our timestamps and instruct to WAIT
    private fun handleConnectedToWifi() {
        // TODO clean this up once logic is sound
        val currentConnectedWifi = getCurrentConnectedWifi()
        val wifiToReturn = currentConnectedWifi?.let { current ->
            var savedCopy = localStorageRepo.savedKnownWifis.wifis.find { it.ssid == current.ssid }
            if (savedCopy != null) {
                if (!savedCopy.cellIDs.containsAll(current.cellIDs)) {
                    val newCellIds = (savedCopy.cellIDs + current.cellIDs).distinct()
                    savedCopy = current.copy(cellIDs = newCellIds)
                    localStorageRepo.saveWifi(savedCopy)
                }
            }
            savedCopy
        }

        Timber.d("HandleConnectedToWifi")
        currentState = currentState.copy(
            lastChecked = now,
            lastConnected = now,
            instruction = WifiInstruction.WAIT,
            connectedWifi = wifiToReturn
        )
    }

    // if wifi is on, but we are not connected to a known ssid, then we want to
    // turn wifi off if we are past our turnOffThreshold
    // if we execute this function we also have to erase the connectedWifi
    private fun handleNotConnectedToWifi() {
        Timber.d("HandleNotConnectedToWifi")
        // check threshold, disconnect if qualified
        currentState.lastConnected?.let { lastConnectedTime ->
            currentState = if (now - thresholds.turnOffThreshold > lastConnectedTime) {
                Timber.d("Turning wifi off, past threshold")
                currentState.copy(
                    lastChecked = now,
                    wifiTurnedOffAt = now,
                    instruction = WifiInstruction.TURN_OFF,
                    connectedWifi = null
                )
            } else {

                currentState.copy(
                    lastChecked = now,
                    instruction = WifiInstruction.WAIT,
                    connectedWifi = null
                )
            }
        } ?: run {
            // in case there is no lastConnected timeStamp, we set it now,
            // so we can calculate our times on the next go
            Timber.d("No lastConnected")
            currentState = currentState.copy(
                lastChecked = now,
                lastConnected = now,
                instruction = WifiInstruction.WAIT,
                connectedWifi = null
            )
        }
    }

    // if wifi is turned off, we check if the off duration has already passed our
    // turnedOffThreshold. If so, we call the according function, otherwise just update the state.
    private fun handleWifiOff() {

        // as a security measure let's erase the last connectedWifi from our state first
        currentState = currentState.copy(connectedWifi = null)

        Timber.d("HandleWifiOff")
        // if turnedOffThreshold passed
        currentState.wifiTurnedOffAt?.let {
            Timber.d("Difference: ${now - thresholds.turnedOffMinThreshold - it}")
            if (now - thresholds.turnedOffMinThreshold > it) {
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
            commandRunner.isWithinReachOfKnownCellTowers(localStorageRepo.savedKnownWifis.wifis)
        if (isConnectedToKnownCell) {
            handleConnectedToKnownCell()
        } else {
            currentState =
                currentState.copy(
                    lastChecked = Date().time,
                    instruction = WifiInstruction.WAIT,
                    connectedToKnownCell = false
                )
        }
    }

    // if wifi is off, and we are connected to a known cell past our turnOffThreshold,
    // we instruct to turn the wifi back on
    private fun handleConnectedToKnownCell() {
        Timber.d("HandleConnectedToKnownCell")
        currentState.wifiTurnedOffAt?.let { wifiTurnedOffTime ->

            if (now - thresholds.turnOnThreshold > wifiTurnedOffTime) {
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

    private fun getCurrentConnectedWifi(): WifiData? {
        return commandRunner.getCurrentConnectedWifi()
    }

    companion object {
        // all the default values
        const val DEFAULT_SCAN_INTERVAL_MILLIS = (3 * 60 * 1000).toLong()
        const val DEFAULT_TURN_OFF_THRESHOLD_MILLIS = (3.5 * 60 * 1000).toLong()
        const val DEFAULT_TURN_ON_THRESHOLD_MILLIS = (3.5 * 60 * 1000).toLong()
        const val DEFAULT_TURNED_OFF_MIN_THRESHOLD_MILLIS = (7 * 60 * 1000).toLong()
    }
}

@JsonClass(generateAdapter = true)
data class TimingThresholds(
    val scanInterval: Long = MonitoringLooper.DEFAULT_SCAN_INTERVAL_MILLIS,
    val turnOffThreshold: Long = MonitoringLooper.DEFAULT_TURN_OFF_THRESHOLD_MILLIS,
    val turnOnThreshold: Long = MonitoringLooper.DEFAULT_TURN_ON_THRESHOLD_MILLIS,
    val turnedOffMinThreshold: Long = MonitoringLooper.DEFAULT_TURNED_OFF_MIN_THRESHOLD_MILLIS
)
