/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
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
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.utils.addTopWindowInsetPadding
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

        //view.apply {
        //    authorPortfolioButton1.setOnClickListener {
        //        context?.openUrl(AUTHOR_1_PORTFOLIO_WEBSITE)
        //    }
//
        //    authorPortfolioButton2.setOnClickListener {
        //        context?.openUrl(AUTHOR_2_PORTFOLIO_WEBSITE)
        //    }
//
        //    authorMailButton1.setOnClickListener {
        //        sendEmail(AUTHOR_1_MAIL)
        //    }
//
        //    authorMailButton2.setOnClickListener {
        //        sendEmail(AUTHOR_2_MAIL)
        //    }
//
        //    aboutFeedbackPlayButton.setOnClickListener {
        //        context?.openUrl(GOOGLE_PLAY_LINK)
        //    }
//
        //    aboutFeedbackMailButton.setOnClickListener {
        //        sendEmail(AUTHOR_1_MAIL)
        //    }
        //}
    }

    private fun sendEmail(address: String) {
        val intent = Intent(Intent.ACTION_SENDTO, "mailto:$address".toUri()).apply {
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        }
        try {
            activity?.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Snackbar.make(requireView(), R.string.activity_not_found_error, Snackbar.LENGTH_LONG).show()
        }
    }
}