package it.simonesestito.wallapp.model

import android.os.Parcel
import it.simonesestito.wallapp.KEY_COUNT
import it.simonesestito.wallapp.KEY_DESCRIPTION
import it.simonesestito.wallapp.KEY_DISPLAY_NAME
import it.simonesestito.wallapp.backend.model.Category
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class CategoryTest {
    private val category = Category("cat-id", "name", "description", 3)


    @Test
    fun toParcel() {
        val parcel = Parcel.obtain()
        category.writeToParcel(parcel, 0)

        assertNotEquals(0, parcel.dataPosition())

        parcel.setDataPosition(0)
        assertEquals(0, parcel.dataPosition())

        assertEquals(category, Category.createFromParcel(parcel))
        parcel.setDataPosition(0)
        assertEquals(category, Category(parcel))
    }

    @Test
    fun fromFirebaseMap() {
        val map = mapOf(
                KEY_DISPLAY_NAME to category.displayName,
                KEY_DESCRIPTION to category.description,
                KEY_COUNT to category.wallpapersCount
        )
        assertEquals(category, Category(category.id, map))
    }
}