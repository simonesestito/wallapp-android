package it.simonesestito.wallapp.data.repository

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import it.simonesestito.wallapp.FIRESTORE_CATEGORIES
import it.simonesestito.wallapp.FIRESTORE_WALLPAPERS
import it.simonesestito.wallapp.data.model.Wallpaper
import it.simonesestito.wallapp.livedata.FirestoreLiveCollection
import it.simonesestito.wallapp.livedata.FirestoreLiveDocument
import it.simonesestito.wallapp.utils.map
import it.simonesestito.wallapp.utils.mapList

object WallpaperRepository {
    fun getWallpapersByCategoryId(categoryId: String): LiveData<List<Wallpaper>> {
        val ref = FirebaseFirestore.getInstance()
                .collection("$FIRESTORE_CATEGORIES/$categoryId/$FIRESTORE_WALLPAPERS")

        return FirestoreLiveCollection(ref).mapList {
            Wallpaper(it.id, categoryId)
        }
    }

    fun getWallpaper(categoryId: String, wallpaperId: String): LiveData<Wallpaper> {
        val ref = FirebaseFirestore.getInstance()
                .document("$FIRESTORE_CATEGORIES/$categoryId/$FIRESTORE_WALLPAPERS/$wallpaperId")

        return FirestoreLiveDocument(ref).map {
            Wallpaper(it.id, categoryId)
        }
    }
}
