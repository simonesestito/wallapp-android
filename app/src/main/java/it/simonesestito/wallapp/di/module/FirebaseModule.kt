package it.simonesestito.wallapp.di.module

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class FirebaseModule {
    @Provides
    @Singleton
    fun firestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun storage() = FirebaseStorage.getInstance()
}