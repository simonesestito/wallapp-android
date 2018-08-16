package it.simonesestito.wallapp.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent


class LifecycleExecutor(private inline val action: () -> Unit) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun executeAction(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
        action()
    }
}