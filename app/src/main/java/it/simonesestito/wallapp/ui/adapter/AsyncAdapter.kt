package it.simonesestito.wallapp.ui.adapter

import androidx.annotation.MainThread
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import it.simonesestito.wallapp.DiffUtilCallback
import it.simonesestito.wallapp.Identifiable


abstract class AsyncAdapter<T : Identifiable<*>, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    private val asyncDiffer by lazy {
        AsyncListDiffer<T>(this, DiffUtilCallback<T>())
    }

    @MainThread
    fun updateDataSet(newData: List<T>) {
        asyncDiffer.submitList(newData)
    }

    protected val data: List<T>
        get() = asyncDiffer.currentList

    override fun getItemCount() = data.size
}