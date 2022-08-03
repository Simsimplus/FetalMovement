package io.simsim.demo.fetal.ui.main

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import io.simsim.demo.fetal.R
import io.simsim.demo.fetal.helper.*
import io.simsim.demo.fetal.service.BaseBinder
import io.simsim.demo.fetal.service.OverlayService
import io.simsim.demo.fetal.ui.overlay.OverlayActivity
import io.simsim.demo.fetal.ui.theme.FetalDemoTheme
import io.simsim.demo.fetal.ui.widgt.FloatingView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import splitties.toast.UnreliableToastApi
import splitties.toast.toast

class MainActivity : AppCompatActivity(), ServiceConnection {
    private lateinit var service: OverlayService
    private val floatWindow by lazy {
        EasyFloat.with(this)
            .setLayout(
                FloatingView(
                    context = this,
                    remainTimeFlow = service.timerFlow,
                    clickTextFlow = service.validClick.combine(service.totalClick) { a, b ->
                        "$a/$b"
                    },
                    onClick = {
                        service.onClick()
                    },
                    coroutineScope = service
                )
            )
            .setDragEnable(true)
            .setSidePattern(SidePattern.RESULT_HORIZONTAL)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setTag("float")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        bindService(
            Intent(this, OverlayService::class.java),
            this,
            Service.BIND_AUTO_CREATE
        )
        setContent {
        }
    }

    override fun onResume() {
        super.onResume()
        EasyFloat.hide("float")
    }

    override fun onPause() {
        super.onPause()
        EasyFloat.show("float")
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        this.service = (service as BaseBinder).service as OverlayService
        setContent {
            RecordScreen(
                this.service.timerFlow.collectAsState().value,
                this.service.validClick.collectAsState().value,
                this.service.totalClick.collectAsState().value,
                this.service::onClick
            )
        }
        floatWindow.show()
        EasyFloat.hide("float")
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        //
    }

    override fun onBackPressed() {
        super.onBackPressed()
        back2Home()
    }
}

@Composable
private fun RecordScreen(
    countdownText: String,
    validClick: Int,
    totalClick: Int,
    onClick: () -> Unit
) {
    FetalDemoTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Heart(
                    modifier = Modifier.align(Alignment.Center),
                    countdownText = countdownText,
                    onClick = onClick
                )
                Text(modifier = Modifier.align(Alignment.BottomStart), text = "valid:$validClick")
                Text(modifier = Modifier.align(Alignment.BottomEnd), text = "total:$totalClick")
            }
        }
    }
}

@Composable
fun Heart(
    modifier: Modifier = Modifier,
    heartSizeDp: Dp = 100.dp,
    countdownText: String?,
    onClick: () -> Unit = {}
) {
    var validClick by remember {
        mutableStateOf(false)
    }
    val scale by animateFloatAsState(targetValue = if (validClick) 1.2f else 1f) {
        validClick = false
    }
    ColorContent(MaterialTheme.colorScheme.primary) {
        CenterAlignColumn(modifier = modifier.scale(scale)) {
            Icon(
                modifier = Modifier
                    .silentClickable {
                        validClick = true
                        onClick()
                    }
                    .size(heartSizeDp),
                painter = painterResource(id = R.drawable.ic_heart_rate),
                contentDescription = ""
            )
            countdownText?.let {
                Text(text = it)
            }
        }
    }
}

@OptIn(UnreliableToastApi::class)
@Composable
@Suppress("unused")
private fun AuthScreen() {
    val ctx = ctx
    val cs = cs
    var authJob: Job? = null

    BackHandler {
    }
    FetalDemoTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = MutableInteractionSource(),
                    onClick = {
                        authJob?.cancel()
                        authJob?.invokeOnCompletion {
                            ctx.toast("cancel authenticate")
                        }
                        authJob = null
                    }
                ),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(onClick = {
                    authJob = cs.launch {
                        ctx.toast(tryAuth(ctx as FragmentActivity))
                    }
                }) {
                    Text(text = "authenticate")
                }
                TextButton(onClick = {
                    if (Settings.canDrawOverlays(ctx)) {
                        ctx.startService<OverlayService>()
                    } else ctx.goto<OverlayActivity>()
                }) {
                    Text(text = "go overlay")
                }
            }
        }
    }
}
