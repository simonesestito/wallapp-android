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

package com.simonesestito.wallapp

const val FIRESTORE_CATEGORIES = "categories"
const val FIRESTORE_WALLPAPERS = "wallpapers"
const val BASE_WEBAPP_URL = "https://wallapp.app"
const val STORAGE_CATEGORIES = "categories"
const val STORAGE_WALLPAPERS = "wallpapers"
const val ROOM_DATABASE_NAME = "wallapp-db"

const val KEY_PUBLISHED = "published"
const val KEY_CREATION_DATE = "creationDate"
const val KEY_DISPLAY_NAME = "displayName"
const val KEY_DESCRIPTION = "description"
const val KEY_CATEGORY_ITEMS_COUNT = "count"
const val KEY_CATEGORY_GROUP = "group"
const val KEY_WALL_AUTHOR_BIO = "authorBio"
const val KEY_WALL_AUTHOR_SOCIAL = "authorSocial"
const val KEY_WALL_AUTHOR_NAME = "authorName"
const val FIRESTORE_LOCALIZED_DEFAULT = "default"

const val SCALEWAY_BUCKET_URL = "https://wallapp-bucket.s3.fr-par.scw.cloud"

const val CHROME_PACKAGE_NAME = "com.android.chrome"

const val SHARED_PREFERENCES_FILENAME = "wallapp_prefs"
const val PREFS_IS_FIRST_LAUNCH_KEY = "first_launch"
const val PREFS_SINGLE_CATEGORY_LAYOUT_ROWS = "single_category_layout_rows"

const val REQUEST_PREVIEW_OVERLAY_PERMISSION = 1
const val REQUEST_READ_STORAGE_PERMISSION = 2
const val REQUEST_WRITE_STORAGE_PERMISSION = 3

const val ACTION_PREVIEW_RESULT = "preview_window_result"

const val PREVIEW_SERVICE_NOTIFICATION_ID = 1
const val PREVIEW_SERVICE_PENDING_INTENT_ID = 1
const val PREVIEW_SERVICE_NOTIFICATION_CHANNEL = "preview_service"

const val EXTRA_WALLPAPER_BOTTOMSHEET_PARCELABLE = "wallpaper_extra"
const val EXTRA_WALLPAPER_PREVIEW_WINDOW_PARCELABLE = "wallpaper_preview"
const val EXTRA_WALLPAPER_PREVIEW_RESULT = "preview_result_extra"

const val RESULT_WALLPAPER_CONFIRMED = 1
const val RESULT_WALLPAPER_CANCELED = 2

const val BACKUP_WALLPAPER_FILENAME = "wall-backup.png"

// Relative to /sdcard/Pictures, or the given folder by Environment
const val PICTURES_DOWNLOAD_SUBDIR = "WallApp"

const val BOTTOMSHEET_FADE_ANIMATION_DURATION = 120L
const val BOTTOMSHEET_AUTO_DISMISS_DELAY = 1200L

const val MAX_CACHED_PALETTE_SIZE = 100

const val IO_EXECUTOR_MAX_THREADS = 1

const val AUTHOR_1_PORTFOLIO_WEBSITE = "https://simonesestito.com"
const val AUTHOR_1_MAIL = "simone@simonesestito.com"

const val AUTHOR_2_PORTFOLIO_WEBSITE = "https://www.paologiubilato.com"
const val AUTHOR_2_MAIL = "giubilato95@gmail.com"

const val GOOGLE_PLAY_LINK = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
