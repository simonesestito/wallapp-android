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

package com.simonesestito.wallapp.ui.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.widget.NestedScrollView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.review.ReviewManagerFactory
import com.simonesestito.wallapp.AUTHOR_MAIL
import com.simonesestito.wallapp.AUTHOR_PORTFOLIO_WEBSITE
import com.simonesestito.wallapp.GOOGLE_PLAY_LINK
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.ui.BillingDelegate
import com.simonesestito.wallapp.utils.addTopWindowInsetPadding
import com.simonesestito.wallapp.utils.openUrl
import kotlinx.android.synthetic.main.about_fragment.view.*


class AboutFragment : AbstractAppFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.about_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.aboutScrollContentRoot.addTopWindowInsetPadding()

        view.aboutScrollContentRoot.setOnScrollChangeListener { _: NestedScrollView?, _: Int, y: Int, _: Int, _: Int ->
            adjustElevation(y)
        }

        view.apply {
            authorPortfolioButton.setOnClickListener { context?.openUrl(AUTHOR_PORTFOLIO_WEBSITE) }
            authorMailButton.setOnClickListener { sendEmail(AUTHOR_MAIL) }
            aboutFeedbackMailButton.setOnClickListener { sendEmail(AUTHOR_MAIL) }

            aboutFeedbackPlayButton.setOnClickListener {
                showReviewDialog(fallback = { context?.openUrl(GOOGLE_PLAY_LINK) })
            }

            aboutDonationButton.setOnClickListener {
                (activity as? BillingDelegate)?.showDonationDialog()
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun sendEmail(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO, "mailto:$email".toUri()).apply {
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        }
        try {
            activity?.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Snackbar.make(requireView(), R.string.activity_not_found_error, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun showReviewDialog(fallback: () -> Unit) {
        val reviewManager = ReviewManagerFactory.create(requireContext())
        reviewManager
                .requestReviewFlow()
                .addOnCompleteListener {
                    if (it.isSuccessful && activity != null) {
                        // Actually launch the review dialog
                        reviewManager.launchReviewFlow(requireActivity(), it.result)
                    } else {
                        fallback()
                    }
                }
    }
}