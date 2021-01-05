package com.example.delicias.domain.repository

import com.example.delicias.domain.Restaurant
import com.example.delicias.domain.RestaurantMinimal
import com.example.delicias.domain.SearchHistory
import com.example.delicias.domain.SettingPreference
import kotlinx.coroutines.flow.Flow

interface RestaurantRepository {
    fun getRestaurantsFromRemote() : Flow<List<Restaurant>>
    fun getAllRestaurants(): Flow<List<Restaurant>>
    fun getAllRestaurantMinimals(): Flow<List<RestaurantMinimal>>
    fun getRestaurantById(id: Long): Flow<Restaurant>

    suspend fun insertRestaurant(restaurant: Restaurant)
    suspend fun deleteRestaurant(restaurant: Restaurant)
    suspend fun deleteAllRestaurant()
    suspend fun updateRestaurantMinimals(restaurantMinimals: List<RestaurantMinimal>)
    suspend fun updateDistanceOrderOfRestaurantById(id: Long, distanceOrder: Int)

    fun searchForBreakfast(searchquery: String): Flow<List<RestaurantMinimal>>
    fun searchForLunch(searchquery: String): Flow<List<RestaurantMinimal>>
    fun searchForDinner(searchquery: String): Flow<List<RestaurantMinimal>>
    fun searchRestaurant(searchquery: String): Flow<List<Restaurant>>

    fun getRestaurantMinimalOrderByName(): Flow<List<RestaurantMinimal>>
    fun getRestaurantMinimalOrderByDistance(): Flow<List<RestaurantMinimal>>
    fun getFavoriteRestaurantMinimalOrderByName(): Flow<List<RestaurantMinimal>>
    fun getFavoriteRestaurantMinimalOrderByDistance(): Flow<List<RestaurantMinimal>>

    suspend fun updateBreakfast()
    suspend fun updateLunch()
    suspend fun updateDinner()

    suspend fun insertSettingPreference(settingPreference: SettingPreference)
    fun isMenuLessRestaurantHidden(): Flow<Boolean>
    fun isPushEnabled(): Flow<Boolean>
    suspend fun updateIsMenuLessRestaurantHidden(isHidden: Boolean)
    suspend fun updateIsPushEnabled(isEnabled: Boolean)

    suspend fun insertSearchHistory(searchHistory: SearchHistory)
    fun getAllSearchHistory(): Flow<List<SearchHistory>>
    suspend fun deleteAllSearchHistory()
    suspend fun deleteSearchHistoryById(id: Long)
}