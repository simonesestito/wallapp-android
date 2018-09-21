/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.adapter

import androidx.annotation.MainThread
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.simonesestito.wallapp.DiffUtilCallback
import com.simonesestito.wallapp.Identifiable


abstract class AsyncAdapter<T : Identifiable<*>, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    private val asyncDiffer by lazy {
        AsyncListDiffer<T>(this, DiffUtilCallback<T>())
    }

    @MainThread
    fun updateDataSet(newData: List<T>) {
        asyncDiffer.submitList(newData)
    }

    val data: List<T>
        get() = asyncDiffer.currentList

    override fun getItemCount() = data.size
}