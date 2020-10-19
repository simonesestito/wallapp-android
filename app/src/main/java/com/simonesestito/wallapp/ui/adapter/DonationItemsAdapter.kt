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

package com.simonesestito.wallapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.simonesestito.wallapp.DONATION_COFFEE_SKU
import com.simonesestito.wallapp.DONATION_DINNER_SKU
import com.simonesestito.wallapp.DONATION_HAMBURGER_SKU
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.model.ParcelableSkuDetails

class DonationItemsAdapter(private val skuDetails: List<ParcelableSkuDetails>) : RecyclerView.Adapter<DonationItemsAdapter.DonationItemViewHolder>() {
    var onItemClickListener: DonationItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationItemViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.donation_recyclerview_item, parent, false)
        return DonationItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonationItemViewHolder, position: Int) {
        val item = skuDetails[position]
        val context = holder.itemView.context

        holder.icon.setImageResource(getIconResFromSkuId(item.id))
        holder.title.text = context.resources.getString(R.string.donation_item_title, item.name, item.formattedPaidPriceNoTaxes)
        holder.subtitle.text = item.description

        holder.itemView.tag = item
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(it.tag as ParcelableSkuDetails)
        }
    }

    @DrawableRes
    private fun getIconResFromSkuId(id: String) = when (id) {
        DONATION_COFFEE_SKU -> R.drawable.ic_coffee
        DONATION_HAMBURGER_SKU -> R.drawable.ic_hamburger
        DONATION_DINNER_SKU -> R.drawable.ic_food_dinner
        else -> R.drawable.ic_gift_outline
    }

    override fun getItemCount() = skuDetails.size

    class DonationItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon = itemView.findViewById<ImageView>(R.id.donationItemIcon)!!
        val title = itemView.findViewById<TextView>(R.id.donationItemTitle)!!
        val subtitle = itemView.findViewById<TextView>(R.id.donationItemSubtitle)!!
    }
}

typealias DonationItemClickListener = (ParcelableSkuDetails) -> Unit