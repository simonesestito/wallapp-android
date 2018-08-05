package it.simonesestito.wallapp.data.repository

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import it.simonesestito.wallapp.FIRESTORE_CATEGORIES
import it.simonesestito.wallapp.KEY_CREATION_DATE
import it.simonesestito.wallapp.KEY_PUBLISHED
import it.simonesestito.wallapp.data.model.Category
import it.simonesestito.wallapp.livedata.FirestoreLiveCollection
import it.simonesestito.wallapp.livedata.FirestoreLiveDocument
import it.simonesestito.wallapp.utils.map
import it.simonesestito.wallapp.utils.mapList


object CategoryRepository {
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
}