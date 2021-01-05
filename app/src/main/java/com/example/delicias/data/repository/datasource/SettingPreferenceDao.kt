package com.example.delicias.data.repository.datasource

import androidx.room.Dao
import androidx.room.Query
import com.example.delicias.domain.SettingPreference
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SettingPreferenceDao: BaseDao<SettingPreference> {
    @Query("SELECT isMenuLessRestaurantHidden FROM setting_preference WHERE id = 1")
    abstract fun isMenuLessRestaurantHidden(): Flow<Boolean>

    @Query("SELECT isPushEnabled FROM setting_preference WHERE id = 1")
    abstract fun isPushEnabled(): Flow<Boolean>

    @Query("UPDATE setting_preference SET isMenuLessRestaurantHidden = :isHidden WHERE id = 1")
    abstract suspend fun updateIsMenuLessRestaurantHidden(isHidden: Boolean)

    @Query("UPDATE setting_preference SET isPushEnabled = :isEnabled WHERE id = 1")
    abstract suspend fun updateIsPushEnabled(isEnabled: Boolean)

    @Query("DELETE FROM setting_preference")
    abstract suspend fun deleteAll()
}