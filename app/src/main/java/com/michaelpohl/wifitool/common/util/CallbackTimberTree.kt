package com.michaelpohl.wifitool.common.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import timber.log.Timber

// Timber tree with a callback, so you can do something else with the logs if you want to
class CallbackTimberTree(val onLog: (String) -> Unit) : Timber.DebugTree(),
    SimpleActivityStateCallBack {

    private var shouldToast = false

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, tag, message, t)
        onLog(message)
    }

    override fun onActivityResumed(activity: Activity) {
        shouldToast = true
    }

    override fun onActivityPaused(activity: Activity) {
        shouldToast = false
    }

    override fun onActivityStopped(activity: Activity) {
        shouldToast = false
    }
}

private interface SimpleActivityStateCallBack : Application.ActivityLifecycleCallbacks {

    override fun onActivityResumed(activity: Activity)
    override fun onActivityPaused(activity: Activity)
    override fun onActivityStopped(activity: Activity)

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        onActivityResumed(activity)
    }
    override fun onActivityStarted(activity: Activity) {
        onActivityResumed(activity)
    }
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}

