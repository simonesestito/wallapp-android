/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp


const val LICENSING_PUBLIC_RSA_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhjZOmmhh5yvyyrxI8+h/R4Arrjb366rf/3iZS9iv85IttKwQPfHlFGbTU/diWFf+8Pl5nNG3i5uFsgwy2flnLg5eaGxqHVckd2GJzIiTyepFKjxhXNOVY2wcwWH5vGMkYDJo86TZ8gjA5S38k8ybU1LyDhLVFNBldmsmB6ZXfs5Q3/k2wia1hux8mzqXJRR61VamlIMMJBavdUhW10DT5AjlVwT5CplDrcd78hp0+eTzkVrKcqz+TNdqyePjpohXec3d6VAsAmyaHAOdhhb5GCItIIatxuWqIpyzMhDI9HS6ttcfweb1lbtw7GaMkT1IIOqnB1yTeGkboApyqigR6wIDAQAB"

const val FIRESTORE_CATEGORIES = "categories"
const val FIRESTORE_WALLPAPERS = "wallpapers"
const val BASE_WEBAPP_URL = "https://wallapp.app"
const val STORAGE_CATEGORIES = "categories"
const val STORAGE_WALLPAPERS = "wallpapers"

const val KEY_PUBLISHED = "published"
const val KEY_CREATION_DATE = "creationDate"
const val KEY_DISPLAY_NAME = "displayName"
const val KEY_DESCRIPTION = "description"
const val KEY_COUNT = "count"
const val FIRESTORE_LOCALIZED_DEFAULT = "default"

const val CHROME_PACKAGE_NAME = "com.android.chrome"

const val SHARED_PREFERENCES_FILENAME = "wallapp_prefs"
const val PREFS_IS_FIRST_LAUNCH_KEY = "first_launch"

const val REQUEST_PREVIEW_OVERLAY_PERMISSION = 1
const val REQUEST_READ_STORAGE_PERMISSION = 2

const val ACTION_PREVIEW_RESULT = "preview_window_result"

const val EXTRA_WALLPAPER_SETUP_PARCELABLE = "wallpaper_extra"
const val EXTRA_WALLPAPER_PREVIEW_WINDOW_PARCELABLE = "wallpaper_preview"
const val EXTRA_WALLPAPER_PREVIEW_RESULT = "preview_result_extra"

const val RESULT_WALLPAPER_CONFIRMED = 1
const val RESULT_WALLPAPER_CANCELED = 2

const val BACKUP_WALLPAPER_FILENAME = "wall-backup.png"

const val BOTTOMSHEET_FADE_ANIMATION_DURATION = 120L
const val BOTTOMSHEET_AUTO_DISMISS_DELAY = 1200L

const val MAX_CACHED_PALETTE_SIZE = 15

const val IO_EXECUTOR_MAX_THREADS = 2

const val AUTHOR_1_PORTFOLIO_WEBSITE = "https://sestito.tk"
const val AUTHOR_1_MAIL = "simone.dev.help@gmail.com"

const val AUTHOR_2_PORTFOLIO_WEBSITE = "https://www.paologiubilato.com"
const val AUTHOR_2_MAIL = "giubilato95@gmail.com"

const val GOOGLE_PLAY_LINK = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
const val TWITTER_LINK = "https://twitter.com/wallapp_info"

