@file:Suppress("unused")

package io.simsim.demo.fetal.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.net.toUri
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.lzf.easyfloat.interfaces.OnPermissionResult
import com.lzf.easyfloat.permission.PermissionUtils
import io.simsim.demo.fetal.service.BaseService
import splitties.activities.startActivity

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

fun Context.back2Home() = startActivity(Intent.ACTION_MAIN) {
    addCategory(Intent.CATEGORY_HOME)
    if (this@back2Home !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}

@SuppressLint("UnspecifiedImmutableFlag")
inline fun <reified T : Service> Context.servicePendingIntent(
    action: String,
    configIntent: Intent.() -> Unit = {}
): PendingIntent? {
    val intent = Intent(this, T::class.java)
    intent.action = action
    configIntent.invoke(intent)
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
    return PendingIntent.getService(this, 0, intent, flags)
}

@SuppressLint("UnspecifiedImmutableFlag")
fun Context.activityPendingIntent(
    intent: Intent,
    action: String
): PendingIntent? {
    intent.action = action
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
    return PendingIntent.getActivity(this, 0, intent, flags)
}

@SuppressLint("UnspecifiedImmutableFlag")
inline fun <reified T : Activity> Context.activityPendingIntent(
    action: String,
    configIntent: Intent.() -> Unit = {}
): PendingIntent? {
    val intent = Intent(this, T::class.java)
    intent.action = action
    configIntent.invoke(intent)
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
    return PendingIntent.getActivity(this, 0, intent, flags)
}

@SuppressLint("UnspecifiedImmutableFlag")
inline fun <reified T : BroadcastReceiver> Context.broadcastPendingIntent(
    action: String,
    configIntent: Intent.() -> Unit = {}
): PendingIntent? {
    val intent = Intent(this, T::class.java)
    intent.action = action
    configIntent.invoke(intent)
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
    return PendingIntent.getBroadcast(this, 0, intent, flags)
}

fun Activity.requestOverlayPermission(onPermissionResult: OnPermissionResult) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            ).apply {
                data = "package:$packageName".toUri()
            }
        )
    } else PermissionUtils.requestPermission(this, onPermissionResult)
}
