package com.example.workmanagerdemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.workmanagerdemo.Constants.CHANNEL_NAME

private lateinit var mNotificationManager: NotificationManager

fun notifyNotification(context: Context, notificationId: Int, block: NotificationCompat.Builder.() -> Unit) {
    val notification = createNotification(context, block)
    val manager = getNotificationManager(context)
    manager.notify(notificationId, notification)
}

fun notifyNotification(context: Context, notificationId: Int, notification: Notification) {
    val manager = getNotificationManager(context)
    manager.notify(notificationId, notification)
}

fun createNotification(context: Context, block: NotificationCompat.Builder.() -> Unit): Notification {
    val builder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
    block.invoke(builder)
    createNotificationChannel(context)
    return builder.build()
}

private  fun createNotificationChannel(context: Context): NotificationChannel {
    val notificationManager = getNotificationManager(context)
    val channel = NotificationChannel(Constants.CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
    notificationManager.createNotificationChannel(channel)
    return channel
}

private fun getNotificationManager(context: Context): NotificationManager {
    if (!::mNotificationManager.isInitialized) {
        mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    return mNotificationManager
}

