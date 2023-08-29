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

package com.simonesestito.wallapp.ui.adapter

import androidx.annotation.MainThread
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.simonesestito.wallapp.DiffUtilCallback
import com.simonesestito.wallapp.Identifiable


abstract class AsyncAdapter<T : Identifiable<*>, VH : RecyclerView.ViewHolder> :
    RecyclerView.Adapter<VH>() {
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