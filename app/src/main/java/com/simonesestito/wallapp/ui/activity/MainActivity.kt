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
import android.graphics.Color.rgb
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.android.billingclient.api.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.simonesestito.wallapp.*
import com.simonesestito.wallapp.backend.androidservice.PreviewService
import com.simonesestito.wallapp.backend.model.ParcelableSkuDetails
import com.simonesestito.wallapp.ui.BillingDelegate
import com.simonesestito.wallapp.ui.ElevatingAppbar
import com.simonesestito.wallapp.ui.dialog.DonationBottomSheet
import com.simonesestito.wallapp.utils.TAG
import com.simonesestito.wallapp.utils.isDarkTheme
import com.simonesestito.wallapp.utils.launchBillingFlow
import com.simonesestito.wallapp.utils.sharedPreferences
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size


class MainActivity : AppCompatActivity(), ElevatingAppbar, BillingDelegate {
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        Log.i("BILLING", "Billing result = ${billingResult.debugMessage}")
        Log.i("BILLING", "Purchases = " + purchases?.map { it.sku })
        purchases?.forEach { handlePurchase(it) }
    }

    private var skuDetails: List<SkuDetails>? = null

    private val billingClientStateListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            Log.i("BILLING", "onBillingSetupFinished")
            Log.i("BILLING", "billingResult ${billingResult.debugMessage}")

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val productIds = listOf(
                        DONATION_COFFEE_SKU,
                        DONATION_HAMBURGER_SKU,
                        DONATION_DINNER_SKU,
                        DONATION_XL_TIP_SKU
                )
                val requestParams = SkuDetailsParams.newBuilder()
                        .setSkusList(productIds)
                        .setType(BillingClient.SkuType.INAPP)
                        .build()
                billingClient.querySkuDetailsAsync(requestParams) { skuDetailsResult, skuDetails ->
                    if (skuDetailsResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetails != null) {
                        // Success.
                        this@MainActivity.skuDetails = skuDetails.sortedBy { it.priceAmountMicros }
                        onSkuDetailsAvailable()
                    }
                }

                billingClient.queryPurchases(BillingClient.SkuType.INAPP).purchasesList?.forEach {
                    handlePurchase(it)
                }
            }
        }

        override fun onBillingServiceDisconnected() = Unit
    }

    private val billingClient by lazy {
        BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build()
    }

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

        billingClient.startConnection(billingClientStateListener)
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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.donationDialogItem)?.let {
            val appliedWallpapersCounter = sharedPreferences.getInt(PREFS_APPLIED_WALLPAPERS_COUNTER, 0)
            it.isVisible = skuDetails != null && appliedWallpapersCounter >= APPLIED_WALLS_COUNTER_DONATION_THRESHOLD
            it.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun onDestinationChanged() {
        // Update Menu because an item depends on current destination
        invalidateOptionsMenu()
    }

    private fun askDonationIfNecessary() {
        val appliedWallpapersCounter = sharedPreferences.getInt(PREFS_APPLIED_WALLPAPERS_COUNTER, 0)
        val donationDialogShown = sharedPreferences.getBoolean(PREFS_DONATION_DIALOG_SHOWN, false)

        Log.e("MainActivity", "askDonationIfNecessary")
        if (appliedWallpapersCounter >= APPLIED_WALLS_COUNTER_DONATION_THRESHOLD
                && !donationDialogShown
                && !skuDetails.isNullOrEmpty()) {
            showDonationDialog(askDonation = true)
        }
    }

    override fun showDonationDialog(askDonation: Boolean) {
        val parcelableSkuDetails = skuDetails!!.map(ParcelableSkuDetails::fromSkuDetails)
        DonationBottomSheet
                .createDialog(parcelableSkuDetails, askDonation)
                .show(supportFragmentManager, null)
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
                R.id.donationDialogItem -> {
                    val parcelableSkuDetails = skuDetails!!.map(ParcelableSkuDetails::fromSkuDetails)
                    DonationBottomSheet
                            .createDialog(parcelableSkuDetails)
                            .show(supportFragmentManager, null)
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

    private fun onSkuDetailsAvailable() {
        invalidateOptionsMenu()
        askDonationIfNecessary()
    }

    private fun handlePurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

        billingClient.consumeAsync(consumeParams) { billingResult, _ ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Handle the success of the consume operation.
                Toast.makeText(this, R.string.donation_success_message, Toast.LENGTH_LONG).show()
                showConfettiView()
            }
        }
    }

    private fun showConfettiView() {
        val confettiView: KonfettiView = findViewById(R.id.confettiView)
        confettiView.doOnPreDraw {
            // Burst from center
            val wallappIconColors = listOf(
                    rgb(243, 164, 134),
                    rgb(49, 204, 169),
                    rgb(77, 147, 228),
                    rgb(207, 156, 226),
                    rgb(130, 226, 139),
                    rgb(218, 126, 127)
            )
            confettiView.build()
                    .addColors(wallappIconColors)
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 8f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(4000)
                    .addShapes(Shape.Circle, Shape.Square)
                    .addSizes(Size(12), Size(16, 6f))
                    .setPosition(confettiView.width / 2f, confettiView.height / 3f)
                    .burst(200)
        }
        confettiView.visibility = View.VISIBLE
    }

    override fun initPurchase(sku: String) {
        if (!billingClient.isReady)
            return

        lifecycleScope.launchWhenResumed {
            billingClient.launchBillingFlow(this@MainActivity, sku)
        }
    }
}
