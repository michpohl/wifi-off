package com.michaelpohl.wifiservice.repository

import com.michaelpohl.wifiservice.CommandRunner.Companion.runShellCommand
import com.michaelpohl.wifiservice.ShellCommand.Companion.CHECK_CELL_TOWERS_COMMAND

class CellInfoRepository {

    private val cellIDs = mutableListOf("5424399")
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
}
