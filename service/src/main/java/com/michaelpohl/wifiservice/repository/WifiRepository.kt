package com.michaelpohl.wifiservice.repository

import com.michaelpohl.wifiservice.CommandRunner.Companion.runShellCommand
import com.michaelpohl.wifiservice.ShellCommand
import com.michaelpohl.wifiservice.ShellCommand.Companion.CHECK_SSID_COMMAND

class WifiRepository {

    private val ssids = mutableListOf<String>("the good strip")
    fun addSSID(ssid: String) {
    }

    fun removeSSID(ssid: String) {
    }

    // TODO we might want this suspending
    fun isConnectedToAnyValidSSIDs(): Boolean {
        var result = false
        ssids.forEach { if (runShellCommand(CHECK_SSID_COMMAND)?.contains(it) == true) result = true }
        return result
    }

    fun isWifiOn(): Boolean {
        return runShellCommand(ShellCommand.CHECK_WIFI_ON) == "1"
    }
}
