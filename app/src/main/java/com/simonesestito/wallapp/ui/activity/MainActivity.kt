/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.simonesestito.wallapp.NavGraphDirections
import com.simonesestito.wallapp.PREFS_IS_FIRST_LAUNCH_KEY
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.service.PreviewService
import com.simonesestito.wallapp.ui.ElevatingAppbar
import com.simonesestito.wallapp.utils.TAG
import com.simonesestito.wallapp.utils.sharedPreferences


class MainActivity : AppCompatActivity(), ElevatingAppbar {
    private val defaultAppbarElevation by lazy {
        resources.getDimension(R.dimen.default_appbar_elevation)
    }
    private val scrollAppbarElevation by lazy {
        resources.getDimension(R.dimen.scroll_appbar_elevation)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth.signInAnonymously().addOnCompleteListener {
            Log.d("MainActivity", "Logging in anonymously, success: ${it.isSuccessful}")
        }

        if (sharedPreferences.getBoolean(PREFS_IS_FIRST_LAUNCH_KEY, true)) {
            // Show first launch activity
            // It's responsibility of IntroActivity to set FIRST_LAUNCH to false
            startActivity(Intent(this, IntroActivity::class.java))
        }

        setContentView(R.layout.main_activity)
        findNavController(R.id.navHostFragment).let {
            setupActionBarWithNavController(this, it)
            it.addOnDestinationChangedListener { _, _, _ ->
                onDestinationChanged()
            }
        }
    }

    override fun onStart() {
        super.onStart()
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

    override fun showAppbarElevation() {
        supportActionBar?.elevation = scrollAppbarElevation
    }

    override fun hideAppbarElevation() {
        supportActionBar?.elevation = defaultAppbarElevation
    }
}
