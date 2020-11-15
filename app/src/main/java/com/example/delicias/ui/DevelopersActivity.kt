package com.example.delicias.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.delicias.R
import com.example.delicias.databinding.ActivityDevelopersBinding
import kotlinx.android.synthetic.main.activity_developers.*

class DevelopersActivity : AppCompatActivity() {
    lateinit var binding: ActivityDevelopersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developers)

        iv_back_button.setOnClickListener {
            onBackPressed()
        }
    }
}