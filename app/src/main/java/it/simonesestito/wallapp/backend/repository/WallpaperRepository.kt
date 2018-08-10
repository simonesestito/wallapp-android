package it.simonesestito.wallapp.backend.repository

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import it.simonesestito.wallapp.FIRESTORE_CATEGORIES
import it.simonesestito.wallapp.FIRESTORE_WALLPAPERS
import it.simonesestito.wallapp.KEY_CREATION_DATE
import it.simonesestito.wallapp.KEY_PUBLISHED
import it.simonesestito.wallapp.annotations.WallpaperFormat
import it.simonesestito.wallapp.arch.livedata.FirestoreLiveCollection
import it.simonesestito.wallapp.arch.livedata.FirestoreLiveDocument
import it.simonesestito.wallapp.backend.model.Wallpaper
import it.simonesestito.wallapp.utils.map
import it.simonesestito.wallapp.utils.mapList
import java.io.File

object WallpaperRepository {

    /**
     * Get all the wallpapers in a given category
     * @param categoryId
     * @return Wallpapers list wrapped in a LiveData
     */
    fun getWallpapersByCategoryId(categoryId: String): LiveData<List<Wallpaper>> {
        val ref = FirebaseFirestore.getInstance()
                .collection("$FIRESTORE_CATEGORIES/$categoryId/$FIRESTORE_WALLPAPERS")
                .whereEqualTo(KEY_PUBLISHED, true)
                .orderBy(KEY_CREATION_DATE, Query.Direction.DESCENDING)

        return FirestoreLiveCollection(ref).mapList {
            Wallpaper(it.id, categoryId)
        }
    }

    /**
     * Get the wallpaper document from Firestore
     * @param categoryId Wallpaper's category ID
     * @param wallpaperId
     * @return LiveData of the Firestore document
     */
    fun getWallpaper(categoryId: String, wallpaperId: String): LiveData<Wallpaper> {
        val ref = FirebaseFirestore.getInstance()
                .document("$FIRESTORE_CATEGORIES/$categoryId/$FIRESTORE_WALLPAPERS/$wallpaperId")

        return FirestoreLiveDocument(ref).map {
            Wallpaper(it.id, categoryId)
        }
    }

    /**
     * Download the wallpaper to a [File]
     * @param wallpaper Target wallpaper
     * @param format Wallpaper format to download
     * @param destination Destination file where to download the wallpaper
     * @return Firebase file download task
     */
    fun downloadWallpaper(wallpaper: Wallpaper, @WallpaperFormat format: String, destination: File) =
            FirebaseStorage.getInstance()
                    .getReference(wallpaper.getStorageFilePath(format))
                    .getFile(destination)
}
