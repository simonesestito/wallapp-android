package it.simonesestito.wallapp.persistence.repository

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import it.simonesestito.wallapp.FIRESTORE_CATEGORIES
import it.simonesestito.wallapp.FIRESTORE_WALLPAPERS
import it.simonesestito.wallapp.livedata.FirestoreLiveCollection
import it.simonesestito.wallapp.mapList
import it.simonesestito.wallapp.model.Wallpaper

object WallpaperRepository {
    fun getWallpapersByCategoryId(categoryId: String): LiveData<List<Wallpaper>> {
        val ref = FirebaseFirestore.getInstance()
                .collection("$FIRESTORE_CATEGORIES/$categoryId/$FIRESTORE_WALLPAPERS")

        return FirestoreLiveCollection(ref).mapList { Wallpaper(it.id, categoryId) }
    }
}