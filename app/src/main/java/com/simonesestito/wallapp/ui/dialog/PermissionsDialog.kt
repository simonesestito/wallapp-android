/*
 * Copyright 2020 Simone Sestito
 * This file is part of WallApp.
 *
 * WallApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WallApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WallApp.  If not, see <http://www.gnu.org/licenses/>.
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