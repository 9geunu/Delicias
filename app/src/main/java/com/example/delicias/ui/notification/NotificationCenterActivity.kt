package com.example.delicias.ui.notification

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.delicias.R
import com.example.delicias.databinding.ActivityNotificationCenterBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator


class NotificationCenterActivity : AppCompatActivity() {
    lateinit var binding: ActivityNotificationCenterBinding
    private lateinit var ivBackButton: ImageView
    private lateinit var tvTitle: TextView
    private val tabLayoutTextArray = arrayOf("알림","공지사항")

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
        val viewPager = binding.pager

        viewPager.adapter = NotificationCenterFragmentAdapter(supportFragmentManager, lifecycle)

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = tabLayoutTextArray[position]
        }.attach()
    }

    inner class NotificationCenterFragmentAdapter(fm: FragmentManager, lc: Lifecycle): FragmentStateAdapter(fm, lc) {
        override fun getItemCount() = 2
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> NotificationFragment()
                1 -> NoticeFragment()
                else -> error("no such position: $position")
            }
        }
    }
}