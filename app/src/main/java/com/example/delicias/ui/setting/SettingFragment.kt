package com.example.delicias.ui.setting

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import com.example.delicias.R
import com.example.delicias.data.repository.RestaurantDataRepository
import com.example.delicias.databinding.FragmentSettingBinding
import com.example.delicias.domain.SearchHistory
import com.example.delicias.domain.SettingPreference
import com.example.delicias.ui.DevelopersActivity
import com.example.delicias.ui.MainActivity
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {
    lateinit var binding: FragmentSettingBinding
    lateinit var settingViewModel: SettingViewModel
    lateinit var lifecycleOwner: LifecycleOwner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)

        lifecycleOwner = viewLifecycleOwner

        val factory = SettingViewModelFactory(RestaurantDataRepository(requireContext()))
        settingViewModel = ViewModelProviders.of(this, factory).get(SettingViewModel::class.java)

        binding.llSeeDeveloper.setOnClickListener {
            var intent = Intent(activity, DevelopersActivity::class.java)

            startActivity(intent)
        }

        settingViewModel.viewModelScope.launch {
            settingViewModel.insertSettingPreference(SettingPreference(1, false, false))
        }

        binding.swHideMenulessRestaurant.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked){
                //TODO Pressed 상태에서 민트색 발견
                binding.swHideMenulessRestaurant.thumbTintList = ContextCompat.getColorStateList(requireContext(), R.color.orange)
                binding.swHideMenulessRestaurant.trackTintList = ContextCompat.getColorStateList(requireContext(), R.color.paleSalmon)
            }
            else {
                binding.swHideMenulessRestaurant.isPressed = false
                binding.swHideMenulessRestaurant.isUseMaterialThemeColors = true
            }
            settingViewModel.viewModelScope.launch {
                settingViewModel.updateIsMenuLessRestaurantHidden(isChecked)
            }
        }

        return binding.root
    }
}