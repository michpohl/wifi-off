package com.michaelpohl.wifiservice.repository

import com.michaelpohl.wifiservice.ShellCommand.Companion.CHECK_SSID_COMMAND
import com.michaelpohl.wifiservice.runShellCommand

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

}
