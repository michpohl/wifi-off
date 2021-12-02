package com.michaelpohl.wifiservice.looper

import com.michaelpohl.wifiservice.repository.CellInfoRepository
import com.michaelpohl.wifiservice.repository.WifiRepository
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.*

class MonitoringLooper(
    private val wifiRepo: WifiRepository,
    private val cellInfoRepo: CellInfoRepository,
    val onStateChanged: (State) -> Unit,
) {

    private var currentState = State()
        set(value) {
            println("new state: $value")
            field = value
            onStateChanged(value)
        }

    private var shouldStop = false
    suspend fun loop() {
        val isConnected = wifiRepo.isConnectedToAnyValidSSIDs()
        Timber.d("Connected? $isConnected")
        if (shouldStop) return
        println("did not stop")
        if (isConnected) handleConnected() else handleDisconnected()
    }

    fun stop() {
        println("stop")
        shouldStop = true
    }

    private suspend fun handleConnected() {
        val now = Date().time
        currentState = currentState.copy(
            lastChecked = now,
            lastConnected = now,
            firstCellSeen = 0,
            isWifiOn = true,
            instruction = WifiInstruction.WAIT
        )
        delay(SCAN_INTERVAL_MILLIS)
        loop()
    }

    private suspend fun handleDisconnected() {
        println("handleDisconnected")
        val now = Date().time
        val isWifiOn = wifiRepo.isWifiOn()
        println("isWifiOn")
        if (isWifiOn) {
            println("wifi on")
            currentState = if (now - currentState.lastConnected > TURN_OFF_THRESHOLD_MILLIS) {
                currentState.copy(
                    lastChecked = now,
                    lastConnected = 0L,
                    isWifiOn = false,
                    instruction = WifiInstruction.TURN_OFF
                )
            } else {
                currentState.copy(
                    lastChecked = now,
                    lastConnected = now,
                    isWifiOn = false,
                    instruction = WifiInstruction.WAIT
                )
            }
        } else {
            println("wifi off")
            currentState = if (cellInfoRepo.isWithinReachOfKnownCellTowers()) {
                println("within reach of cell tower, time since first seen: ${now - currentState.firstCellSeen}")
                if (now - currentState.firstCellSeen > TURN_ON_THRESHOLD_MILLIS) {
                    println("first")
                    currentState.copy(
                        lastChecked = now,
                        isWifiOn = false,
                        instruction = WifiInstruction.TURN_ON
                    )
                } else {
                    println("second")
                    currentState.copy(
                        lastChecked = now,
                        firstCellSeen = if (currentState.firstCellSeen == 0L) now else currentState.firstCellSeen,
                        isWifiOn = false,
                        instruction = WifiInstruction.WAIT
                    )
                }
            } else {
                println("not within reach")
                currentState.copy(
                    lastChecked = now,
                    firstCellSeen = 0L,
                    isWifiOn = false,
                    instruction = WifiInstruction.WAIT
                )
            }
        }
        println("end")
        delay(SCAN_INTERVAL_MILLIS)
        loop()
    }

    data class State(
        val lastChecked: Long = 0L,
        val lastConnected: Long = 0L,
        val firstCellSeen: Long = 0L,
        val isWifiOn: Boolean = false,
        val instruction: WifiInstruction = WifiInstruction.WAIT
    )

    // TODO change to actual values
    companion object {

        const val SCAN_INTERVAL_MILLIS = (1 * 10 * 1000).toLong()
        const val TURN_OFF_THRESHOLD_MILLIS = 1 * 60 * 1000
        const val TURN_ON_THRESHOLD_MILLIS = 1 * 60 * 1000
    }
}

enum class WifiInstruction {
    TURN_OFF, TURN_ON, WAIT
}
