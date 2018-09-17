/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.google.android.vending.licensing;

/**
 * Indicates that an error occurred while validating the integrity of data managed by an
 * {@link Obfuscator}.}
 */
public class ValidationException extends Exception {
    private static final long serialVersionUID = 1L;

    public ValidationException() {
        super();
    }

    public ValidationException(String s) {
        super(s);
    }
}
