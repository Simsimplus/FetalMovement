@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package io.simsim.demo.fetal.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.CallSuper
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import io.simsim.demo.fetal.helper.LifecycleHelp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class BaseService :
    CoroutineScope by MainScope(),
    Service(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {
    private val _lifecycleRegistry: LifecycleRegistry by lazy { LifecycleRegistry(this) }
    private val handleLifecycleEvent = _lifecycleRegistry::handleLifecycleEvent
    private val _savedStateRegistry by lazy { SavedStateRegistryController.create(this) }
    private val _viewModelStore by lazy { ViewModelStore() }
    override fun getLifecycle(): Lifecycle = _lifecycleRegistry
    override fun getViewModelStore(): ViewModelStore = _viewModelStore
    override val savedStateRegistry: SavedStateRegistry = _savedStateRegistry.savedStateRegistry

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
    override fun onDestroy() {
        super.onDestroy()
        cancel()
        LifecycleHelp.onServiceDestroy(this)
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    @CallSuper
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
