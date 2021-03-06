package com.layne.squirrel.presentation.main.directories.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.layne.squirrel.R
import com.layne.squirrel.framework.getValue
import com.layne.squirrel.framework.withArgs

class ChangePasswordDialog : DialogFragment() {

	companion object {
		const val ARG_PASSWORD = "password"

		fun build(password: String, block: (String) -> Unit) = ChangePasswordDialog()
			.withArgs<ChangePasswordDialog>(bundleOf(ARG_PASSWORD to password))
			.setOnPositiveButtonClickListener(block)
	}

	private var positiveButtonClickListener: (String) -> Unit = {}

	fun setOnPositiveButtonClickListener(l: (String) -> Unit): ChangePasswordDialog {
		positiveButtonClickListener = l
		return this
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val dialogView = View.inflate(context, R.layout.dialog_change_password, null)

		val editTextActualPassword =
			dialogView?.findViewById(R.id.editTextActualPassword) as EditText
		val editTextNewPassword = dialogView.findViewById(R.id.editTextNewPassword) as EditText
		val editTextConfirmation =
			dialogView.findViewById(R.id.editTextNewPasswordConfirmation) as EditText

		return AlertDialog.Builder(context ?: throw NullPointerException())
			.setTitle(getString(R.string.new_password))
			.setView(dialogView)
			.setIcon(R.drawable.ic_folder)
			.setPositiveButton(getString(R.string.validate)) { _, _ ->
				val password = requireArguments().getString(ARG_PASSWORD)
				val actual = editTextActualPassword.getValue()
				val newPassword = editTextNewPassword.getValue()
				val confirmation = editTextConfirmation.getValue()

				when {
					actual != password          ->
						editTextActualPassword.error = getString(R.string.wrong_password)
					newPassword != confirmation ->
						editTextConfirmation.error = getString(R.string.password_does_not_match)
					else                        ->
						positiveButtonClickListener(newPassword)
				}
			}
			.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
			.create()
	}
}