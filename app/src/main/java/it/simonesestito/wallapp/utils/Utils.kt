/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.utils

import android.app.Activity
import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import android.view.View
import android.view.animation.Animation
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import it.simonesestito.wallapp.CHROME_PACKAGE_NAME
import it.simonesestito.wallapp.FIRESTORE_LOCALIZED_DEFAULT
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.lifecycle.LifecycleExecutor
import java.io.File
import java.io.IOException
import java.util.*

typealias LocalizedString = Map<String, Any>

val LocalizedString.localized: String
    get() {
        val currentLang = Locale.getDefault().language
        val localized = this[currentLang] ?: this[FIRESTORE_LOCALIZED_DEFAULT] ?: ""
        return localized.toString()
    }

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
inline fun <L : List<I>, I, T> LiveData<L>.mapList(crossinline converter: (I) -> T): LiveData<List<T>> {
    return Transformations.map(this) { list ->
        list.map(converter)
    }
}

/**
 * Observe a LiveData only once, then remove the observer
 */
inline fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, crossinline onValue: (T?) -> Unit) =
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(newValue: T?) {
                removeObserver(this /* Observer<T> */)
                onValue(newValue)
            }
        })

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
 * @receiver Context needed to getInstance app cache directory
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
    if (state.ordinal >= Lifecycle.State.RESUMED.ordinal) {
        Log.d("executeOnReady", "LifecycleOwner is ready, performing action")
        action()
    } else {
        lifecycle.addObserver(LifecycleExecutor(action))
        Log.d("executeOnReady", "LifecycleOwner is not ready yet, action scheduled")
    }
}

/**
 * Utility function to getInstance a [ViewModel] using a [ViewModelProvider.Factory] as required by Dagger
 * Using this function you are obliged to pass a factory
 */
inline fun <reified T : ViewModel> Fragment.getViewModel(factory: ViewModelProvider.Factory) =
        ViewModelProviders.of(this, factory)[T::class.java]

/**
 * Check if the user is connected to the Internet
 */
fun Context.isConnectivityOnline() =
        ContextCompat.getSystemService(this, ConnectivityManager::class.java)
                ?.activeNetworkInfo?.isConnected ?: false

/**
 * Check if the user has a live wallpaper set
 */
fun WallpaperManager.isSetLiveWallpaper() = this.wallpaperInfo != null

/**
 * Open a given URL
 * @param forceChrome True to set the Intent package as Chrome package name
 */
fun Context.openUrl(url: String, forceChrome: Boolean = false, useCustomTab: Boolean = true) {
    if (useCustomTab) {
        val customTabIntent = CustomTabsIntent.Builder()
                .enableUrlBarHiding()
                .setShowTitle(true)
                .setToolbarColor(ResourcesCompat.getColor(resources, R.color.color_accent, null))
                .build()

        if (forceChrome) {
            customTabIntent.intent.setPackage(CHROME_PACKAGE_NAME)
        }

        customTabIntent.launchUrl(this, url.toUri())
    } else {
        startActivity(Intent().apply {
            action = Intent.ACTION_VIEW
            data = url.toUri()

            if (forceChrome) {
                `package` = CHROME_PACKAGE_NAME
            }

            if (this !is Activity) {
                // Add this flag to launch an Activity from non-activity context
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        })
    }
}
