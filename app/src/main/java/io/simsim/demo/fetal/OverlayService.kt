package io.simsim.demo.fetal

import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import io.simsim.demo.fetal.App.Companion.NOTIFICATION_FLOAT_ID
import io.simsim.demo.fetal.ui.appLifecycleFlow
import io.simsim.demo.fetal.ui.buildComposeView
import io.simsim.demo.fetal.ui.goto
import io.simsim.demo.fetal.ui.theme.FetalDemoTheme
import kotlinx.coroutines.launch

class OverlayService : BaseService() {

    private val chatHead: View by lazy {
        buildComposeView {
            FloatView()
        }
    }
    private val wm by lazy {
        getSystemService(WINDOW_SERVICE) as WindowManager
    }


    override fun onCreate() {
        super.onCreate()
        startForeground(1, getNotification())
        launch {
            appLifecycleFlow.collect {
                when (it) {
                    Lifecycle.Event.ON_CREATE -> {}
                    Lifecycle.Event.ON_START -> {}
                    Lifecycle.Event.ON_RESUME -> dismissFloatingWindow()
                    Lifecycle.Event.ON_PAUSE -> showFloatingWindow()
                    Lifecycle.Event.ON_STOP -> {}
                    Lifecycle.Event.ON_DESTROY -> dismissFloatingWindow()
                    Lifecycle.Event.ON_ANY -> {}
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissFloatingWindow()
    }

    private fun showFloatingWindow() {
        startForeground(1, getNotification())
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 100

        wm.addView(chatHead, params)
    }

    private fun dismissFloatingWindow() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        if (chatHead.isAttachedToWindow) {
            wm.removeView(chatHead)
        }
    }

    private fun getNotification() = NotificationCompat.Builder(this, NOTIFICATION_FLOAT_ID).apply {
        this.setContentText("setContentText")
        this.setContentTitle("setContentTitle")
    }.build()
}

@Composable
fun FloatView() = LocalContext.current.run {
    FetalDemoTheme {
        Surface {
            IconButton(onClick = { goto<MainActivity>() }) {
                Icon(imageVector = Icons.Rounded.MonitorHeart, contentDescription = "heart rate")
            }
        }
    }
}