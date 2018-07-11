package it.simonesestito.wallapp.persistence.repository

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import it.simonesestito.wallapp.FIRESTORE_CATEGORIES
import it.simonesestito.wallapp.livedata.FirestoreLiveCollection
import it.simonesestito.wallapp.livedata.FirestoreLiveDocument
import it.simonesestito.wallapp.map
import it.simonesestito.wallapp.mapList
import it.simonesestito.wallapp.model.Category


object CategoryRepository {
    fun getCategories(): LiveData<List<Category>> {
        val ref = FirebaseFirestore.getInstance()
                .collection(FIRESTORE_CATEGORIES)

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