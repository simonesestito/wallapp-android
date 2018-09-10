/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.utils

import kotlin.reflect.KProperty

/**
 * Lazy Kotlin Delegate, with a reference to the receiver object
 */
class ReferencedLazy<T, R>(private inline val initializer: T.() -> R) {
    operator fun getValue(thisRef: T, property: KProperty<*>) = thisRef.initializer()
}

fun <T, R> thisLazy(initializer: T.() -> R) = ReferencedLazy(initializer)