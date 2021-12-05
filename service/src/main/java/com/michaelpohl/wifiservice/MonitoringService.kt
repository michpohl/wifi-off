package com.michaelpohl.wifiservice

import android.app.*
import android.content.Intent
import android.os.*
import com.michaelpohl.wifiservice.CommandRunner.Companion.runShellCommand
import com.michaelpohl.wifiservice.di.serviceModule
import com.michaelpohl.wifiservice.looper.MonitoringLooper
import com.michaelpohl.wifiservice.looper.WifiInstruction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.util.*

class MonitoringService : Service(), KoinComponent {

    private val binder = ServiceBinder()
    private val notificationHandler = NotificationHandler()

    private lateinit var looper: MonitoringLooper

    private lateinit var notificationManager: NotificationManager
    private lateinit var serviceHandler: Handler

    var activityClass: Class<out Activity>? = null
    var serviceState = ServiceState.STOPPED
    var wifiStateListener: ((MonitoringLooper.State) -> Unit)? = null

    fun start() {
        initKoinModule()
        if (serviceState != ServiceState.RUNNING) startService(
            Intent(
                applicationContext,
                MonitoringService::class.java
            )
        )
        serviceState = ServiceState.RUNNING
    }

    private fun initKoinModule() {
        loadKoinModules(serviceModule)
        looper = get { parametersOf({ state: MonitoringLooper.State -> onWifiStateChanged(state) }) }
    }

    override fun onCreate() {
        Timber.d("Service created")
        super.onCreate()
//        setupThread()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("Service started")
        handleNotificationStopClicked(intent)
        CoroutineScope(Dispatchers.Main).launch {
            looper.loop()
        }
        return START_NOT_STICKY
    }

    private fun onWifiStateChanged(state: MonitoringLooper.State) {
        when (state.instruction) {
            WifiInstruction.TURN_OFF -> {
                Timber.i("No known SSIDs visible. Turning off Wifi")
                runShellCommand(ShellCommand.TURN_WIFI_OFF)
            }
            WifiInstruction.TURN_ON -> {
                Timber.i("Connected to known cell tower. Turning on Wifi")
                runShellCommand(ShellCommand.TURN_WIFI_ON)
            }
            WifiInstruction.WAIT -> Timber.d("No change necessary. Waiting")
        }
        wifiStateListener?.invoke(state)
    }

    private fun setupNotification() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        NotificationChannel(
            NOTIFICATION_CHANNEL_ID, "here be app name", // TODO
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            notificationManager.createNotificationChannel(this)
        }
    }

//    private fun setupThread() {
//        val handlerThread = HandlerThread(TAG)
//        handlerThread.start()
//        serviceHandler = Handler(handlerThread.looper)
//    }

    override fun onBind(intent: Intent): IBinder {
        stopForeground(true)
        return binder
    }

    override fun onRebind(intent: Intent) {
        stopForeground(true)
        super.onRebind(intent)
    }

    // activity is gone or backgrounded, so we want to go foreground
    override fun onUnbind(intent: Intent): Boolean {
        Timber.d("onUnbind")

        // if we don't know the activity class yet, we can't properly set up the service and notification
        // and therefore we shouldn't do anything
        activityClass?.let {
            setupNotification()
            startForeground(NOTIFICATION_ID, notificationHandler.buildNotification(this, activityClass!!))
            return true
        }
            ?: Timber.w("No activity class found!")
        return false
    }

    private fun stop() {
        Timber.d("Service stopped")
        serviceState = ServiceState.STOPPED
        stopSelf()
    }

    override fun onDestroy() {
        serviceHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    // if the user wants to end the service from the notification, this gets executed
    private fun handleNotificationStopClicked(intent: Intent?) {
        if (intent?.getBooleanExtra(DID_START_FROM_NOTIFICATION, false) == true) {
//            CoroutineScope(Dispatchers.Default).launch {
//                binder.stop()
//            }
        }
    }

    // TODO let's see if this is the smartest way...
    inner class ServiceBinder : MonitoringServiceBinder() {

        fun getService(): MonitoringService {
            return this@MonitoringService
        }
    }

    companion object {

        // TODO check which places these should go to
        private val TAG = MonitoringService::class.java.simpleName
        const val NOTIFICATION_CHANNEL_ID = "loopy_channel"
        private const val NOTIFICATION_ID = 56479
        const val DID_START_FROM_NOTIFICATION = "started_from_notification"
    }
}

enum class ServiceState { RUNNING, STOPPED
}
