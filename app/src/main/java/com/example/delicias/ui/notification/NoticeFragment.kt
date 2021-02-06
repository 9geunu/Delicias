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
import com.example.delicias.databinding.FragmentNoticeBinding
import com.example.delicias.domain.Notice

class NoticeFragment : Fragment() {
    lateinit var binding: FragmentNoticeBinding
    lateinit var adapter: NoticeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notice, container, false)

        adapter = NoticeAdapter()

        binding.rvNotices.layoutManager = LinearLayoutManager(this.requireContext())
        binding.rvNotices.itemAnimator = DefaultItemAnimator()
        binding.rvNotices.adapter = adapter

        adapter.submitList(listOf(
            Notice(1, "[공지] 서비스 점검 안내", "2020.00.00", "안녕하세요. 밥먹샤입니다.\n" +
                    "\n" +
                    "어쩌구 저쩌구 대충 칸만 잡아두겠습니다. \n" +
                    "어쩌구 저쩌구 대충 칸만 잡아두겠습니다.\n" +
                    "어쩌구 저쩌구 대충 칸만 잡아두겠습니다.어쩌구 저쩌구 대충 칸만 잡아두겠습니다.어쩌구 저쩌구 대충 칸만 잡아두겠습니다.\n" +
                    "\n" +
                    "쾌적한 서비스 제공을 위해 최선을 다하겠습니다.\n" +
                    "감사합니다."),
            Notice(2, "[공지] 서비스 점검 안내", "2020.00.00", "안녕하세요. 밥먹샤입니다.\n" +
                    "\n" +
                    "어쩌구 저쩌구 대충 칸만 잡아두겠습니다. \n" +
                    "어쩌구 저쩌구 대충 칸만 잡아두겠습니다.\n" +
                    "어쩌구 저쩌구 대충 칸만 잡아두겠습니다.어쩌구 저쩌구 대충 칸만 잡아두겠습니다.어쩌구 저쩌구 대충 칸만 잡아두겠습니다.\n" +
                    "\n" +
                    "쾌적한 서비스 제공을 위해 최선을 다하겠습니다.\n" +
                    "감사합니다."),
            Notice(3, "[공지] 서비스 점검 안내", "2020.00.00", "안녕하세요. 밥먹샤입니다.\n" +
                    "\n" +
                    "어쩌구 저쩌구 대충 칸만 잡아두겠습니다. \n" +
                    "어쩌구 저쩌구 대충 칸만 잡아두겠습니다.\n" +
                    "어쩌구 저쩌구 대충 칸만 잡아두겠습니다.어쩌구 저쩌구 대충 칸만 잡아두겠습니다.어쩌구 저쩌구 대충 칸만 잡아두겠습니다.\n" +
                    "\n" +
                    "쾌적한 서비스 제공을 위해 최선을 다하겠습니다.\n" +
                    "감사합니다."),
            Notice(4, "[공지] 서비스 점검 안내", "2020.00.00", "안녕하세요. 밥먹샤입니다.\n" +
                    "\n" +
                    "어쩌구 저쩌구 대충 칸만 잡아두겠습니다. \n" +
                    "어쩌구 저쩌구 대충 칸만 잡아두겠습니다.\n" +
                    "어쩌구 저쩌구 대충 칸만 잡아두겠습니다.어쩌구 저쩌구 대충 칸만 잡아두겠습니다.어쩌구 저쩌구 대충 칸만 잡아두겠습니다.\n" +
                    "\n" +
                    "쾌적한 서비스 제공을 위해 최선을 다하겠습니다.\n" +
                    "감사합니다.")
        ))

        return binding.root
    }
}