package it.simonesestito.wallapp.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import androidx.core.os.postDelayed
import androidx.recyclerview.widget.RecyclerView
import com.yarolegovich.discretescrollview.DiscreteScrollView
import it.simonesestito.wallapp.utils.TAG

const val KEY_SAVED_SUPER_STATE = "super_state"
const val KEY_SAVED_LAYOUT_STATE = "layout_state"

class DiscreteRecyclerView(context: Context, attributeSet: AttributeSet) : DiscreteScrollView(context, attributeSet) {

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(KEY_SAVED_SUPER_STATE, super.onSaveInstanceState())
        bundle.putInt(KEY_SAVED_LAYOUT_STATE, currentItem)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable<Parcelable>(KEY_SAVED_SUPER_STATE))
            delayedScrollToPosition(state.getInt(KEY_SAVED_LAYOUT_STATE))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        super.setAdapter(adapter)
        Log.d(TAG, "Discrete adapter set")
    }

    private fun delayedScrollToPosition(currentItem: Int, delay: Long = 150) {
        Handler(Looper.getMainLooper()).postDelayed(delay) {
            scrollToPosition(currentItem)
        }
    }
}
