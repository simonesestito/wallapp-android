package it.simonesestito.wallapp.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import it.simonesestito.wallapp.utils.addListener

/**
 * Special fragment that acts as a destination for a SharedElements transition
 */
abstract class SharedElementsDestination : AbstractAppFragment() {
    /**
     * Called when the fragment needs to create the Shared Elements transition
     * @return Shared Elements transition to apply
     */
    open fun createSharedElementsEnterTransition(): Transition =
            TransitionInflater.from(context)
                    .inflateTransition(android.R.transition.move)
                    .apply {
                        interpolator = DecelerateInterpolator(2.0f)
                        duration = 400
                    }.addListener(
                            onStart = { onPreSharedElementsTransition() },
                            onEnd = { onPostSharedElementsTransition() }
                    )

    /**
     * Called after the creation of the view but before transition start
     * Here you have to set the transition name on shared views
     * and everything needed before transition start
     * @param createdView View created in onCreateView
     */
    abstract fun onPrepareSharedElements(createdView: View)

    /**
     * Called when the transition has just started
     * From onStart listener attached on the transition
     */
    open fun onPostSharedElementsTransition() {
        // Empty
    }

    /**
     * Called after the transition finished
     * From onEnd listener attached on the transition
     */
    open fun onPreSharedElementsTransition() {
        // Empty
    }

    //region Fragment Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        sharedElementEnterTransition = createSharedElementsEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onPrepareSharedElements(view)
        startPostponedEnterTransition()
    }
    //endregion
}