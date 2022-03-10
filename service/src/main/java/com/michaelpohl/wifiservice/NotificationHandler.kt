package com.michaelpohl.wifiservice

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import timber.log.Timber
import com.michaelpohl.design.R as design


class NotificationHandler {

    @SuppressLint("UnspecifiedImmutableFlag")
    fun buildNotification(context: Context, activityClass: Class<out Activity>): Notification {
        Timber.d("Get notification")

        // service intent
        val intent = Intent(context.applicationContext, MonitoringService::class.java)
        intent.putExtra(MonitoringService.DID_START_FROM_NOTIFICATION, true)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val servicePendingIntent = PendingIntent.getService(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        // resume activity intent
        val activityPendingIntent = PendingIntent.getActivity(
            context, 1321, Intent(context, activityClass), PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder =
            NotificationCompat.Builder(context, MonitoringService.NOTIFICATION_CHANNEL_ID)
                .addAction(0, context.getString(R.string.service_notification_buttonOopen), activityPendingIntent)
                .addAction(0, context.getString(R.string.service_notification_button_stop), servicePendingIntent)
                .setContentTitle( context.getString(R.string.service_notification_title))
                .setOngoing(true)

                .setPriority(NotificationManager.IMPORTANCE_LOW
                )
                .setSmallIcon(design.drawable.ic_wifi_eye_icon)
                .setWhen(System.currentTimeMillis())

            createChannel(context)
            builder.setChannelId(MonitoringService.NOTIFICATION_CHANNEL_ID) // Channel ID
        return builder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val description = "wifi-off"

        val channel = NotificationChannel(
            MonitoringService.NOTIFICATION_CHANNEL_ID,
            "app",
            NotificationManager.IMPORTANCE_LOW
        )
            .apply {
                this.description = description
                this.setShowBadge(false)
                this.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
        manager.createNotificationChannel(channel)
    }
}
