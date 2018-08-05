package it.simonesestito.wallapp.data.model

import it.simonesestito.wallapp.KEY_COUNT
import it.simonesestito.wallapp.KEY_DESCRIPTION
import it.simonesestito.wallapp.KEY_DISPLAY_NAME
import it.simonesestito.wallapp.STORAGE_CATEGORIES
import it.simonesestito.wallapp.annotations.FORMAT_COVER
import it.simonesestito.wallapp.utils.Identifiable

data class Category(
        override val id: String,
        val displayName: String,
        val description: String,
        val wallpapersCount: Long
) : Identifiable<String> {
    val coverUrl: String
        get() = "$STORAGE_CATEGORIES/$id/$FORMAT_COVER"

    constructor(id: String, map: Map<String, Any>) : this(
            id = id,
            displayName = map[KEY_DISPLAY_NAME].toString(),
            description = map[KEY_DESCRIPTION].toString(),
            wallpapersCount = map[KEY_COUNT] as Long)
}