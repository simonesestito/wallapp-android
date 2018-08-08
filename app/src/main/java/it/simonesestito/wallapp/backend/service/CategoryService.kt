package it.simonesestito.wallapp.backend.service

import android.widget.ImageView
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import it.simonesestito.wallapp.*
import it.simonesestito.wallapp.annotations.FORMAT_COVER
import it.simonesestito.wallapp.backend.model.Category
import it.simonesestito.wallapp.utils.livedata.FirestoreLiveCollection
import it.simonesestito.wallapp.utils.livedata.FirestoreLiveDocument
import it.simonesestito.wallapp.utils.map
import it.simonesestito.wallapp.utils.mapList

object CategoryService {
    fun getCategories(): LiveData<List<Category>> {
        val ref = FirebaseFirestore.getInstance()
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

    fun getCategoryById(id: String): LiveData<Category> {
        val ref = FirebaseFirestore.getInstance()
                .document("$FIRESTORE_CATEGORIES/$id")

        return FirestoreLiveDocument(ref).map { snap ->
            Category(
                    snap.id,
                    snap.data ?: emptyMap()
            )
        }
    }

    fun loadCoverOn(categoryId: String, imageView: ImageView) {
        val imageRef = FirebaseStorage
                .getInstance()
                .getReference("$STORAGE_CATEGORIES/$categoryId/$FORMAT_COVER")

        GlideApp
                .with(imageView.context)
                .load(imageRef)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(imageView)
    }
}