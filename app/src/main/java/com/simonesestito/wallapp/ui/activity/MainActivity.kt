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

package com.simonesestito.wallapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.simonesestito.wallapp.NavGraphDirections
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.androidservice.PreviewService
import com.simonesestito.wallapp.ui.ElevatingAppbar
import com.simonesestito.wallapp.utils.TAG
import com.simonesestito.wallapp.utils.isDarkTheme


class MainActivity : AppCompatActivity(), ElevatingAppbar {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        setContentView(R.layout.main_activity)

        // Set custom toolbar
        setSupportActionBar(findViewById(R.id.appToolbar))

        // TODO: IntroActivity
        //if (sharedPreferences.getBoolean(PREFS_IS_FIRST_LAUNCH_KEY, true)) {
            // Show first launch activity
            // It's responsibility of IntroActivity to set FIRST_LAUNCH to false
        //}

        // Draw edge to edge
        findViewById<View>(R.id.root).systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        findNavController(R.id.navHostFragment).let {
            setupActionBarWithNavController(this, it)
            it.addOnDestinationChangedListener { _, _, _ ->
                onDestinationChanged()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Be sure to stop PreviewService every time the user launches the main app
        stopService(Intent(this, PreviewService::class.java))
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == Intent.ACTION_VIEW) {
            Log.d(TAG, "Received VIEW Intent with url: ${intent.data}")
            findNavController(R.id.navHostFragment).handleDeepLink(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            menu.clear()
            MenuInflater(this).inflate(R.menu.main_activity_menu, menu)

            val isInAbout = findNavController(R.id.navHostFragment).currentDestination?.id == R.id.aboutFragment
            // Show About menu item only if user is not in About page yet
            menu.findItem(R.id.aboutMenuItem).isVisible = !isInAbout
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun onDestinationChanged() {
        // Update Menu because an item depends on current destination
        invalidateOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.aboutMenuItem -> {
                    findNavController(R.id.navHostFragment).let {
                        if (it.currentDestination?.id != R.id.aboutFragment)
                            it.navigate(NavGraphDirections.openAbout())
                    }
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        hideAppbarElevation()
        val navController = findNavController(R.id.navHostFragment)
        setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.navHostFragment).navigateUp()

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        supportActionBar?.title = title
    }

    override fun showAppbarElevation() =
            updateElevation(resources.getDimension(R.dimen.scroll_appbar_elevation))

    override fun hideAppbarElevation() =
            updateElevation(resources.getDimension(R.dimen.default_appbar_elevation))

    private fun updateElevation(elevation: Float) {
        findViewById<AppBarLayout>(R.id.appBarLayout)!!.elevation = elevation
        // Different behaviour based on the current theme
        // In dark theme, elevation is made with different background color
        // while in light theme, elevation uses the well-known Material shadows
        //
        // Applying a shadow on Toolbar in light theme would result in a duplicated shadow
        if (isDarkTheme()) {
            findViewById<MaterialToolbar>(R.id.appToolbar)!!.elevation = elevation
        }
    }
}
