package it.simonesestito.wallapp.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration


class FirestoreLiveCollection(private val ref: CollectionReference) : LiveData<List<DocumentSnapshot>>() {
    private var listenerRegistration: ListenerRegistration? = null

    override fun onActive() {
        super.onActive()
        listenerRegistration?.remove()
        listenerRegistration = ref.addSnapshotListener { snap, err ->
            if (snap == null) {
                err?.printStackTrace()
            } else {
                value = snap.documents
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        listenerRegistration?.remove()
        listenerRegistration = null
    }
}