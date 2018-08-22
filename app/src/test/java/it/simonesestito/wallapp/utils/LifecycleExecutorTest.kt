/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.utils

import androidx.appcompat.app.AppCompatActivity
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class LifecycleExecutorTest {
    private lateinit var activityController: ActivityController<AppCompatActivity>
    private lateinit var countDownLatch: CountDownLatch

    @Before
    fun init() {
        activityController = Robolectric.buildActivity(AppCompatActivity::class.java).create()
        countDownLatch = CountDownLatch(1)
    }

    @Test
    fun testCreated() {
        activityController.get().executeOnReady { countDownLatch.countDown() }
        assertEquals(1L, countDownLatch.count)
    }

    @Test
    fun testStarted() {
        activityController.start().get().executeOnReady { countDownLatch.countDown() }
        assertEquals(1L, countDownLatch.count)
    }

    @Test
    fun testResumed() {
        activityController.resume().get().executeOnReady { countDownLatch.countDown() }
        assertEquals(0L, countDownLatch.count)
    }

    @Test
    fun testPaused() {
        activityController.pause().get().executeOnReady { countDownLatch.countDown() }
        assertEquals(1L, countDownLatch.count)
    }

    @Test
    fun testStopped() {
        activityController.stop().get().executeOnReady { countDownLatch.countDown() }
        assertEquals(1L, countDownLatch.count)
    }

    @Test
    fun testDestroyed() {
        activityController.destroy()
        activityController.get().executeOnReady { countDownLatch.countDown() }
        assertEquals(1L, countDownLatch.count)
    }


}