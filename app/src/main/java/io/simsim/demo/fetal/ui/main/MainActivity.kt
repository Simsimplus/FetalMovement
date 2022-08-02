package io.simsim.demo.fetal.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import io.simsim.demo.fetal.R
import io.simsim.demo.fetal.helper.*
import io.simsim.demo.fetal.service.OverlayService
import io.simsim.demo.fetal.ui.overlay.OverlayActivity
import io.simsim.demo.fetal.ui.theme.FetalDemoTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import splitties.toast.UnreliableToastApi
import splitties.toast.toast
import java.time.Duration

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            RecordScreen()
        }
    }
}

@OptIn(UnreliableToastApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun RecordScreen() {
    BackHandler {
    }
    val validChecker = remember {
        ValidChecker(Duration.ofMinutes(5))
    }
    var validClick by rememberSaveable {
        mutableStateOf(0)
    }
    var totalClick by rememberSaveable {
        mutableStateOf(0)
    }
    FetalDemoTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Heart(modifier = Modifier.align(Alignment.Center)) {
                    validChecker.apply {
                        validClick++
                    }
                    totalClick++
                }
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
    onClick: () -> Unit = {}
) {
    var validClick by remember {
        mutableStateOf(false)
    }
    val scale by animateFloatAsState(targetValue = if (validClick) 1.2f else 1f) {
        validClick = false
    }
    ColorContent(MaterialTheme.colorScheme.primary) {
        Box(modifier = modifier) {
            Icon(
                modifier = Modifier
                    .scale(scale)
                    .align(Alignment.Center)
                    .size(heartSizeDp),
                painter = painterResource(id = R.drawable.ic_heart_rate),
                contentDescription = ""
            )
            Icon(
                modifier = Modifier
                    .silentClickable {
                        validClick = true
                        onClick()
                    }
                    .align(Alignment.Center)
                    .size(heartSizeDp),
                painter = painterResource(id = R.drawable.ic_heart_rate),
                contentDescription = ""
            )
        }
    }
}

@Composable
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
