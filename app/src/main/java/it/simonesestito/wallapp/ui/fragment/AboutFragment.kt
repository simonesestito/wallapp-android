/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.ui.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.google.android.material.snackbar.Snackbar
import it.simonesestito.wallapp.*
import it.simonesestito.wallapp.utils.openUrl
import kotlinx.android.synthetic.main.about_fragment.view.*


class AboutFragment : AbstractAppFragment() {
    override val title: CharSequence
        get() = getString(R.string.about_fragment_title)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.about_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.authorPortfolioButton1.setOnClickListener {
            requireContext().openUrl(AUTHOR_1_PORTFOLIO_WEBSITE)
        }

        view.authorPortfolioButton2.setOnClickListener {
            requireContext().openUrl(AUTHOR_2_PORTFOLIO_WEBSITE)
        }

        view.authorMailButton1.setOnClickListener {
            sendEmail(AUTHOR_1_MAIL)
        }

        view.authorMailButton2.setOnClickListener {
            sendEmail(AUTHOR_2_MAIL)
        }

        view.aboutFeedbackPlayButton.setOnClickListener {
            requireContext().openUrl(GOOGLE_PLAY_LINK)
        }
    }

    private fun sendEmail(address: String) {
        val intent = Intent(Intent.ACTION_SENDTO, "mailto:$address".toUri())
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Snackbar.make(view!!, R.string.activity_not_found_error, Snackbar.LENGTH_LONG).show()
        }
    }
}