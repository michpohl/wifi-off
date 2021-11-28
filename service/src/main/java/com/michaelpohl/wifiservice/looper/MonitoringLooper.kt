package com.michaelpohl.wifiservice.looper

import com.michaelpohl.wifiservice.repository.WifiRepository
import kotlinx.coroutines.delay
import timber.log.Timber

class MonitoringLooper(
    val wifiRepo: WifiRepository,
//    val cellInfoRepo: CellInfoRepository,
    val onStateChanged: (State) -> Unit
) {

    private var state = State()
        set(value) {
            field = value
            onStateChanged(value)
        }

    suspend fun loop() {
        val isConnected = wifiRepo.isConnectedToAnyValidSSIDs()
        state = state.copy(isConnected = isConnected)
        delay(60 * 100) // TODO set fixed time
        Timber.d("Repeat loop")
        loop()
    }

    data class State(
        val lastChecked: Long = 0L,
        val isConnected: Boolean = false,
        val instruction: WifiInstruction = WifiInstruction.WAIT
    )
}

enum class WifiInstruction {
    TURN_OFF, TURN_ON, WAIT
}
