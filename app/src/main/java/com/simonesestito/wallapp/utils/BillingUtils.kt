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

package com.simonesestito.wallapp.utils

import android.app.Activity
import com.android.billingclient.api.*

suspend fun BillingClient.querySkuDetails(sku: String): SkuDetails? {
    val params = SkuDetailsParams.newBuilder()
            .setSkusList(listOf(sku))
            .setType(BillingClient.SkuType.INAPP)
            .build()
    val result = this.querySkuDetails(params)
    return result.skuDetailsList?.firstOrNull()
}

suspend fun BillingClient.launchBillingFlow(activity: Activity, sku: String) {
    // Get full SKU details
    val skuDetails = querySkuDetails(sku) ?: return

    val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()
    launchBillingFlow(activity, billingFlowParams)
}