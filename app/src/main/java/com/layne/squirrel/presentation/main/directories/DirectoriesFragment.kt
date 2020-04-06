package com.layne.squirrel.presentation.main.directories

import android.annotation.TargetApi
import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.view.*
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import com.layne.squirrel.R
import com.layne.squirrel.R.string.key_deleted
import com.layne.squirrel.framework.show
import com.layne.squirrel.presentation.ItemTouchHelperCallback
import com.layne.squirrel.presentation.main.MainActivity
import com.layne.squirrel.presentation.main.MainViewModel
import com.layne.squirrel.presentation.main.directories.dialog.*
import kotlinx.android.synthetic.main.fragment_home.*

class DirectoriesFragment : Fragment() {

	private var model: MainViewModel? = null
	private var viewAdapter: DirectoryAdapter? = null
	private var touchHelper: ItemTouchHelper? = null

	override fun onCreateView(i: LayoutInflater, c: ViewGroup?, b: Bundle?): View? =
		i.inflate(R.layout.fragment_home, c, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setHasOptionsMenu(true)

		model = activity?.run {
			ViewModelProvider(this).get(MainViewModel::class.java)
		}
		(activity as MainActivity).appBar?.performShow()

		viewAdapter = DirectoryAdapter(
			mutableListOf()
		).apply {
			setOnItemClickListener {
				val action =
					DirectoriesFragmentDirections.openDirectory(it)
				NavHostFragment
					.findNavController(this@DirectoriesFragment)
					.navigate(action)
			}
			setOnContextMenuitemClickListener { item, position, directory ->
				when (item) {
					0 -> openDialogRenameDir(position, directory.title)
					1 -> openDialogDeleteDir(position)
				}
			}
			setOnStartDragListener {
				touchHelper?.startDrag(it)
			}
			setOnItemMoveListener { from, to ->
				model?.swapDirectories(from, to)
			}
			setOnItemDismissListener { i ->
				model?.data?.value?.get(i)?.let { dir ->
					Snackbar.make(container, getString(key_deleted, dir.title), LENGTH_LONG)
						.setAction(R.string.cancel) {
							model?.remitDirectory(dir, i)
							notifyItemInserted(i)
							notifyItemRangeChanged(i, itemCount)
						}
						.show()
				}
				model?.deleteDirectory(i)
			}

			touchHelper = ItemTouchHelper(ItemTouchHelperCallback(this))
			touchHelper?.attachToRecyclerView(recyclerView)
		}

		activity?.run {
			model?.data?.observe(this, Observer {
				viewAdapter?.updateData(it)
			})
		}

		recyclerView.apply {
			setHasFixedSize(true)
			layoutManager = LinearLayoutManager(context)
			adapter = viewAdapter
		}

		(activity as MainActivity).fab?.setOnClickListener {
			openDialogNewDir()
		}
	}

	override fun onResume() {
		super.onResume()

		viewAdapter?.updateData(model?.data?.value ?: mutableListOf())
		viewAdapter?.notifyDataSetChanged()
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.home, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {

		when (item.itemId) {
			R.id.biometrics -> {
				model?.checkPasswordExists(model?.uri?.toString() ?: "") {
					if (it)
						activity?.run { CancelBiometricsDialog().show("biometrics", this) }
					else
						showBiometricPrompt()
				}
			}
			R.id.changePassword -> {
				activity?.run {
					ChangePasswordDialog.getInstance(model?.password ?: "")
						.addPositiveButtonClick { model?.changePassword(it) }
						.show("changePassword", this)
				}
			}
		}

		return true
	}

	@TargetApi(Build.VERSION_CODES.P)
	private fun showBiometricPrompt() {
		activity?.let {
			if (FingerprintManagerCompat.from(it).hasEnrolledFingerprints()) {
				BiometricPrompt.Builder(context)
					.setTitle(getText(R.string.biometric_title))
					.setSubtitle(getText(R.string.biometric_subtitle))
					.setDescription(getText((R.string.biometric_activation_description)))
					.setNegativeButton(getText(R.string.biometric_cancel),
						it.mainExecutor,
						DialogInterface.OnClickListener { _, _ -> })
					.build()
					.authenticate(CancellationSignal(),
						it.mainExecutor,
						object : BiometricPrompt.AuthenticationCallback() {
							override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
								super.onAuthenticationSucceeded(result)
								model?.savePassword(
									model?.uri?.toString() ?: "",
									model?.password ?: ""
								)
							}
						})
			} else
				Snackbar.make(container, getText(R.string.no_fingerprint_detected), LENGTH_LONG)
					.show()
		}
	}

	private fun openDialogNewDir() {
		activity?.run {
			CreateDirectoryDialog()
				.addPositiveButtonClick { name ->
					model?.addDirectory(name)
					model?.data?.value?.let {
						viewAdapter?.updateData(it)
						viewAdapter?.notifyItemInserted(it.size - 1)
					}
				}.show("createDirectory", this)
		}
	}

	private fun openDialogRenameDir(position: Int, name: String) {
		activity?.run {
			RenameDirectoryDialog(name)
				.addPositiveButtonClick { newName ->
					model?.data?.value?.let {
						it[position].title = newName
						viewAdapter?.updateData(it)
						viewAdapter?.notifyDataSetChanged()
					}
				}.show("renameDirectory", this)
		}
	}

	private fun openDialogDeleteDir(position: Int) {
		activity?.run {
			DeleteDirectoryDialog()
				.addPositiveButtonClick {
					model?.deleteDirectory(position)
				}.show("deleteDirectory", this)
		}
	}
}
