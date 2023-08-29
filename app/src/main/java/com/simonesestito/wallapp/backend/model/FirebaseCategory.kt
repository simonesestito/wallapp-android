/*
 * Copyright 2020 Simone Sestito
 * This file is part of WallApp.
 *
 * WallApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WallApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WallApp.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.simonesestito.wallapp.backend.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import com.simonesestito.wallapp.*
import com.simonesestito.wallapp.enums.CATEGORY_GROUP_ORIGINAL
import com.simonesestito.wallapp.enums.CategoryGroup
import com.simonesestito.wallapp.enums.FORMAT_COVER
import com.simonesestito.wallapp.utils.LocalizedString
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseCategory(
    override val id: String,
    val displayName: LocalizedString,
    val description: LocalizedString,
    @CategoryGroup val group: String,
    val wallpapersCount: Int
) : Identifiable<String>, Parcelable {
    @IgnoredOnParcel
    val previewImageUrl = "$SCALEWAY_BUCKET_URL/$STORAGE_CATEGORIES/$id/$FORMAT_COVER"

    @Suppress("UNCHECKED_CAST")
    constructor(snap: DocumentSnapshot) : this(
        id = snap.id,
        displayName = snap[KEY_DISPLAY_NAME] as LocalizedString,
        description = snap[KEY_DESCRIPTION] as LocalizedString,
        group = snap[KEY_CATEGORY_GROUP] as String? ?: CATEGORY_GROUP_ORIGINAL,
        wallpapersCount = (snap[KEY_CATEGORY_ITEMS_COUNT] as Long?)?.toInt() ?: 0
    )
}