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

package com.simonesestito.wallapp

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

/**
 * DiffUtil Callback implementation valid for 2 lists
 * Item list type must be the same and it must implement Identifiable
 */
class DiffUtilCallback<T : Identifiable<*>> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(old: T, new: T) = old.id == new.id
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(old: T, new: T) = old == new
}
