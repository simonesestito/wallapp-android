/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.simonesestito.wallapp.enums.ALL_CATEGORY_GROUPS
import com.simonesestito.wallapp.ui.fragment.ChildCategoriesFragment

class ChildCategoriesFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        val categoryGroup = ALL_CATEGORY_GROUPS[position]
        return ChildCategoriesFragment.newInstance(categoryGroup)
    }

    override fun getItemCount() = ALL_CATEGORY_GROUPS.size
}