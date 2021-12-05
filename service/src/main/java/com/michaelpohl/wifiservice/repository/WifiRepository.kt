package com.michaelpohl.wifiservice.repository

import com.michaelpohl.wifiservice.CommandRunner.Companion.runShellCommand
import com.michaelpohl.wifiservice.ShellCommand
import com.michaelpohl.wifiservice.ShellCommand.Companion.CHECK_SSID_COMMAND

class WifiRepository {

    fun isConnectedToAnyValidSSIDs(ssids: List<String>): Boolean {
        var result = false
        ssids.forEach { if (runShellCommand(CHECK_SSID_COMMAND)?.contains(it) == true) result = true }
        return result
    }

    fun isWifiOn(): Boolean {
        return runShellCommand(ShellCommand.CHECK_WIFI_ON) == "1"
    }
}
