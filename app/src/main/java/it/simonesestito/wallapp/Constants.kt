package it.simonesestito.wallapp

//region Firestore
const val FIRESTORE_CATEGORIES = "categories"
const val FIRESTORE_WALLPAPERS = "wallpapers"
//endregion

//region Firebase Storage
const val STORAGE_CATEGORIES = "categories"
const val STORAGE_WALLPAPERS = "wallpapers"
//endregion

//region JSON mapping keys
const val KEY_PUBLISHED = "published"
const val KEY_CREATION_DATE = "creationDate"
const val KEY_DISPLAY_NAME = "displayName"
const val KEY_DESCRIPTION = "description"
const val KEY_COUNT = "count"
//endregion

//region Request codes (startActivityForResult)
const val REQUEST_PREVIEW_OVERLAY_PERMISSION = 1
const val REQUEST_READ_STORAGE_PERMISSION = 2
//endregion

//region Intent / Bundle constants
const val ACTION_PREVIEW_RESULT = "preview_window_result"

const val EXTRA_WALLPAPER_SETUP_PARCELABLE = "wallpaper_extra"
const val EXTRA_WALLPAPER_PREVIEW_WINDOW_PARCELABLE = "wallpaper_preview"
const val EXTRA_WALLPAPER_PREVIEW_RESULT = "preview_result_extra"

const val RESULT_WALLPAPER_CONFIRMED = 1
const val RESULT_WALLPAPER_CANCELED = 2
//endregion

const val BACKUP_WALLPAPER_FILENAME = "wall-backup.png"

const val BOTTOMSHEET_FADE_ANIMATION_DURATION = 120L
const val BOTTOMSHEET_AUTO_DISMISS_DELAY = 1200L
