package it.simonesestito.wallapp.dagger.module

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.Reusable


@Module
class FirebaseModule {
    @Provides
    @Reusable
    fun firestore() = FirebaseFirestore.getInstance()

    @Provides
    @Reusable
    fun storage() = FirebaseStorage.getInstance()
}