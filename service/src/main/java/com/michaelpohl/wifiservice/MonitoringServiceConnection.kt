package com.michaelpohl.wifiservice

import android.app.Activity
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.michaelpohl.wifiservice.looper.MonitoringState
import timber.log.Timber

class MonitoringServiceConnection(private val activityClass: Class<out Activity>) : ServiceConnection {

    private var monitoringInterface: MonitoringService.ServiceBinder? = null

    var onServiceConnectedListener: ((MonitoringService.ServiceBinder) -> Unit)? = null

    var monitoringService: MonitoringService? = null
        private set

    var wifiStateListener: ((MonitoringState) -> Unit)? = null
        set(value) {
            field = value
            if (value == null) return
            monitoringService?.let {
                it.wifiStateListener = value
            } ?: Timber.w("Could not set listener because the service was not present. Wait until service is set")
        }

//    fun requestInterface(): MonitoringService.ServiceBinder? {
//        return monitoringInterface
//    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        Timber.d("Service connected")
        val binder = service as MonitoringService.ServiceBinder
        this@MonitoringServiceConnection.monitoringService = binder.getService().apply {
            activityClass = this@MonitoringServiceConnection.activityClass
            initService()
        }
        monitoringInterface = binder
        onServiceConnectedListener?.invoke(binder)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        Timber.d("Service disconnected")
    }
}

