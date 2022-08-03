package io.simsim.demo.fetal.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.simsim.demo.fetal.App.Companion.NOTIFICATION_FLOAT_ID
import io.simsim.demo.fetal.helper.Timer
import io.simsim.demo.fetal.helper.ValidChecker
import io.simsim.demo.fetal.helper.activityPendingIntent
import io.simsim.demo.fetal.ui.main.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.Duration

class OverlayService : BaseService() {

    private val validChecker = ValidChecker(Duration.ofMinutes(5))

    val timerFlow = Timer.timer(Duration.ofHours(1), Duration.ofSeconds(1))
        .stateIn(this, SharingStarted.Lazily, initialValue = "--")
    val totalClick = MutableStateFlow(0)
    val validClick = MutableStateFlow(0)

    fun onClick() {
        validChecker.apply {
            validClick.update {
                it + 1
            }
        }
        totalClick.update {
            it + 1
        }
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(-1122394, getNotification())
    }

    override fun onBind(intent: Intent?): IBinder {
        return object : BaseBinder() {
            override val service: Service
                get() = this@OverlayService
        }
    }

    private fun getNotification() = NotificationCompat.Builder(this, NOTIFICATION_FLOAT_ID)
        .setOngoing(true)
        .setContentTitle("fetal")
        .setContentText("notificationContent")
        .setContentIntent(
            activityPendingIntent<MainActivity>("back")
        ).setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .build()
}
