package com.layne.squirrel.presentation.main

interface ItemTouchHelperAdapter {
	fun onItemMove(from: Int, to: Int)

	fun onItemDismiss(position: Int)
}