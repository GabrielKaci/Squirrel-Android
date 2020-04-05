package com.layne.squirrel.presentation.main.directories.dialog

import android.app.Dialog
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.layne.squirrel.R
import com.layne.squirrel.framework.PasswordUseCases
import com.layne.squirrel.framework.Squirrel
import com.layne.squirrel.presentation.main.MainViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class CancelBiometricsDialog : DialogFragment() {

	private var model: MainViewModel? = null

	@Inject
	lateinit var passwordUseCases: PasswordUseCases

	init {
		Squirrel.dagger.inject(this)
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		model = activity?.run {
			ViewModelProvider(this).get(MainViewModel::class.java)
		}

		val nightModeFlags = context?.let {
			it.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
		}
		val icon = when (nightModeFlags) {
			Configuration.UI_MODE_NIGHT_YES -> R.drawable.ic_fingerprint_white
			else                            -> R.drawable.ic_fingerprint_black
		}

		return AlertDialog.Builder(context ?: throw NullPointerException())
			.setIcon(icon)
			.setTitle(getText(R.string.biometric_title))
			.setMessage(getString(R.string.cancel_biometrics_dialog_message))
			.setPositiveButton(R.string.cancel_biometrics_dialog_yes) { _, _ ->
				GlobalScope.launch {
					passwordUseCases.deletePassword(model?.uri?.toString() ?: "")
				}
			}
			.setNegativeButton(R.string.cancel_biometrics_dialog_no, null)
			.create()
	}
}