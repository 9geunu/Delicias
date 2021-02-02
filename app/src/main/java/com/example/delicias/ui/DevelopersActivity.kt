package com.example.delicias.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.example.delicias.R
import com.example.delicias.databinding.ActivityDevelopersBinding
import kotlinx.android.synthetic.main.activity_developers.*

class DevelopersActivity : AppCompatActivity() {
    lateinit var binding: ActivityDevelopersBinding
    private lateinit var ivBackButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_developers)

        val backButtonTitle: View = binding.backButtonTitle

        ivBackButton = (backButtonTitle as ConstraintLayout).getViewById(R.id.iv_back_button) as ImageView

        ivBackButton.setOnClickListener {
            onBackPressed()
        }
    }
}