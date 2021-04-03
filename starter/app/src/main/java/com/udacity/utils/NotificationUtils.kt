package com.udacity.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.MainActivity
import com.udacity.MainActivity.Companion.NOTIFICATION_ID
import com.udacity.R


fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    statusPendingIntent: PendingIntent
) {
    val bigImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.ic_baseline_cloud_download_24
    )

    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigLargeIcon(null)
        .bigPicture(bigImage)

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setAutoCancel(true)
        .setLargeIcon(bigImage)
        .setStyle(bigPicStyle)
        .setContentIntent(statusPendingIntent)
        .addAction(
            R.drawable.ic_baseline_cloud_download_24,
            applicationContext.getString(R.string.notification_action),
            statusPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}