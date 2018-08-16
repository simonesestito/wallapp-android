package it.simonesestito.wallapp.fragment

import android.os.SystemClock
import androidx.navigation.findNavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.backend.model.Category
import it.simonesestito.wallapp.ui.DiscreteRecyclerView
import it.simonesestito.wallapp.ui.activity.MainActivity
import it.simonesestito.wallapp.ui.adapter.WallpapersVH
import it.simonesestito.wallapp.ui.fragment.CategoriesListFragmentDirections
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CategoryTest {
    private val fakeCategory = Category(
            "backgrounds",
            "Name",
            "Description",
            5
    )

    @get:Rule
    val rule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun init() {
        rule.runOnUiThread {
            rule.activity.findNavController(R.id.navHostFragment)
                    .navigate(CategoriesListFragmentDirections
                            .toCategory(fakeCategory))
        }
    }

    @Test
    fun testDescription() {
        onView(withId(R.id.categoryDescription))
                .check(matches(withText(fakeCategory.description)))
    }

    @Test
    fun testTitle() {
        assertEquals(fakeCategory.displayName, rule.activity.title)
    }

    @Test
    fun testSavedState() {
        SystemClock.sleep(400) // Wait loading

        onView(withId(R.id.wallpapersRecyclerView))
                .perform(actionOnItemAtPosition<WallpapersVH>(1, click()))

        onView(withId(R.id.wallpaperImage))
                .perform(pressBack())

        onView(withId(R.id.wallpapersRecyclerView))
                .check { view, _ ->
                    view as DiscreteRecyclerView
                    assertEquals(1, view.currentItem)
                }
    }
}