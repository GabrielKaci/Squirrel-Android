package com.squirrel.presentation.main

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squirrel.R
import com.squirrel.core.domain.Directory

class MainActivity : AppCompatActivity() {

	private var model: MainViewModel? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		setSupportActionBar(findViewById(R.id.toolbar))
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setDisplayShowHomeEnabled(true)

		model = ViewModelProviders.of(this).get(MainViewModel::class.java)

		val json = intent.getStringExtra("json") ?: "{}"

		model?.data?.value =
			Gson().fromJson(json, object : TypeToken<ArrayList<Directory?>?>() {}.type)

		model?.data?.observe(this, Observer {
			model?.saveKeys()
		})
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			onBackPressed()
			return true
		}

		return super.onOptionsItemSelected(item)
	}
}