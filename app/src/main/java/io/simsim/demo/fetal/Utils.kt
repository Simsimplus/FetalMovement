package io.simsim.demo.fetal.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import io.simsim.demo.fetal.BaseService


@SuppressLint("ObsoleteSdkInt")
inline fun <reified Service> Context.startService() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(Intent(this, Service::class.java))
    } else {
        startService(Intent(this, Service::class.java))
    }
}


fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
inline fun <reified Activity> Context.goto() = startActivity(
    Intent(this, Activity::class.java).addFlags(
        Intent.FLAG_ACTIVITY_NEW_TASK
    )
)

fun BaseService.buildComposeView(
    content: @Composable () -> Unit
) = ComposeView(this).also {
    it.setContent(content)
    ViewTreeLifecycleOwner.set(it, this)
    ViewTreeViewModelStoreOwner.set(it, this)
    it.setViewTreeSavedStateRegistryOwner(this)
}

