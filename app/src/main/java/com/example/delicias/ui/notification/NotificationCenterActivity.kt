package com.example.delicias.ui.notification

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.delicias.R
import com.example.delicias.databinding.ActivityNotificationCenterBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class NotificationCenterActivity : AppCompatActivity() {
    lateinit var binding: ActivityNotificationCenterBinding
    private lateinit var ivBackButton: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var notificationFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_center)

        val backButtonTitle: View = binding.backButtonTitle

        ivBackButton = (backButtonTitle as ConstraintLayout).getViewById(R.id.iv_back_button) as ImageView
        tvTitle = backButtonTitle.getViewById(R.id.tv_toolbar) as TextView

        tvTitle.text = "알림센터"

        ivBackButton.setOnClickListener {
            onBackPressed()
        }

        val tabs = binding.tabs

        tabs.addTab(tabs.newTab().setText("알림"))
        tabs.addTab(tabs.newTab().setText("공지사항"))

        notificationFragment = NotificationFragment()

        tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                var selected: Fragment? = null

                if (position == 0) selected = notificationFragment
                else if (position == 1) selected = notificationFragment

                if (selected != null) {
                    supportFragmentManager.beginTransaction().replace(R.id.container, selected).commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}