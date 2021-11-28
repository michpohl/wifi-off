package com.michaelpohl.wifiservice.looper

import com.michaelpohl.wifiservice.repository.CellInfoRepository
import com.michaelpohl.wifiservice.repository.WifiRepository
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.*

class MonitoringLooper(
    val wifiRepo: WifiRepository,
    val cellInfoRepo: CellInfoRepository,
    val onStateChanged: (State) -> Unit
) {

    private var state = State()
        set(value) {
            field = value
            onStateChanged(value)
        }

    suspend fun loop() {
        val isConnected = wifiRepo.isConnectedToAnyValidSSIDs()
        val timeStamp = Date().time
        val instruction = if (!isConnected && timeStamp - state.lastChecked > TURN_OFF_THRESHOLD_MILLIS) {
            WifiInstruction.TURN_OFF
        } else WifiInstruction.WAIT
        state = state.copy(
            lastChecked = if (!isConnected) timeStamp else state.lastChecked,
            isConnected = isConnected,
            instruction = instruction
        )
        delay(60 * 100) // TODO set fixed time
        Timber.d("Repeat loop")
        loop()
    }

    data class State(
        val lastChecked: Long = 0L,
        val isConnected: Boolean = false,
        val instruction: WifiInstruction = WifiInstruction.WAIT
    )

    companion object {

        const val TURN_OFF_THRESHOLD_MILLIS = 1 * 60 * 1000
    }
}

enum class WifiInstruction {
    TURN_OFF, TURN_ON, WAIT
}
