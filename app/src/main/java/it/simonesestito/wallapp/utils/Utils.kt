package it.simonesestito.wallapp.utils

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.view.View
import android.view.animation.Animation
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import it.simonesestito.wallapp.lifecycle.LifecycleExecutor
import java.io.File
import java.io.IOException
import java.util.*

val Any.TAG: String
    get() = this.javaClass.simpleName

/**
 * Detect if the given color is light
 * @receiver color to test
 * @return true if the color is light
 */
fun @receiver:ColorInt Int.isLightColor() =
        ColorUtils.calculateLuminance(this) >= 0.5

/**
 * Map [LiveData] value
 */
fun <X, Y> LiveData<X>.map(converter: (X) -> Y): LiveData<Y> {
    return Transformations.map(this, converter)
}

/**
 * Map all items in a list contained in a [LiveData]
 */
fun <L : List<I>, I, T> LiveData<L>.mapList(converter: (I) -> T): LiveData<List<T>> {
    return Transformations.map(this) { list ->
        list.map(converter)
    }
}

/**
 * Add a listener to a [RecyclerView]
 */
inline fun RecyclerView.onScrollListener(crossinline listener: (RecyclerView) -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            listener(recyclerView)
        }
    })
}

fun Fragment.findNavController() = NavHostFragment.findNavController(this)

/**
 * Set light status bar altering [View.setSystemUiVisibility] flags
 */
@RequiresApi(Build.VERSION_CODES.M)
fun Activity.setLightStatusBar(light: Boolean) {
    val decorView = window?.decorView
    decorView ?: return

    if (light) {
        // Set light system bars (black icons)
        decorView.systemUiVisibility = decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
        // Set dark system bars (light icons)
        decorView.systemUiVisibility = decorView.systemUiVisibility and
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
}

/**
 * Set light navigation bar altering [View.setSystemUiVisibility] flags
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Activity.setLightNavBar(light: Boolean) {
    val decorView = window?.decorView
    decorView ?: return

    if (light) {
        // Set light system bars (black icons)
        decorView.systemUiVisibility = decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    } else {
        // Set dark system bars (light icons)
        decorView.systemUiVisibility = decorView.systemUiVisibility and
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
    }
}

/**
 * Add a listener to a [Transition]
 */
inline fun Transition.addListener(crossinline onStart: () -> Unit = {},
                                  crossinline onEnd: () -> Unit = {}) =
        addListener(object : TransitionListenerAdapter() {
            override fun onTransitionStart(transition: Transition) = onStart()
            override fun onTransitionEnd(transition: Transition) = onEnd()
        })

/**
 * Add a listener to an [Animation]
 */
inline fun Animation.addListener(crossinline onStart: () -> Unit = {},
                                 crossinline onEnd: () -> Unit = {},
                                 crossinline onRepeat: () -> Unit = {}): Animation {
    this.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationEnd(p0: Animation?) = onEnd()
        override fun onAnimationStart(p0: Animation?) = onStart()
        override fun onAnimationRepeat(p0: Animation?) = onRepeat()
    })
    return this
}

/**
 * Try dismissing a [DialogFragment]
 * Handle wrong state and bad visibility state
 * @receiver Dialog to dismiss
 * @return true in case it has been successfully dismissed
 */
fun DialogFragment.tryDismiss(): Boolean {
    if (!isVisible) {
        return false
    }

    return try {
        dismiss()
        true
    } catch (_: IllegalStateException) {
        false
    }
}

/**
 * Create a new file with random name in app cache
 * @receiver Context needed to get app cache directory
 * @param prefix File prefix
 * @param suffix File extension, with the dot
 * @throws IOException Error creating the file
 * @return Created [File]
 */
@Throws(IOException::class)
fun Context.createCacheFile(prefix: String, suffix: String = ".tmp"): File {
    val uuid = UUID.randomUUID().toString()
    val cacheDir = File(cacheDir, "$prefix-$uuid-$suffix")
    cacheDir.createNewFile()
    return cacheDir
}

/**
 * Register a [BroadcastReceiver] to the [LocalBroadcastManager]
 * @receiver Local broadcast manager where to register the receiver
 * @param intentFilter
 * @param receiver Lambda representation of the [BroadcastReceiver.onReceive]
 */
fun LocalBroadcastManager.registerReceiver(intentFilter: IntentFilter, receiver: (Intent, Context) -> Boolean) =
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null && context != null) {
                    val hasToUnregister = receiver(intent, context)
                    if (hasToUnregister) {
                        unregisterReceiver(this)
                    }
                }
            }
        }, intentFilter)

/**
 * Check if a given list contains only the given element
 * @receiver Given collection of the same type of the element
 * @param element Given element
 * @return true if it contains only the given element and list is not empty
 */
fun <T> Collection<T>.containsOnly(element: T): Boolean {
    if (isEmpty()) {
        return false
    }
    forEach {
        if (it != element) {
            return false
        }
    }
    return true
}

/**
 * Execute an action when a LifecycleOwner is in a ready state (at least STARTED)
 * Else wait until it's in [Lifecycle.State.RESUMED]
 * @receiver Target LifecycleOwner
 * @param action Action to execute
 */
fun LifecycleOwner.executeOnReady(action: () -> Unit) {
    val state = lifecycle.currentState
    if (state.isAtLeast(Lifecycle.State.RESUMED)) {
        action()
    } else {
        lifecycle.addObserver(LifecycleExecutor(action))
    }
}

/**
 * Utility function to get a [ViewModel] using a [ViewModelProvider.Factory] as required by Dagger
 * Using this function you are obliged to pass a factory
 */
inline fun <reified T : ViewModel> Fragment.getViewModel(factory: ViewModelProvider.Factory) =
        ViewModelProviders.of(this, factory)[T::class.java]