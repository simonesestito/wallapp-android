/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp

/**
 * Interface used for objects with ID of any type
 */
interface Identifiable<ID> {
    val id: ID
}