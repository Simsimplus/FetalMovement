package io.simsim.demo.fetal.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

val appLifecycleFlow = callbackFlow {
    val defaultLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            trySend(
                Lifecycle.Event.ON_CREATE
            )
        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            trySend(
                Lifecycle.Event.ON_START
            )
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            trySend(
                Lifecycle.Event.ON_RESUME
            )
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            trySend(
                Lifecycle.Event.ON_PAUSE
            )
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            trySend(
                Lifecycle.Event.ON_STOP
            )
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            trySend(
                Lifecycle.Event.ON_DESTROY
            )
        }
    }
    ProcessLifecycleOwner.get().lifecycle.addObserver(defaultLifecycleObserver)
    this.awaitClose {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(defaultLifecycleObserver)
    }
}