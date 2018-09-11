/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.ui.fragment

import com.github.lion4ik.arch.sharedelements.HasSharedElements

interface SharedElementsStart : HasSharedElements {
  override fun hasReorderingAllowed() = false
}
