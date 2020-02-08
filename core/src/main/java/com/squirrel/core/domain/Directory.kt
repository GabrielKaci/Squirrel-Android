package com.squirrel.core.domain

data class Directory(
	var title: String,
	var accounts: MutableList<Account> = mutableListOf()
)