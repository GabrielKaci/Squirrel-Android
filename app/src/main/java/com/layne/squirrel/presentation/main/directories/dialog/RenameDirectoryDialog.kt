package com.layne.squirrel.presentation.main.directories.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.layne.squirrel.R
import com.layne.squirrel.framework.getValue

class RenameDirectoryDialog(private val name: String) : DialogFragment() {

	companion object {
		fun build(name: String, block: (String) -> Unit) = RenameDirectoryDialog(name)
			.setOnPositiveButtonClickListener(block)
	}

	private var positiveButtonClickListener: (String) -> Unit = {}

	fun setOnPositiveButtonClickListener(l: (String) -> Unit): RenameDirectoryDialog {
		positiveButtonClickListener = l
		return this
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val dialogView = View.inflate(context, R.layout.dialog_newdir, null)
		val editText = dialogView.findViewById(R.id.editTextDirName) as EditText
		editText.setText(name)

		return AlertDialog.Builder(context ?: throw NullPointerException())
			.setTitle(getString(R.string.rename_dir))
			.setView(dialogView)
			.setIcon(R.drawable.ic_folder)
			.setPositiveButton(getString(R.string.validate)) { _, _ ->
				positiveButtonClickListener(editText.getValue())
			}
			.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
			.create()
	}
}