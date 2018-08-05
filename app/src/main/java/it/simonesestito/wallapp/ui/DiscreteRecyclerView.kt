package it.simonesestito.wallapp.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import androidx.core.os.postDelayed
import androidx.customview.view.AbsSavedState
import androidx.recyclerview.widget.RecyclerView
import com.yarolegovich.discretescrollview.DiscreteScrollView

class DiscreteRecyclerView(context: Context, attributeSet: AttributeSet) : DiscreteScrollView(context, attributeSet) {

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()!!
        return DiscreteState(currentItem, superState)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is DiscreteState) {
            delayedScrollToPosition(state.currentItem)
            super.onRestoreInstanceState(state.superState)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    fun delayedScrollToPosition(currentItem: Int, delay: Long = 150) {
        Handler(Looper.getMainLooper()).postDelayed(delay) {
            scrollToPosition(currentItem)
        }
    }

    /**
     * Implementation similar to RecyclerView.SavedState
     */
    class DiscreteState : AbsSavedState {
        val layoutState: Parcelable
        val currentItem: Int

        // Called by onRestoreInstanceState() when Parcelable needs to be recreated
        constructor(input: Parcel, classLoader: ClassLoader?) : super(input, classLoader) {
            this.layoutState = input.readParcelable(classLoader
                    ?: RecyclerView.LayoutManager::class.java.classLoader)
            this.currentItem = input.readInt()
        }

        // Called by onSaveInstanceState() to save state as a Parcelable
        constructor(currentItem: Int, layoutState: Parcelable) : super(layoutState) {
            this.layoutState = layoutState
            this.currentItem = currentItem
        }

        override fun writeToParcel(dest: Parcel?, flags: Int) {
            super.writeToParcel(dest, flags)
            dest?.writeParcelable(layoutState, 0)
            dest?.writeInt(currentItem)
        }
    }
}
