package it.simonesestito.wallapp.backend.repository

import android.widget.ImageView
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import it.simonesestito.wallapp.*
import it.simonesestito.wallapp.annotations.FORMAT_COVER
import it.simonesestito.wallapp.backend.model.Category
import it.simonesestito.wallapp.lifecycle.livedata.FirestoreLiveCollection
import it.simonesestito.wallapp.lifecycle.livedata.FirestoreLiveDocument
import it.simonesestito.wallapp.utils.map
import it.simonesestito.wallapp.utils.mapList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val firestore: FirebaseFirestore,
                                             private val storage: FirebaseStorage) {
    /**
     * List all the available categories from Firestore
     * @return LiveData observing the categories list
     */
    fun getCategories(): LiveData<List<Category>> {
        val ref = firestore
                .collection(FIRESTORE_CATEGORIES)
                .whereEqualTo(KEY_PUBLISHED, true)
                .orderBy(KEY_CREATION_DATE, Query.Direction.DESCENDING)

        return FirestoreLiveCollection(ref).mapList { snap ->
            Category(
                    snap.id,
                    snap.data ?: emptyMap()
            )
        }
    }

    /**
     * Get the category document from Firestore
     * @param id Category ID
     * @return LiveData of the Firestore document
     */
    fun getCategoryById(id: String): LiveData<Category> {
        val ref = firestore
                .document("$FIRESTORE_CATEGORIES/$id")

        return FirestoreLiveDocument(ref).map { snap ->
            Category(
                    snap.id,
                    snap.data ?: emptyMap()
            )
        }
    }

    /**
     * Load cover image from Firebase Storage in a target [ImageView] asynchronously
     * @param categoryId Category ID
     * @param imageView Target ImageView
     */
    fun loadCoverOn(categoryId: String, imageView: ImageView) {
        val imageRef = storage.getReference("$STORAGE_CATEGORIES/$categoryId/$FORMAT_COVER")

        GlideApp
                .with(imageView)
                .load(imageRef)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(imageView)
    }
}