package com.example.delicias.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "setting_preference")
data class SettingPreference(
    @PrimaryKey
    val id: Long,
    var isMenuLessRestaurantHidden: Boolean,
    var isPushEnabled: Boolean
)
