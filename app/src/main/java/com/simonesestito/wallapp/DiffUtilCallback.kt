/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
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
