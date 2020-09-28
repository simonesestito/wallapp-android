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

package com.simonesestito.wallapp.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.model.ParcelableSkuDetails
import com.simonesestito.wallapp.ui.BillingDelegate
import com.simonesestito.wallapp.ui.adapter.DonationItemsAdapter
import com.simonesestito.wallapp.utils.tryDismiss

class DonationBottomSheet : AbstractAppBottomSheet() {
    companion object {
        private const val ARG_SKU_DETAILS = "sku_details"

        fun createDialog(skuDetails: List<ParcelableSkuDetails>) = DonationBottomSheet().apply {
            val args = bundleOf()
            if (skuDetails is ArrayList)
                args.putParcelableArrayList(ARG_SKU_DETAILS, skuDetails)
            else
                args.putParcelableArrayList(ARG_SKU_DETAILS, ArrayList(skuDetails))
            arguments = args
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.donation_bottomsheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val skuDetails = requireArguments().getParcelableArrayList<ParcelableSkuDetails>(ARG_SKU_DETAILS)
                ?: emptyList()
        val adapter = DonationItemsAdapter(skuDetails)
        adapter.onItemClickListener = {
            (activity as? BillingDelegate)?.initPurchase(it.id)
            tryDismiss()
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.donationItemsRecyclerView)
        recyclerView.adapter = adapter
    }
}