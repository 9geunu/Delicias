package com.example.delicias.ui.setting

import androidx.lifecycle.ViewModel
import com.example.delicias.domain.SettingPreference
import com.example.delicias.domain.repository.RestaurantRepository

class SettingViewModel(private val repository: RestaurantRepository) : ViewModel() {

    suspend fun insertSettingPreference(settingPreference: SettingPreference){
        repository.insertSettingPreference(settingPreference)
    }

    suspend fun updateIsMenuLessRestaurantHidden(isHidden: Boolean) {
        repository.updateIsMenuLessRestaurantHidden(isHidden)
    }

    suspend fun updateIsPushEnabled(isEnabled: Boolean) {
        repository.updateIsPushEnabled(isEnabled)
    }
}