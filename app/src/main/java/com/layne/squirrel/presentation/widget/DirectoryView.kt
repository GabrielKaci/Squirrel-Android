package com.layne.squirrel.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.core.view.setMargins
import com.google.android.material.card.MaterialCardView
import com.layne.squirrel.R
import com.layne.squirrel.core.domain.Directory
import kotlinx.android.synthetic.main.view_directory.view.*

class DirectoryView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

	companion object {
		private const val MARGIN = 5
		private const val RADIUS = 6f
		private const val ELEVATION = 6f
	}

	private var onHandlerTouchListener: ((MotionEvent) -> Unit)? = null

	private inline var title: String
		get() = textViewTitle.text.toString()
		set(value) {
			textViewTitle.text = value
		}

	init {
		LayoutInflater.from(context)?.inflate(R.layout.view_directory, this)
		val scale = context.resources.displayMetrics.density
		val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
			setMargins((MARGIN * scale).toInt())
		}
		layoutParams = params
		radius = RADIUS * scale
		elevation = ELEVATION * scale
		imageViewHandler.setOnTouchListener { _, event ->
			(onHandlerTouchListener != null).also { onHandlerTouchListener?.invoke(event) }
		}
	}

	fun setOnHandlerTouchListener(l: (MotionEvent) -> Unit) {
		onHandlerTouchListener = l
	}

	fun bindData(directory: Directory) {
		title = directory.title
	}
}