package com.michaelpohl.wifiservice.looper

import com.michaelpohl.wifiservice.ShellCommand
import com.michaelpohl.wifiservice.repository.CellInfoRepository
import com.michaelpohl.wifiservice.repository.WifiRepository
import com.michaelpohl.wifiservice.runShellCommand
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.*

class MonitoringLooper(
    private val wifiRepo: WifiRepository,
    private val cellInfoRepo: CellInfoRepository,
    val onStateChanged: (State) -> Unit
) {

    private var currentState = State()
        set(value) {
            field = value
            onStateChanged(value)
        }

    suspend fun loop() {
        val isConnected = wifiRepo.isConnectedToAnyValidSSIDs()
        Timber.d("Connected? $isConnected")
        if (isConnected) handleConnected() else handleDisconnected()
    }

    private suspend fun handleConnected() {
        val now = Date().time
        currentState = currentState.copy(
            lastChecked = now,
            lastConnected = now,
            isConnected = true,
            instruction = WifiInstruction.WAIT
        )
        delay(SCAN_INTERVAL)
        loop()
    }

    private suspend fun handleDisconnected() {
        val now = Date().time
        val isWifiOn = runShellCommand(ShellCommand.CHECK_WIFI_ON) == "1"
        if (isWifiOn) {
            currentState = if (now - currentState.lastConnected > TURN_OFF_THRESHOLD_MILLIS) {
                currentState.copy(lastChecked = now, lastConnected = 0L, isConnected = false, instruction = WifiInstruction.TURN_OFF)
            } else {
                currentState.copy(lastChecked = now, lastConnected = now, isConnected = false, instruction = WifiInstruction.WAIT)
            }
        } else {
            currentState = if (cellInfoRepo.isWithinReachOfKnownCellTowers()) {
                if (now - currentState.firstCellSeen > TURN_ON_THRESHOLD_MILLIS) {
                    currentState.copy(lastChecked = now, firstCellSeen = 0L, isConnected = false, instruction = WifiInstruction.TURN_ON)
                } else {
                    currentState.copy(
                        lastChecked = now,
                        firstCellSeen = if (currentState.firstCellSeen == 0L) now else currentState.firstCellSeen,
                        isConnected = false,
                        instruction = WifiInstruction.WAIT
                    )
                }
            } else {
                currentState.copy(lastConnected = now, firstCellSeen = 0L, isConnected = false, instruction = WifiInstruction.WAIT)
            }
        }
        loop()
    }

    data class State(
        val lastChecked: Long = 0L,
        val lastConnected: Long = 0L,
        val firstCellSeen: Long = 0L,
        val isConnected: Boolean = false,
        val instruction: WifiInstruction = WifiInstruction.WAIT
    )

    // TODO change to actual values
    companion object {

        const val SCAN_INTERVAL = (3 * 1000).toLong()
        const val TURN_OFF_THRESHOLD_MILLIS = 1 * 60 * 1000
        const val TURN_ON_THRESHOLD_MILLIS = 1 * 60 * 1000
    }
}

enum class WifiInstruction {
    TURN_OFF, TURN_ON, WAIT
}
