package com.michaelpohl.wifiservice.repository

import com.michaelpohl.wifiservice.runShellCommand

class CellInfoRepository {


    private val cellIDs = mutableListOf<String>()
    fun addID(ssid: String) {
    }

    fun removeID(ssid: String) {
    }

    // TODO we might want this suspending
    fun isWithinReachOfKnownCellTowers(): Boolean {
        var result = false
        cellIDs.forEach { if (runShellCommand(CHECK_CELL_TOWERS_COMMAND)?.contains(it) == true) result = true }
        return result
    }

    companion object {

        private const val CHECK_CELL_TOWERS_COMMAND = "su -c dumpsys telephony.registry | grep \"mCi=\""
    }
}
