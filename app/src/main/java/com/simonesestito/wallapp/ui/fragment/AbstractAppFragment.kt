/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.simonesestito.wallapp.ui.ElevatingAppbar

private const val KEY_FRAGMENT_HIDDEN_APPBAR = "have_hidden_appbar"

abstract class AbstractAppFragment : Fragment() {
    private var haveHiddenAppbar = false

    override fun onDestroyView() {
        super.onDestroyView()
        hideAppbarElevation()
        showAppbar()
    }

    protected fun adjustElevation(y: Int) {
        if (y > 0) {
            showAppbarElevation()
        } else {
            hideAppbarElevation()
        }
    }

    protected fun hideAppbarElevation() {
        val mainActivity = activity
        if (mainActivity is ElevatingAppbar) {
            mainActivity.hideAppbarElevation()
        }
    }

    protected fun showAppbarElevation() {
        val mainActivity = activity
        if (mainActivity is ElevatingAppbar) {
            mainActivity.showAppbarElevation()
        }
    }

    protected fun hideAppbar() {
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
    protected fun showAppbar() {
        if (haveHiddenAppbar) {
            (activity as AppCompatActivity?)?.supportActionBar?.show()
            haveHiddenAppbar = false
        }
    }
}
