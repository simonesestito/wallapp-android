/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.utils

import android.app.Activity
import android.app.WallpaperManager
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.simonesestito.wallapp.CHROME_PACKAGE_NAME
import com.simonesestito.wallapp.FIRESTORE_LOCALIZED_DEFAULT
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.SHARED_PREFERENCES_FILENAME
import com.simonesestito.wallapp.lifecycle.LifecycleExecutor
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
fun <T, R> LiveData<T>.map(converter: (T) -> R): LiveData<R> {
    return Transformations.map(this, converter)
}

/**
 * Filter items from a list wrapped inside a [LiveData]
 */
inline fun <T> LiveData<List<T>>.filter(crossinline predicate: (T) -> Boolean): LiveData<List<T>> {
    return Transformations.map(this) { list ->
        list.filter(predicate)
    }
}

/**
 * Map all items in a list wrapped in a [LiveData]
 */
inline fun <T, R> LiveData<List<T>>.mapList(crossinline converter: (T) -> R): LiveData<List<R>> {
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

/**
 * Lazy load SharedPreferences
 * Don't load preferences twice
 * Warning: always use .apply()
 */
val Context.sharedPreferences: SharedPreferences by thisLazy {
    getSharedPreferences(SHARED_PREFERENCES_FILENAME, Context.MODE_PRIVATE)
}

val Fragment.sharedPreferences
    get() = requireContext().sharedPreferences

fun Resources.Theme.resolveIntAttribute(id: Int): Int {
    val typedValue = TypedValue()
    resolveAttribute(id, typedValue, true)
    return typedValue.data
}

/**
 * Call [View.setOnApplyWindowInsetsListener], ensuring the listener is called only once
 */
inline fun View.setOnApplyWindowInsetsListenerOnce(crossinline listener: (View, WindowInsetsCompat) -> Unit) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        // Remove the listener
        ViewCompat.setOnApplyWindowInsetsListener(view, null)

        // Call the given listener
        listener(view, insets)

        // Return the same insets
        return@setOnApplyWindowInsetsListener insets
    }
}

/**
 * Detect if the current device is running MIUI
 */
fun Context.isPlatformMIUI() = arrayOf(
        Intent("miui.intent.action.OP_AUTO_START")
                .addCategory(Intent.CATEGORY_DEFAULT),
        Intent()
                .setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
        Intent("miui.intent.action.POWER_HIDE_MODE_APP_LIST")
                .addCategory(Intent.CATEGORY_DEFAULT),
        Intent()
                .setComponent(ComponentName("com.miui.securitycenter", "com.miui.powercenter.PowerSettings"))
).any { packageManager.resolveActivity(it, PackageManager.MATCH_DEFAULT_ONLY) != null }

/**
 * Generate a palette in a suspend function
 */
suspend fun Palette.Builder.suspendGenerate(): Palette = suspendCoroutine {
    this.generate { palette -> it.resume(palette!!) }
}

fun Context.isDarkTheme() = resources
        .configuration
        .uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES ||
        AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

fun TabLayout.setupWithViewPager(viewPager: ViewPager2, tabTitles: Array<String>) {
    TabLayoutMediator(this, viewPager) { tab, position ->
        tab.text = tabTitles[position]
    }.attach()
}

fun View.addTopWindowInsetPadding() {
    setOnApplyWindowInsetsListenerOnce { root, insets ->
        root.updatePadding(
                top = insets.systemWindowInsetTop + root.paddingTop,
                bottom = insets.systemWindowInsetBottom
        )
    }
}