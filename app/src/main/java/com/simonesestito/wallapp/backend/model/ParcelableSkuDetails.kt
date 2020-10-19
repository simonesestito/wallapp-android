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
package com.simonesestito.wallapp.backend.model

import android.os.Parcelable
import com.android.billingclient.api.SkuDetails
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ParcelableSkuDetails(
        val id: String,
        val name: String,
        val description: String,
        val paidPrice: Int,
        val currencySign: String
) : Parcelable {
    companion object {
        fun fromSkuDetails(skuDetails: SkuDetails): ParcelableSkuDetails {
            // Extract currency sign from formatted Play Store price
            val unwantedChars = charArrayOf(',', '.', ' ', 160.toChar() /* Non breaking space */)
            val currencySign = skuDetails.price.toCharArray()
                    .asSequence()
                    .filterNot { it.isDigit() }
                    .filterNot { unwantedChars.contains(it) }
                    // Use the currency code as last resort
                    .firstOrNull()?.toString() ?: skuDetails.priceCurrencyCode

            // Price represented in cents
            val priceCents = skuDetails.priceAmountMicros / 1_000_0

            // Full title: "product name (App name)"
            // Short title: "product name"
            val shortTitle = skuDetails.title.split('(')[0].trimEnd()

            return ParcelableSkuDetails(
                    skuDetails.sku,
                    shortTitle,
                    skuDetails.description,
                    priceCents.toInt(),
                    currencySign
            )
        }
    }

    @IgnoredOnParcel
    val formattedPaidPriceNoTaxes = currencySign + ' ' + String.format("%.2f", paidPrice / 100.0)
}