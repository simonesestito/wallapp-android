/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.di.annotation

import javax.inject.Named

@Named("main_thread")
@Retention(AnnotationRetention.RUNTIME)
annotation class MainHandler