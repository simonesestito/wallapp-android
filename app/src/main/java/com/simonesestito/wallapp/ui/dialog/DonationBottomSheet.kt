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

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.simonesestito.wallapp.PREFS_DONATION_DIALOG_SHOWN
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.model.ParcelableSkuDetails
import com.simonesestito.wallapp.ui.BillingDelegate
import com.simonesestito.wallapp.ui.adapter.DonationItemsAdapter
import com.simonesestito.wallapp.utils.sharedPreferences
import com.simonesestito.wallapp.utils.tryDismiss

class DonationBottomSheet : AbstractAppBottomSheet() {
    companion object {
        private const val ARG_SKU_DETAILS = "sku_details"
        private const val ARG_ASK_DONATION = "ask_donation"

        fun createDialog(skuDetails: List<ParcelableSkuDetails>, askDonation: Boolean = false) = DonationBottomSheet().apply {
            val args = bundleOf()
            if (skuDetails is ArrayList)
                args.putParcelableArrayList(ARG_SKU_DETAILS, skuDetails)
            else
                args.putParcelableArrayList(ARG_SKU_DETAILS, ArrayList(skuDetails))
            args.putBoolean(ARG_ASK_DONATION, askDonation)
            arguments = args

            isCancelable = !askDonation
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set donation dialog as shown
        sharedPreferences.edit {
            putBoolean(PREFS_DONATION_DIALOG_SHOWN, true)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (arguments?.getBoolean(ARG_ASK_DONATION, false) == true) {
            Toast.makeText(requireContext(), R.string.donation_dismiss_toast, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.donation_bottomsheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = requireArguments()
        val skuDetails = args.getParcelableArrayList<ParcelableSkuDetails>(ARG_SKU_DETAILS)
                ?: emptyList()
        val askDonation = args.getBoolean(ARG_ASK_DONATION, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.donationItemsRecyclerView)
        val donationAskGroup = view.findViewById<View>(R.id.donationAskGroup)
        val donationListGroup = view.findViewById<View>(R.id.donationListGroup)

        // Set visibility
        if (askDonation) {
            donationAskGroup.visibility = View.VISIBLE
            donationListGroup.visibility = View.GONE
        }

        val adapter = DonationItemsAdapter(skuDetails)
        adapter.onItemClickListener = {
            (activity as? BillingDelegate)?.initPurchase(it.id)
            tryDismiss()
        }
        recyclerView.adapter = adapter

        view.findViewById<View>(R.id.donationAskCancelButton).setOnClickListener { tryDismiss() }
        view.findViewById<View>(R.id.donationAskDonateButton).setOnClickListener {
            donationAskGroup.visibility = View.GONE
            donationListGroup.visibility = View.VISIBLE
            isCancelable = true
        }
    }
}