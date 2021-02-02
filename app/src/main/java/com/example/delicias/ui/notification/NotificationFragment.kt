package com.example.delicias.ui.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.delicias.R
import com.example.delicias.databinding.FragmentNotificationBinding
import com.example.delicias.domain.Notification

class NotificationFragment : Fragment() {
    lateinit var binding: FragmentNotificationBinding
    lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)

        adapter = NotificationAdapter()

        binding.rvNotifications.layoutManager = LinearLayoutManager(this.requireContext())
        binding.rvNotifications.itemAnimator = DefaultItemAnimator()
        binding.rvNotifications.adapter = adapter

        adapter.submitList(listOf(
            Notification(1,"학생회관 식당 운영 중단 안내", "2020.00.00", ""),
            Notification(1,"학생회관 식당 운영 중단 안내", "2020.00.00", ""),
            Notification(1,"학생회관 식당 운영 중단 안내", "2020.00.00", ""),
            Notification(1,"학생회관 식당 운영 중단 안내", "2020.00.00", "")))

        return binding.root
    }
}