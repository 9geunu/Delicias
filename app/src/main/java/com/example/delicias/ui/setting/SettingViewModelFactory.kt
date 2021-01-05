package com.example.delicias.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.delicias.domain.repository.RestaurantRepository

class SettingViewModelFactory(private val repository: RestaurantRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingViewModel(repository) as T
    }
}