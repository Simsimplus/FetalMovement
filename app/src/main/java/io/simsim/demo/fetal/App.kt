package io.simsim.demo.fetal

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import splitties.systemservices.notificationManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        registerActivityLifecycleCallbacks(LifecycleHelp)
    }

    private fun createNotificationChannel() {
        val channels = listOf(
            NotificationChannel(
                NOTIFICATION_FLOAT_ID,
                "悬浮窗通知",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
        notificationManager.createNotificationChannels(channels)
    }

    companion object {
        const val NOTIFICATION_FLOAT_ID = "111"
    }
}