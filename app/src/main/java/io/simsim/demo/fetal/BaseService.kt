@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package io.simsim.demo.fetal

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.CallSuper
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class BaseService :
    CoroutineScope by MainScope(),
    Service(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {
    private val lifecycleRegistry: LifecycleRegistry by lazy { LifecycleRegistry(this) }
    private val _savedStateRegistry by lazy { SavedStateRegistryController.create(this) }
    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    //ViewModelStore Methods
    private val store by lazy { ViewModelStore() }

    override fun getViewModelStore(): ViewModelStore = store

    //SaveStateRegistry Methods
    override val savedStateRegistry: SavedStateRegistry
        get() = _savedStateRegistry.savedStateRegistry

    private fun handleLifecycleEvent(event: Lifecycle.Event) =
        lifecycleRegistry.handleLifecycleEvent(event)

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        LifecycleHelp.onServiceCreate(this)
        // You must call performAttach() before calling performRestore(Bundle)
        savedStateRegistry.performAttach(lifecycle)
        savedStateRegistry.performRestore(null)
        handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    @CallSuper
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        cancel()
        LifecycleHelp.onServiceDestroy(this)
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}