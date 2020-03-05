/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.enums

import androidx.annotation.StringDef

@Retention(AnnotationRetention.SOURCE)
@StringDef(
        CATEGORY_GROUP_ORIGINAL,
        CATEGORY_GROUP_COMMUNITY
)
annotation class CategoryGroup

const val CATEGORY_GROUP_ORIGINAL = "original"
const val CATEGORY_GROUP_COMMUNITY = "community"