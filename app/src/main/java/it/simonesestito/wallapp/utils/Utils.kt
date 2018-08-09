package it.simonesestito.wallapp.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.Animation
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import it.simonesestito.wallapp.GlideRequest
import it.simonesestito.wallapp.annotations.WallpaperFormat
import it.simonesestito.wallapp.annotations.dimensions
import it.simonesestito.wallapp.annotations.downloadableFormats

val Any.TAG: String
    get() = this.javaClass.simpleName

fun <X, Y> LiveData<X>.map(converter: (X) -> Y): LiveData<Y> {
    return Transformations.map(this, converter)
}

fun <L : List<I>, I, T> LiveData<L>.mapList(converter: (I) -> T): LiveData<List<T>> {
    return Transformations.map(this) { list ->
        list.map(converter)
    }
}

inline fun RecyclerView.onScrollListener(crossinline listener: (RecyclerView) -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            listener(recyclerView)
        }
    })
}

@WallpaperFormat
fun getSuggestedWallpaperFormat(displayMetrics: DisplayMetrics): String {
    // Calculate user aspect ratio
    val userRatio = displayMetrics.widthPixels / displayMetrics.heightPixels.toDouble()

    // Find if user display is exactly one of the formats
    downloadableFormats.forEach { format ->
        if (format.dimensions.ratio == userRatio) {
            return format
        }
    }

    // If not exactly one of those formats, pick the best one
    return downloadableFormats
            .sortedBy { format -> Math.abs(format.dimensions.ratio - userRatio) }
            .first()
}

fun Fragment.findNavController() = NavHostFragment.findNavController(this)

fun Context.getOptimalCacheSize(divider: Int = 8) =
        ContextCompat.getSystemService(this, ActivityManager::class.java)!!
                .memoryClass * 1024 * 1024 / divider

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

inline fun Transition.addListener(crossinline onStart: () -> Unit = {},
                                  crossinline onEnd: () -> Unit = {}) =
        addListener(object : TransitionListenerAdapter() {
            override fun onTransitionStart(transition: Transition) = onStart()
            override fun onTransitionEnd(transition: Transition) = onEnd()
        })

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

inline fun <T> GlideRequest<T>.addListener(crossinline onFailed: () -> Unit = {},
                                           crossinline onSuccess: (T) -> Unit = {}) =
        addListener(object : RequestListener<T> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<T>?, isFirstResource: Boolean): Boolean {
                onFailed()
                return false
            }

            override fun onResourceReady(resource: T?, model: Any?, target: Target<T>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                onSuccess(resource!!)
                return false
            }
        })

fun BottomSheetBehavior<out View>.show() {
    this.state = BottomSheetBehavior.STATE_EXPANDED
}

fun BottomSheetBehavior<out View>.hide() {
    this.state = BottomSheetBehavior.STATE_HIDDEN
}

inline fun BottomSheetBehavior<out View>.setListener(crossinline onVisible: () -> Unit = {},
                                                     crossinline onHidden: () -> Unit = {}) =
        this.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(v: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_HIDDEN) {
                    onHidden()
                } else {
                    onVisible()
                }
            }

            override fun onSlide(v: View, offset: Float) {}
        })
