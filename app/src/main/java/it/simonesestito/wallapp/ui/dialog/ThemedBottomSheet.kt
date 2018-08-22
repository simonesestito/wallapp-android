/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.ui.dialog

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.simonesestito.wallapp.R


open class ThemedBottomSheet : BottomSheetDialogFragment() {
    override fun getTheme() = R.style.AppTheme_BottomSheet_Dialog
}