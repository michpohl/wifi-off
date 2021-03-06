package com.michaelpohl.wifiservice

import com.michaelpohl.wifiservice.model.WifiData
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Wrapper class for executing shell commands. The shell commands this app is running are
 * executed by this class, wrapped by functions built around the needed functionalities
 */
class CommandRunner {

    fun getCurrentConnectedWifi(): WifiData? {

        val ssidString = runShellCommand(ShellCommand.CHECK_SSID_COMMAND)
        val splitString = ssidString?.split("\"")
        if (splitString == null || splitString.size < 2) return null
        // The regex works, but we don't need it at this point
//        Timber.d("ssid regex: ${ssidRegex.find(ssidString ?: "")?.groups?.get(0)?.value}")

        val cellTowerID = runShellCommand(ShellCommand.CHECK_CELL_TOWERS_COMMAND)
        return cellTowerID?.let {
            compareCellTowerID(it, splitString)
        }
    }

    private fun compareCellTowerID(cellTowerId: String, splitString: List<String>): WifiData {
        var idMatch = ""
        cellTowerRegex.find(cellTowerId)?.groups?.get(0)?.apply {
            this.value.forEach { if (it.isDigit()) idMatch = idMatch.plus(it) }
        }

        //            Timber.d("cellid: $cellTowerId")
        //            Timber.d("regex: $idMatch")
        // TODO check if this is enough and how we best compare this to saved wifis with multiple cellIDs
        return WifiData(splitString[1], listOf(idMatch))
    }

    fun isWithinReachOfKnownCellTowers(wifis: List<WifiData>): Boolean {
        var result = false
        val command: String? = runShellCommand(ShellCommand.CHECK_CELL_TOWERS_COMMAND)
        val possibleCellIDs = mutableListOf<String>()
        Timber.d("Command: $command")
        wifis.forEach { possibleCellIDs.addAll(it.cellIDs) }
        possibleCellIDs.forEach { if (command?.contains(it) == true) result = true }
        Timber.d("Check cell towers result: $result")
        return result
    }

    fun isConnectedToAnyWifi(): Boolean {
        return (runShellCommand(ShellCommand.CHECK_SSID_COMMAND)?.contains(wifiConnectionString) == true)
    }

    fun isConnectedToAnyValidSSIDs(ssids: List<String>): Boolean {
        var result = false
        ssids.forEach {
            if (runShellCommand(ShellCommand.CHECK_SSID_COMMAND)?.contains(it) == true) result = true
        }
        return result
    }

    fun isWifiOn(): Boolean {
        val commandResult = runShellCommand(ShellCommand.CHECK_WIFI_ON)
        val result = commandResult?.trim() == "1"
        Timber.d("Is wifi on: commandResult: $commandResult, $result")
        return result
    }

    fun turnWifiOn() {
        runShellCommand(ShellCommand.TURN_WIFI_ON)
    }

    fun turnWifiOff() {
        runShellCommand(ShellCommand.TURN_WIFI_OFF)
    }

    companion object {

        private const val wifiConnectionString = "networkId=\""

        //        private val ssidRegex = """=".*",""".toRegex()
        private val cellTowerRegex = """mCi=[0-9]*""".toRegex()

        private fun runShellCommand(command: String): String? {
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

object ShellCommand {

    const val CHECK_CELL_TOWERS_COMMAND = "su -c dumpsys telephony.registry | grep \"mCi=\" -m1"
    const val CHECK_SSID_COMMAND = "su -c dumpsys netstats | grep -E 'iface='"
    const val TURN_WIFI_OFF = "su -c svc wifi disable"
    const val TURN_WIFI_ON = "su -c svc wifi enable"
    const val CHECK_WIFI_ON = "su -c settings get global wifi_on"
}
