/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.simonesestito.wallapp.R

/**
 * Show a dialog with an explanation for the required permissions
 * Communicate the result back to the Fragment via [onActivityResult]
 */
class PermissionsDialog : DialogFragment() {
    companion object {
        private const val ARG_MESSAGE = "message"

        fun createDialog(message: String) = PermissionsDialog().apply {
            arguments = bundleOf(ARG_MESSAGE to message)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        return MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(R.string.permission_request_dialog_title)
            setMessage(requireArguments().getString(ARG_MESSAGE)!!)
            setNegativeButton(R.string.permission_request_negative_button) { _, _ ->
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, null)
            }
            setPositiveButton(R.string.permission_request_positive_button) { _, _ ->
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
            }
        }.create()
    }
}