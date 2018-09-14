/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.lifecycle.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration


class FirestoreLiveDocument(private val ref: DocumentReference) : LiveData<DocumentSnapshot>() {
    private var listenerRegistration: ListenerRegistration? = null

    override fun onActive() {
        super.onActive()
        listenerRegistration?.remove()
        listenerRegistration = ref.addSnapshotListener { snap, err ->
            if (snap == null) {
                err?.printStackTrace()
            } else {
                value = snap
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        listenerRegistration?.remove()
        listenerRegistration = null
    }
}
