package it.simonesestito.wallapp.ui.fragment

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.ui.activity.MainActivity
import it.simonesestito.wallapp.utils.isLightColor
import it.simonesestito.wallapp.utils.setLightNavBar
import it.simonesestito.wallapp.utils.setLightStatusBar

private const val KEY_FRAGMENT_HIDDEN_APPBAR = "have_hidden_appbar"

abstract class AbstractAppFragment : Fragment() {
    private var haveHiddenAppbar = false
    abstract val title: CharSequence

    var statusBarColor: Int
        get() = activity?.window?.statusBarColor ?: Color.WHITE
        set(color) {
            activity?.window?.statusBarColor = color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity?.setLightStatusBar(color.isLightColor())
            }
        }

    var navigationBarColor: Int
        get() = activity?.window?.navigationBarColor ?: Color.WHITE
        set(color) {
            activity?.window?.navigationBarColor = color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity?.setLightNavBar(color.isLightColor())
            }
        }

    override fun onResume() {
        super.onResume()
        activity?.title = title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideAppbarElevation()
        statusBarColor = ResourcesCompat.getColor(resources, R.color.status_bar_color, null)
        navigationBarColor = ResourcesCompat.getColor(resources, R.color.navigation_bar_color, null)
        showAppbar()
    }

    fun hideAppbarElevation() {
        val mainActivity = activity
        if (mainActivity is MainActivity) {
            mainActivity.hideAppbarElevation()
        }
    }

    fun showAppbarElevation() {
        val mainActivity = activity
        if (mainActivity is MainActivity) {
            mainActivity.showAppbarElevation()
        }
    }

    fun hideAppbar() {
        haveHiddenAppbar = true
        (activity as AppCompatActivity?)?.supportActionBar?.hide()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_FRAGMENT_HIDDEN_APPBAR, if (haveHiddenAppbar) 1 else 0)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState ?: return
        haveHiddenAppbar = savedInstanceState.getInt(KEY_FRAGMENT_HIDDEN_APPBAR) == 1
    }

    /**
     * Show the appbar only if it has been hidden by this fragment, not another instance
     */
    fun showAppbar() {
        if (haveHiddenAppbar) {
            (activity as AppCompatActivity?)?.supportActionBar?.show()
            haveHiddenAppbar = false
        }
    }
}
