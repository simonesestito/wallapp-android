/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.lifecycle.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query


class FirestoreLiveCollection(private val ref: Query) : LiveData<List<DocumentSnapshot>>() {
    private var listenerRegistration: ListenerRegistration? = null

    override fun onActive() {
        super.onActive()
        listenerRegistration?.remove()
        listenerRegistration = ref.addSnapshotListener { snap, err ->
            if (snap == null || err != null) {
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