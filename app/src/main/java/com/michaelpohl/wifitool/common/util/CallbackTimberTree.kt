package com.michaelpohl.wifitool.common.util

import android.content.Context
import android.widget.Toast
import timber.log.Timber

// Timber tree with a callback, so you can do something else with the logs if you want to
class CallbackTimberTree(val onLog: (String) -> Unit) : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, tag, message, t)
        onLog(message)
    }
}
