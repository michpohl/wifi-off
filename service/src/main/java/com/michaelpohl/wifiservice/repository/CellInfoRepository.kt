package com.michaelpohl.wifiservice.repository

import com.michaelpohl.wifiservice.CommandRunner.Companion.runShellCommand
import com.michaelpohl.wifiservice.ShellCommand.Companion.CHECK_CELL_TOWERS_COMMAND

class CellInfoRepository {

    fun isWithinReachOfKnownCellTowers(cellIDs : List<String>): Boolean {
        var result = false
        cellIDs.forEach { if (runShellCommand(CHECK_CELL_TOWERS_COMMAND)?.contains(it) == true) result = true }
        return result
    }
}
