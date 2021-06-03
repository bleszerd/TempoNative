package com.example.temponative.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toolbar
import com.example.temponative.R
import com.example.temponative.dataholder.DataHolder

class SearchActivity : AppCompatActivity() {
    val dataHolder = DataHolder
    lateinit var backButton: ImageView
    lateinit var searchInput: EditText
    lateinit var searchButton: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        backButton = findViewById(R.id.back_button)
        searchInput = findViewById(R.id.search_edit_text)
        searchButton = findViewById(R.id.search_button_action)

        backButton.setOnClickListener {
            finish()
        }

        searchButton.setOnClickListener {
            dataHolder.citySearch = searchInput.text.toString()
            finish()
        }
    }


}