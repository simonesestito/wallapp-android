package it.simonesestito.wallapp.model

import it.simonesestito.wallapp.STORAGE_CATEGORIES
import it.simonesestito.wallapp.annotations.FORMAT_COVER

data class Category(
        val id: String,
        val displayName: String,
        val description: String,
        val wallpapersCount: Long
) {
    val coverUrl: String
        get() = "$STORAGE_CATEGORIES/$id/$FORMAT_COVER.jpg"

    constructor(id: String, map: Map<String, Any>) : this(
            id = id,
            displayName = map["displayName"].toString(),
            description = map["description"].toString(),
            wallpapersCount = map["count"] as Long)
}