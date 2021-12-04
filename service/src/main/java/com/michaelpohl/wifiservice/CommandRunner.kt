package com.michaelpohl.wifiservice

import com.michaelpohl.wifiservice.model.WifiData
import java.io.BufferedReader
import java.io.InputStreamReader

class CommandRunner {

    fun getCurrentConnectedWifi() : WifiData? {
        // TODO implement
        // get ssid, if ssid exists, get celltowerid
        return null
    }

    companion object {

        fun runShellCommand(command: String): String? {
            val splitCommand: Array<String> = command.split(" ").toTypedArray()
            val process = Runtime.getRuntime().exec(splitCommand)
            val processOutput = BufferedReader(InputStreamReader(process.inputStream)).readText()
            val processError = BufferedReader(InputStreamReader(process.errorStream)).readText()
//            Timber.d("command: $command\noutput: $processOutput")
            return when {
                processOutput.isNotEmpty() -> processOutput
                processError.isNotEmpty() -> processError
                else -> null
            }
        }
    }
}

class ShellCommand {
    companion object {

        const val CHECK_CELL_TOWERS_COMMAND = "su -c dumpsys telephony.registry | grep \"mCi=\" -m1"
        const val CHECK_SSID_COMMAND = "su -c dumpsys netstats | grep -E 'iface='"
        const val TURN_WIFI_OFF = "su -c svc wifi disable"
        const val TURN_WIFI_ON = "su -c svc wifi enable"
        const val CHECK_WIFI_ON = "su -c settings get global wifi_on"
    }
}

