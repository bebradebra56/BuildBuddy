package com.buildsof.budsde.gort.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.buildsof.budsde.BuildBuddyActivity
import com.buildsof.budsde.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val BUILD_BUDDY_CHANNEL_ID = "build_buddy_notifications"
private const val BUILD_BUDDY_CHANNEL_NAME = "BuildBuddy Notifications"
private const val BUILD_BUDDY_NOT_TAG = "BuildBuddy"

class BuildBuddyPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                buildBuddyShowNotification(it.title ?: BUILD_BUDDY_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                buildBuddyShowNotification(it.title ?: BUILD_BUDDY_NOT_TAG, it.body ?: "", data = null)
            }
        }

    }

    private fun buildBuddyShowNotification(title: String, message: String, data: String?) {
        val buildBuddyNotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                BUILD_BUDDY_CHANNEL_ID,
                BUILD_BUDDY_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            buildBuddyNotificationManager.createNotificationChannel(channel)
        }

        val buildBuddyIntent = Intent(this, BuildBuddyActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val buildBuddyPendingIntent = PendingIntent.getActivity(
            this,
            0,
            buildBuddyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val buildBuddyNotification = NotificationCompat.Builder(this, BUILD_BUDDY_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.build_buddy_noti_ic)
            .setAutoCancel(true)
            .setContentIntent(buildBuddyPendingIntent)
            .build()

        buildBuddyNotificationManager.notify(System.currentTimeMillis().toInt(), buildBuddyNotification)
    }

}