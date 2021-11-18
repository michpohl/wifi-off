package com.michaelpohl.service

import android.app.*
import android.content.Intent
import android.media.session.MediaSession
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber
import java.util.*

class MonitoringService : Service() {

    private val binder = ServiceBinder()
    private val notificationHandler = NotificationHandler()
    private lateinit var session: MediaSession

    private lateinit var notificationManager: NotificationManager
    private lateinit var serviceHandler: Handler

    var activityClass: Class<out Activity>? = null
    var serviceState = ServiceState.STOPPED
    fun start() {
        if (serviceState != ServiceState.RUNNING) startService(Intent(applicationContext, MonitoringService::class.java))
        serviceState = ServiceState.RUNNING
    }

    override fun onCreate() {
        Timber.d("Service created")
        super.onCreate()
//        setupThread()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("Service started")
        handleNotificationStopClicked(intent)
        return START_NOT_STICKY
    }

    private fun setupNotification() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID, "here be app name", // TODO
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                notificationManager.createNotificationChannel(this)
            }
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

        // if we're not playing, no need to go foreground

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
        session.isActive = false
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
