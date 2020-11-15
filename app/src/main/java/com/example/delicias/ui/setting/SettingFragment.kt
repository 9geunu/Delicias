package com.example.delicias.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.delicias.R
import com.example.delicias.databinding.FragmentSettingBinding
import com.example.delicias.ui.DevelopersActivity
import com.example.delicias.ui.MainActivity

class SettingFragment : Fragment() {
    lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)

        binding.llSeeDeveloper.setOnClickListener {
            var intent = Intent(activity, DevelopersActivity::class.java)

            startActivity(intent)
        }

        return binding.root
    }
}