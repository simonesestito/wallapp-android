package it.simonesestito.wallapp.utils

import androidx.recyclerview.widget.DiffUtil

/**
 * DiffUtil Callback implementation valid for 2 lists
 * Item list type must be the same and it must implement Identifiable
 */
class DiffUtilCallback<out T : Identifiable<*>>(private val oldList: List<T>,
                                                private val newList: List<T>) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return old.id == new.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return old == new
    }
}
