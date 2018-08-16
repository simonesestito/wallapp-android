package it.simonesestito.wallapp.fragment

import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.backend.model.Wallpaper
import it.simonesestito.wallapp.ui.activity.MainActivity
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class WallpaperTest {
    @get:Rule
    val rule = ActivityTestRule(MainActivity::class.java)

    private val fakeWallpaper = Wallpaper("id", "catId")

    @Before
    fun init() {
        rule.runOnUiThread {
            rule.activity.findNavController(R.id.navHostFragment)
                    .navigate(R.id.wallpaperFragment, bundleOf(
                            "wallpaper" to fakeWallpaper
                    ))
        }
    }

    @Test
    fun testAppbar() {
        assertFalse(rule.activity.supportActionBar!!.isShowing)
        pressBackUnconditionally()
        assertTrue(rule.activity.supportActionBar!!.isShowing)
    }

    @Test
    fun testSetupBottomSheet() {
        onView(withId(R.id.downloadFab))
                .perform(click())

        onView(withId(R.id.wallpaperSetup))
                .check(matches(isDisplayed()))
    }
}