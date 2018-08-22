/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.activity

import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.ui.activity.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val rule = ActivityTestRule(MainActivity::class.java)

    private var shownElevation: Float = 0f
    private var hiddenElevation: Float = 0f

    @Before
    fun init() {
        shownElevation = rule.activity.resources.getDimension(R.dimen.scroll_appbar_elevation)
        hiddenElevation = rule.activity.resources.getDimension(R.dimen.default_appbar_elevation)
    }

    @Test
    fun defaultElevation() {
        assertEquals(hiddenElevation, getCurrentElevation())
    }

    @Test
    fun testShowElevation() {
        rule.activity.showAppbarElevation()
        assertEquals(shownElevation, getCurrentElevation())
    }

    @Test
    fun testHiddenElevation() {
        rule.activity.showAppbarElevation()
        rule.activity.hideAppbarElevation()
        assertEquals(hiddenElevation, getCurrentElevation())
    }

    private fun getCurrentElevation() =
            rule.activity.supportActionBar!!.elevation
}