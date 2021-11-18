package com.michaelpohl.service

import android.app.Activity
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import timber.log.Timber

class MonitoringServiceConnection(private val activityClass: Class<out Activity>) : ServiceConnection {

    private var monitoringInterface: MonitoringService.ServiceBinder? = null

    var onServiceConnectedListener: ((MonitoringService.ServiceBinder) -> Unit)? = null

    var monitoringService: MonitoringService? = null
        private set

    fun requestInterface(): MonitoringService.ServiceBinder? {
        return monitoringInterface
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        Timber.d("Service connected")
        val binder = service as MonitoringService.ServiceBinder
        this@MonitoringServiceConnection.monitoringService = binder.getService().apply {
            activityClass = this@MonitoringServiceConnection.activityClass
            start()
        }
        monitoringInterface = binder
        onServiceConnectedListener?.invoke(binder)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        Timber.d("Service disconnected")
    }
}

