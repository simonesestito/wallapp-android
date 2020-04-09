/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.storage

import java.io.File
import java.io.FileOutputStream

/**
 * A [FileOutputStream] which publicly exposes a reference to the target [File]
 */
class NamedFileOutputStream(val file: File) : FileOutputStream(file)

fun File.namedOutputStream() = NamedFileOutputStream(this)