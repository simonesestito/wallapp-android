/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.activity

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import com.simonesestito.wallapp.PREFS_IS_FIRST_LAUNCH_KEY
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.utils.sharedPreferences


class IntroActivity : AppIntro() {

    // TODO: Update drawables
    private val introElements = arrayOf(
            AppIntroElement(
                    drawable = R.mipmap.ic_launcher_web,
                    title = R.string.intro_slide_welcome_title,
                    description = R.string.intro_slide_welcome_description
            ),
            AppIntroElement(
                    drawable = R.mipmap.ic_launcher_web,
                    title = R.string.intro_slide_unique_wallpapers_title,
                    description = R.string.intro_slide_unique_wallpapers_description
            ),
            AppIntroElement(
                    drawable = R.mipmap.ic_launcher_web,
                    title = R.string.intro_slide_no_ads_title,
                    description = R.string.intro_slide_no_ads_description
            ),
            AppIntroElement(
                    drawable = R.mipmap.ic_launcher_web,
                    title = R.string.intro_slide_no_subs_title,
                    description = R.string.intro_slide_no_subs_description
            )
    )

    override fun getLayoutId() = R.layout.intro_app_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Display intro elements
        introElements.map { element ->
            val page = SliderPage().apply {
                title = getString(element.title)
                description = getString(element.description)
                imageDrawable = element.drawable
                titleColor = ContextCompat.getColor(this@IntroActivity, R.color.text_intro_color)
                descColor = ContextCompat.getColor(this@IntroActivity, R.color.text_intro_color)
                bgColor = ContextCompat.getColor(this@IntroActivity, R.color.activity_background_color)
            }

            return@map AppIntroFragment.newInstance(page)
        }.forEach { addSlide(it) }

        setIndicatorColor(
                ContextCompat.getColor(this, R.color.intro_indicator_active_page),
                ContextCompat.getColor(this, R.color.intro_indicator_unselected_page)
        )
        skipButtonEnabled = false
        backButtonVisibilityWithDone = false
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Set first launch as completed
        sharedPreferences.edit {
            putBoolean(PREFS_IS_FIRST_LAUNCH_KEY, false)
        }
        finish()
    }

    private data class AppIntroElement(
            @DrawableRes val drawable: Int,
            @StringRes val title: Int,
            @StringRes val description: Int
    )
}
