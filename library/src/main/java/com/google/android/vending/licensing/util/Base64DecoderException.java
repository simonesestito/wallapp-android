/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.google.android.vending.licensing.util;

/**
 * Exception thrown when encountering an invalid Base64 input character.
 *
 * @author nelson
 */
public class Base64DecoderException extends Exception {
    private static final long serialVersionUID = 1L;

    public Base64DecoderException() {
        super();
    }

    public Base64DecoderException(String s) {
        super(s);
    }
}
