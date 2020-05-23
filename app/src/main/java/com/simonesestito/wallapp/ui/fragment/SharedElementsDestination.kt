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

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import com.simonesestito.wallapp.utils.addListener

/**
 * Special fragment that acts as a destination for a SharedElements transition
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
abstract class SharedElementsDestination : AbstractAppFragment() {
    /**
     * Determine if the postponed transition should be started
     * automatically by this fragment.
     * Set this attribute to false if the fragment has to do something async before
     */
    protected open var shouldStartTransition = true

    /**
     * Called when the fragment needs to create the Shared Elements transition
     * @return Shared Elements transition to apply
     */
    protected open fun createSharedElementsEnterTransition(): Transition =
            TransitionInflater.from(context)
                    .inflateTransition(android.R.transition.move)
                    .apply {
                        interpolator = DecelerateInterpolator(2.0f)
                        duration = requireContext().resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
                    }.addListener(
                            onStart = { onPreSharedElementsTransition() },
                            onEnd = { onPostSharedElementsTransition() }
                    )

    /**
     * @return The transition or null if you don't want a return transition
     */
    protected open fun createSharedElementsReturnTransition(): Transition? =
            TransitionInflater.from(context)
                    .inflateTransition(android.R.transition.move)
                    .apply {
                        interpolator = DecelerateInterpolator(2.0f)
                        duration = requireContext().resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
                    }

    /**
     * Called after the creation of the view but before transition start
     * Here you have to set the transition name on shared views
     * and everything needed before transition start
     * @param createdView View created in onCreateView
     */
    protected abstract fun onPrepareSharedElements(createdView: View)

    /**
     * Called when the transition has just started
     * From onStart listener attached on the transition
     */
    protected open fun onPostSharedElementsTransition() {
        // Empty
    }

    /**
     * Called after the transition finished
     * From onEnd listener attached on the transition
     */
    protected open fun onPreSharedElementsTransition() {
        // Empty
    }

    //region Fragment Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = createSharedElementsEnterTransition()
        sharedElementReturnTransition = createSharedElementsReturnTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        onPrepareSharedElements(view)

        if (shouldStartTransition)
            startPostponedEnterTransition()
    }
    //endregion
}