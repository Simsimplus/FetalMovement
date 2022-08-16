package io.simsim.demo.fetal.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import dagger.hilt.android.AndroidEntryPoint
import io.simsim.demo.fetal.App.Companion.NOTIFICATION_FLOAT_ID
import io.simsim.demo.fetal.R
import io.simsim.demo.fetal.data.FetalDB
import io.simsim.demo.fetal.data.entity.FetalMovementRecord
import io.simsim.demo.fetal.helper.*
import io.simsim.demo.fetal.ui.main.MainActivity
import io.simsim.demo.fetal.ui.widgt.FloatingView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class OverlayService : BaseService() {

    @Inject
    lateinit var db: FetalDB
    private val validChecker = ValidChecker(Duration.ofMinutes(5))
    private val _validTimeFlowStarter = MutableStateFlow(false)
    private val _fetalMovementRecordFlow = MutableStateFlow(FetalMovementRecord())

    val timerFlow = Timer.formatTimeString(Duration.ofHours(1), Duration.ofSeconds(1))
        .onCompletion {
            // do final when countdown ends
        }
        .stateIn(this, SharingStarted.Lazily, initialValue = "--")

    val validTimeFlow = _validTimeFlowStarter.flatMapLatest {
        Timer.formatTimeString(
            validChecker.duration,
            Duration.ofSeconds(1),
            it
        ).onCompletion {
            _validTimeFlowStarter.value = false
        }
    }.stateIn(this, SharingStarted.Lazily, initialValue = "--")

    val totalClick = _fetalMovementRecordFlow.flatMapLatest {
        db.recordDao().queryRecord(it.recordId)
    }.map {
        it?.totalMovement ?: 0
    }

    val validClick = _fetalMovementRecordFlow.flatMapLatest {
        db.recordDao().queryRecord(it.recordId)
    }.map {
        it?.validMovement ?: 0
    }

    fun onClick() = launch {
        db.recordDao().addMovementToRecord(
            _fetalMovementRecordFlow.value,
            validChecker.apply {
                _validTimeFlowStarter.value = true
            }
        )
    }

    private val floatWindow by lazy {
        EasyFloat.with(this)
            .setLayout(
                FloatingView(
                    context = this,
                    remainTimeFlow = timerFlow,
                    clickTextFlow = validClick.combine(totalClick) { a, b ->
                        "$a/$b"
                    },
                    onLongClick = {
                        goto<MainActivity>()
                    },
                    onClick = {
                        onClick()
                    },
                    coroutineScope = this
                )
            )
            .setDragEnable(true)
            .setSidePattern(SidePattern.RESULT_HORIZONTAL)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setTag("float")
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(-1122394, getNotification())
        launch {
            appLifecycleFlow.collectLatest {
                when (it) {
                    Lifecycle.Event.ON_CREATE -> {
                    }
                    Lifecycle.Event.ON_START -> {}
                    Lifecycle.Event.ON_RESUME -> {
                        EasyFloat.hide("float")
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        showFloatingWindow()
                    }
                    Lifecycle.Event.ON_STOP -> {}
                    Lifecycle.Event.ON_DESTROY -> {}
                    Lifecycle.Event.ON_ANY -> {}
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return object : BaseBinder() {
            override val service: Service
                get() = this@OverlayService
        }
    }

    private fun showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            floatWindow.show()
            EasyFloat.show("float")
        }
    }

    private fun getNotification() = NotificationCompat.Builder(this, NOTIFICATION_FLOAT_ID)
        .setSmallIcon(R.drawable.ic_heart_rate)
        .setOngoing(true)
        .setContentTitle("fetal")
        .setContentText("notificationContent")
        .setContentIntent(
            activityPendingIntent<MainActivity>("back")
        ).setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .build()
}
