/*
 * Copyright 2020 Simone Sestito
 * This file is part of WallApp.
 *
 * WallApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WallApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WallApp.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.simonesestito.wallapp.ui.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.simonesestito.wallapp.ui.ElevatingAppbar

private const val KEY_FRAGMENT_HIDDEN_APPBAR = "have_hidden_appbar"

abstract class AbstractAppFragment : Fragment() {
    private var haveHiddenAppbar = false

    override fun onStart() {
        super.onStart()
        showAppbar()
        findElevatingAppbar()?.hideAppbarElevation()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_FRAGMENT_HIDDEN_APPBAR, if (haveHiddenAppbar) 1 else 0)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState ?: return
        haveHiddenAppbar = savedInstanceState.getInt(KEY_FRAGMENT_HIDDEN_APPBAR) == 1
        if (haveHiddenAppbar)
            hideAppbar()
        else
            showAppbar()
    }

    private fun showAppbar() {
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        haveHiddenAppbar = false
    }

    protected fun hideAppbar() {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        haveHiddenAppbar = true
    }

    protected fun adjustElevation(y: Int) {
        if (y > 0) {
            findElevatingAppbar()?.showAppbarElevation()
        } else {
            findElevatingAppbar()?.hideAppbarElevation()
        }
    }

    protected fun findElevatingAppbar(): ElevatingAppbar? {
        var parent = parentFragment
        while (parent != null) {
            if (parent is ElevatingAppbar)
                return parent
            parent = parent.parentFragment
        }

        if (activity is ElevatingAppbar) {
            return activity as ElevatingAppbar
        }

        return null
    }
}
