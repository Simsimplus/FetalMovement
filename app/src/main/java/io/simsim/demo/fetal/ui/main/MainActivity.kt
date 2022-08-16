package io.simsim.demo.fetal.ui.main

import android.app.Activity
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.whenResumed
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.*
import com.lzf.easyfloat.interfaces.OnPermissionResult
import dagger.hilt.android.AndroidEntryPoint
import io.simsim.demo.fetal.R
import io.simsim.demo.fetal.helper.*
import io.simsim.demo.fetal.service.BaseBinder
import io.simsim.demo.fetal.service.OverlayService
import io.simsim.demo.fetal.ui.history.HistoryActivity
import io.simsim.demo.fetal.ui.theme.FetalDemoTheme
import io.simsim.demo.fetal.ui.widgt.RocketWord
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import splitties.toast.UnreliableToastApi
import splitties.toast.toast
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ServiceConnection {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        bindService(
            Intent(
                this,
                OverlayService::class.java
            ),
            this,
            Service.BIND_AUTO_CREATE
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        back2Home()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        //
        val overlayService = (service as BaseBinder).service as OverlayService
        setContent {
            RecordScreen(
                overlayService.timerFlow.collectAsState().value,
                overlayService.validTimeFlow.collectAsState().value,
                overlayService.validClick.collectAsState(0).value,
                overlayService.totalClick.collectAsState(0).value,
                overlayService::onClick
            )
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        //
    }
}

@Composable
private fun RecordScreen(
    countdownText: String,
    validCountDownText: String,
    validClick: Int,
    totalClick: Int,
    onClick: () -> Unit
) {
    val ctx = ctx
    val density = LocalDensity.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_heart2))
    var isAllowOverlay by remember {
        mutableStateOf(
            Settings.canDrawOverlays(ctx)
        )
    }
    var wordPosition by remember {
        mutableStateOf(DpOffset.Zero)
    }
    var word by remember {
        mutableStateOf("test")
    }
    var clickEnabled by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(clickEnabled) {
        if (!clickEnabled) {
            delay(lottieComposition?.duration?.toLong() ?: 2500)
            clickEnabled = true
        }
    }
    rememberCoroutineScope().launch {
        lifecycleOwner.whenResumed {
            isAllowOverlay = Settings.canDrawOverlays(ctx)
        }
    }
    FetalDemoTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(true) {
                    this.detectTapGestures(onPress = {
                        wordPosition = with(density) { DpOffset(it.x.toDp(), it.y.toDp()) }
                        word = UUID
                            .randomUUID()
                            .toString()
                            .slice(0..3)
                    })
                },
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                IconToggleButton(
                    checked = isAllowOverlay,
                    onCheckedChange = {
                        if (!Settings.canDrawOverlays(ctx)) (ctx as Activity).requestOverlayPermission(
                            object : OnPermissionResult {
                                override fun permissionResult(isOpen: Boolean) {
                                    isAllowOverlay = isOpen
                                }
                            }
                        )
                    }
                ) {
                    Icon(imageVector = Icons.Rounded.Layers, contentDescription = "canOverlay")
                }
                IconButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onClick = { ctx.goto<HistoryActivity>() }
                ) {
                    Icon(imageVector = Icons.Rounded.History, contentDescription = "history")
                }
                Heart(
                    modifier = Modifier.align(Alignment.Center),
                    clickEnabled = clickEnabled,
                    countdownText = countdownText,
                    onClick = {
                        onClick()
                        clickEnabled = false
                    },
                    lottieComposition = lottieComposition
                )
                Text(
                    modifier = Modifier.align(Alignment.BottomStart),
                    text = "[$validCountDownText]valid:$validClick"
                )
                Text(modifier = Modifier.align(Alignment.BottomEnd), text = "total:$totalClick")
                RocketWord(word = word, position = wordPosition)
            }
        }
    }
}

@Composable
fun Heart(
    modifier: Modifier = Modifier,
    heartSizeDp: Dp = LocalConfiguration.current.screenWidthDp.dp / 2,
    countdownText: String?,
    onClick: () -> Unit = {},
    clickEnabled: Boolean = true,
    lottieComposition: LottieComposition?
) {
    CenterAlignColumn(modifier = modifier) {
        val lottieAnimationState = rememberLottieAnimatable()
        val progress by lottieAnimationState
        val endProgress = 0.8f
        LaunchedEffect(true) {
            lottieAnimationState.snapTo(
                lottieComposition,
                1f
            )
//            if (progress == endProgress) onLottieEnd()
        }
        LaunchedEffect(clickEnabled) {
            if (!clickEnabled) {
                lottieAnimationState.animate(
                    composition = lottieComposition,
                    clipSpec = LottieClipSpec.Progress(0f, endProgress),
                    cancellationBehavior = LottieCancellationBehavior.OnIterationFinish
                )
            }
        }
        LottieAnimation(
            modifier = Modifier
                .silentClickable(
                    enabled = clickEnabled
                ) {
                    onClick()
                }
                .size(heartSizeDp),
            composition = lottieComposition,
            progress = { progress }
        )
        Text(text = progress.toString())
        countdownText?.let {
            Text(text = it)
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
            }
        }
    }
}
