package io.simsim.demo.fetal.ui.main

import android.app.Activity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Layers
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
import androidx.lifecycle.viewModelScope
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.interfaces.OnPermissionResult
import com.lzf.easyfloat.permission.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint
import io.simsim.demo.fetal.R
import io.simsim.demo.fetal.helper.*
import io.simsim.demo.fetal.ui.history.HistoryActivity
import io.simsim.demo.fetal.ui.theme.FetalDemoTheme
import io.simsim.demo.fetal.ui.widgt.FloatingView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import splitties.toast.UnreliableToastApi
import splitties.toast.toast

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val vm: MainVM by viewModels()
    private val floatWindow by lazy {
        EasyFloat.with(this)
            .setLayout(
                FloatingView(
                    context = this,
                    remainTimeFlow = vm.timerFlow,
                    clickTextFlow = vm.validClick.combine(vm.totalClick) { a, b ->
                        "$a/$b"
                    },
                    onClick = {
                        vm.onClick()
                    },
                    coroutineScope = vm.viewModelScope
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
        setContent {
            RecordScreen(
                vm.timerFlow.collectAsState().value,
                vm.validTimeFlow.collectAsState().value,
                vm.validClick.collectAsState(0).value,
                vm.totalClick.collectAsState(0).value,
                vm::onClick
            )
        }
        EasyFloat.hide("float")
    }

    override fun onResume() {
        super.onResume()
        EasyFloat.hide("float")
    }

    override fun onPause() {
        super.onPause()
        floatWindow.show()
        EasyFloat.show("float")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        back2Home()
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
    var isAllowOverlay by remember {
        mutableStateOf(
            Settings.canDrawOverlays(ctx)
        )
    }
    FetalDemoTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                IconToggleButton(
                    checked = isAllowOverlay,
                    onCheckedChange = {
                        if (!Settings.canDrawOverlays(ctx)) PermissionUtils.requestPermission(
                            ctx as Activity,
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
                    countdownText = countdownText,
                    onClick = onClick
                )
                Text(
                    modifier = Modifier.align(Alignment.BottomStart),
                    text = "[$validCountDownText]valid:$validClick"
                )
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
            }
        }
    }
}
