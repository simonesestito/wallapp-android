package it.simonesestito.wallapp.fragment

import android.os.SystemClock
import androidx.navigation.findNavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.ui.activity.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoriesListTest {
    @get:Rule
    val rule = ActivityTestRule(MainActivity::class.java)

    private var shownElevation: Float = 0f
    private var hiddenElevation: Float = 0f

    @Before
    fun init() {
        shownElevation = rule.activity.resources.getDimension(R.dimen.scroll_appbar_elevation)
        hiddenElevation = rule.activity.resources.getDimension(R.dimen.default_appbar_elevation)

        rule.activity.runOnUiThread {
            rule.activity.findNavController(R.id.navHostFragment)
                    .navigate(R.id.categoriesFragment)
        }
    }

    @Test
    fun scrollElevation() {
        SystemClock.sleep(1000) // Wait async loading
        onView(withId(R.id.categoriesRecyclerView))
                .perform(swipeUp())
                .check { _, _ ->
                    assertEquals(shownElevation, rule.activity.supportActionBar?.elevation)
                }
                .perform(swipeDown())
                .check { _, _ ->
                    assertEquals(hiddenElevation, rule.activity.supportActionBar?.elevation)
                }
    }
}