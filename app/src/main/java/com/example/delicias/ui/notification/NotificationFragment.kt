package com.example.delicias.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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

        adapter = NotificationAdapter(requireContext())

        binding.rvNotifications.layoutManager = LinearLayoutManager(this.requireContext())
        binding.rvNotifications.itemAnimator = DefaultItemAnimator()
        binding.rvNotifications.adapter = adapter

        adapter.submitList(listOf(
            Notification(1,"학생회관 식당 운영 중단 안내", "타이틀입니다. 타이틀입니다.","2020.00.00", "슈퍼 푸드의 종류와 범위는 명확하게 정해져 있지 않지만, 열량과 지방함량이 낮고 비타민, 무기질, 항산화 영양소, 섬유소를 포함한 생리활성물질인 \"파이토케미컬\"을 함유하고 있는 식품들을 의미하는데요,\n" +
                    "\n" +
                    "우리가 흔히 알고 있는 세계 10대 슈퍼 푸드는 귀리, 블루베리, 녹차, 마늘, 연어, 브로콜리, 아몬드, 적포도주, 시금치, 토마토로,미국의 <타임>지에서 선정, 발표한 10가지 식품입니다."),
            Notification(1,"학생회관 식당 운영 중단 안내", "타이틀입니다. 타이틀입니다.","2020.00.00", "슈퍼 푸드의 종류와 범위는 명확하게 정해져 있지 않지만, 열량과 지방함량이 낮고 비타민, 무기질, 항산화 영양소, 섬유소를 포함한 생리활성물질인 \"파이토케미컬\"을 함유하고 있는 식품들을 의미하는데요,\n" +
                    "\n" +
                    "우리가 흔히 알고 있는 세계 10대 슈퍼 푸드는 귀리, 블루베리, 녹차, 마늘, 연어, 브로콜리, 아몬드, 적포도주, 시금치, 토마토로,미국의 <타임>지에서 선정, 발표한 10가지 식품입니다."),
            Notification(1,"학생회관 식당 운영 중단 안내", "타이틀입니다. 타이틀입니다.","2020.00.00", "슈퍼 푸드의 종류와 범위는 명확하게 정해져 있지 않지만, 열량과 지방함량이 낮고 비타민, 무기질, 항산화 영양소, 섬유소를 포함한 생리활성물질인 \"파이토케미컬\"을 함유하고 있는 식품들을 의미하는데요,\n" +
                    "\n" +
                    "우리가 흔히 알고 있는 세계 10대 슈퍼 푸드는 귀리, 블루베리, 녹차, 마늘, 연어, 브로콜리, 아몬드, 적포도주, 시금치, 토마토로,미국의 <타임>지에서 선정, 발표한 10가지 식품입니다."),
            Notification(1,"학생회관 식당 운영 중단 안내", "타이틀입니다. 타이틀입니다.","2020.00.00", "슈퍼 푸드의 종류와 범위는 명확하게 정해져 있지 않지만, 열량과 지방함량이 낮고 비타민, 무기질, 항산화 영양소, 섬유소를 포함한 생리활성물질인 \"파이토케미컬\"을 함유하고 있는 식품들을 의미하는데요,\n" +
                    "\n" +
                    "우리가 흔히 알고 있는 세계 10대 슈퍼 푸드는 귀리, 블루베리, 녹차, 마늘, 연어, 브로콜리, 아몬드, 적포도주, 시금치, 토마토로,미국의 <타임>지에서 선정, 발표한 10가지 식품입니다.")))

        return binding.root
    }
}