package com.example.delicias.ui.notification

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.example.delicias.R
import com.example.delicias.databinding.ActivityNotificationDetailBinding
import com.example.delicias.util.Constants

class NotificationDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityNotificationDetailBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_detail)

        val intent = intent
        val summary = intent.getStringExtra(Constants.NOTIFICATION_SUMMARY)
        val title = intent.getStringExtra(Constants.NOTIFICATION_TITLE)
        val date = intent.getStringExtra(Constants.NOTIFICATION_DATE)
        val body = intent.getStringExtra(Constants.NOTIFICATION_BODY)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_detail)

        val backButtonTitle: View = binding.backButtonTitle

        val ivBackButton = (backButtonTitle as ConstraintLayout).getViewById(R.id.iv_back_button) as ImageView

        ivBackButton.setOnClickListener {
            onBackPressed()
        }

        val tvTitle = backButtonTitle.getViewById(R.id.tv_toolbar) as TextView

        tvTitle.text = summary
        tvTitle.setTextColor(resources.getColor(R.color.gray, null))

        binding.tvTitle.text = title
        binding.tvDate.text = date
        binding.tvBody.text = body
    }
}