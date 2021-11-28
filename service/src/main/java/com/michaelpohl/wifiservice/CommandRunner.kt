package com.michaelpohl.wifiservice

import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader

fun runShellCommand(command: String): String? {
    Timber.d("run: $command")
    val splitCommand: Array<String> = command.split(" ").toTypedArray()
    Timber.d(splitCommand.joinToString(" "))
    val process = Runtime.getRuntime().exec(splitCommand)
//            val process = Runtime.getRuntime().exec("su -c dumpsys telephony.registry | grep \"mCi=\"")
    val processOutput = BufferedReader(InputStreamReader(process.inputStream)).readText()
    val processError = BufferedReader(InputStreamReader(process.errorStream)).readText()
    Timber.d("output: $processOutput")
    return when {
        processOutput.isNotEmpty() -> processOutput
        processError.isNotEmpty() -> processError
        else -> null
    }
}

