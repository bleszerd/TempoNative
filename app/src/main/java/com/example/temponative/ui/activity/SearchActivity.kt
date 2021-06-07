package com.example.temponative.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.temponative.R


class SearchActivity : AppCompatActivity() {
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
            val resultIntent = Intent()
            resultIntent.putExtra("searchExtra", searchInput.text)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }


}