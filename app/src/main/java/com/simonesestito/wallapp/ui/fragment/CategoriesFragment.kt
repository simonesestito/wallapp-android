/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.enums.ALL_CATEGORY_GROUPS
import com.simonesestito.wallapp.ui.ElevatingAppbar
import com.simonesestito.wallapp.ui.adapter.ChildCategoriesFragmentAdapter
import com.simonesestito.wallapp.utils.addTopWindowInsetPadding
import com.simonesestito.wallapp.utils.isDarkTheme
import com.simonesestito.wallapp.utils.setupWithViewPager
import kotlinx.android.synthetic.main.categories_fragment.*
import kotlinx.android.synthetic.main.categories_fragment.view.*

class CategoriesFragment : AbstractAppFragment(), ElevatingAppbar {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.categories_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.categoriesFragmentRoot.addTopWindowInsetPadding()
        view.categoriesGroupViewPager.adapter = ChildCategoriesFragmentAdapter(this)
        view.categoriesTabLayout.setupWithViewPager(view.categoriesGroupViewPager, ALL_CATEGORY_GROUPS)
    }

    override fun showAppbarElevation() {
        // Different behaviour based on the current theme
        // In dark theme, elevation is made with different background color
        // while in light theme, elevation uses the well-known Material shadows
        //
        // Applying a shadow on Activity appbar in light theme would result in a duplicated shadow
        if (requireContext().isDarkTheme()) {
            findElevatingAppbar()?.showAppbarElevation()
        } else {
            findElevatingAppbar()?.hideAppbarElevation()
        }

        categoriesTabLayout.elevation = resources.getDimension(R.dimen.scroll_appbar_elevation)
    }

    override fun hideAppbarElevation() {
        findElevatingAppbar()?.hideAppbarElevation()
        categoriesTabLayout.elevation = resources.getDimension(R.dimen.default_appbar_elevation)
    }
}
