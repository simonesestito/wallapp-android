/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.utils

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun Query.toSuspendQuery(): List<DocumentSnapshot> = suspendCoroutine { cont ->
    this.get()
            .addOnSuccessListener { result -> cont.resume(result.documents) }
            .addOnFailureListener { e -> cont.resumeWithException(e) }
}

suspend fun DocumentReference.toSuspendDocument(): DocumentSnapshot = suspendCoroutine { cont ->
    this.get()
            .addOnSuccessListener { result -> cont.resume(result) }
            .addOnFailureListener { e -> cont.resumeWithException(e) }
}