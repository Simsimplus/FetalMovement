package io.simsim.demo.fetal.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import io.simsim.demo.fetal.helper.*
import io.simsim.demo.fetal.service.OverlayService
import io.simsim.demo.fetal.ui.overlay.OverlayActivity
import io.simsim.demo.fetal.ui.theme.FetalDemoTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import splitties.toast.UnreliableToastApi
import splitties.toast.toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            Screen()
        }
    }
}

@OptIn(UnreliableToastApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun Screen() {
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
