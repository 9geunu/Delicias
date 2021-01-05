package com.example.delicias.data.repository

import android.content.Context
import com.example.delicias.data.repository.datasource.*
import com.example.delicias.domain.Restaurant
import com.example.delicias.domain.RestaurantMinimal
import com.example.delicias.domain.SearchHistory
import com.example.delicias.domain.SettingPreference
import com.example.delicias.domain.repository.RestaurantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.lang.Exception

class RestaurantDataRepository(context: Context) : RestaurantRepository{
    private val restaurantDao = LocalRestaurantDataStore.getInstance(context).restaurantDao()
    private val restaurantMinimalDao = LocalRestaurantDataStore.getInstance(context).restaurantMinimalDao()
    private val searchHistoryDao = LocalRestaurantDataStore.getInstance(context).searchHistoryDao()
    private val settingPreferenceDao = LocalRestaurantDataStore.getInstance(context).settingPreferenceDao()

    override fun getRestaurantsFromRemote(): Flow<List<Restaurant>> {
        return try {
            RemoteRestaurantDataStore().getAllRestaurants()
        } catch (e: Exception){
            restaurantDao.getRestaurants()
        }
    }

    override fun getAllRestaurants(): Flow<List<Restaurant>> {
        return restaurantDao.getRestaurants()
    }

    override fun getAllRestaurantMinimals(): Flow<List<RestaurantMinimal>> {
        return restaurantMinimalDao.getRestaurantMinimals()
    }

    override fun getRestaurantById(id: Long): Flow<Restaurant> {
        return restaurantDao.getRestaurantById(id)
    }

    override suspend fun insertRestaurant(restaurant: Restaurant) {
        restaurantDao.insert(restaurant)
    }

    override suspend fun deleteRestaurant(restaurant: Restaurant) {
        restaurantDao.delete(restaurant)
    }

    override suspend fun deleteAllRestaurant() {
        restaurantDao.deleteAll()
    }

    override suspend fun updateRestaurantMinimals(restaurantMinimals: List<RestaurantMinimal>) {
        restaurantMinimalDao.updateRestaurantMinimals(restaurantMinimals)
    }

    override suspend fun updateDistanceOrderOfRestaurantById(id: Long, distanceOrder: Int) {
        restaurantDao.updateDistanceOrderOfRestaurantById(id, distanceOrder)
    }

    override fun searchForBreakfast(searchquery: String): Flow<List<RestaurantMinimal>> {
        return restaurantDao.searchForBreakfast(searchquery)
    }

    override fun searchForLunch(searchquery: String): Flow<List<RestaurantMinimal>> {
        return restaurantDao.searchForLunch(searchquery)
    }

    override fun searchForDinner(searchquery: String): Flow<List<RestaurantMinimal>> {
        return restaurantDao.searchForDinner(searchquery)
    }

    override fun searchRestaurant(searchquery: String): Flow<List<Restaurant>> {
        return restaurantDao.searchRestaurant(searchquery)
    }

    override fun getRestaurantMinimalOrderByName(): Flow<List<RestaurantMinimal>> {
        return restaurantMinimalDao.getRestaurantMinimalOrderByName()
    }

    override fun getRestaurantMinimalOrderByDistance(): Flow<List<RestaurantMinimal>> {
        return restaurantMinimalDao.getRestaurantMinimalOrderByDistance()
    }

    override fun getFavoriteRestaurantMinimalOrderByName(): Flow<List<RestaurantMinimal>> {
        return restaurantMinimalDao.getFavoriteRestaurantMinimalOrderByName()
    }

    override fun getFavoriteRestaurantMinimalOrderByDistance(): Flow<List<RestaurantMinimal>> {
        return restaurantMinimalDao.getFavoriteRestaurantMinimalOrderByDistance()
    }

    override suspend fun updateBreakfast() {
        val breakfastList = restaurantDao.getAllBreakfast().first()
        restaurantMinimalDao.updateRestaurantMinimals(breakfastList)
    }

    override suspend fun updateLunch() {
        val lunchList = restaurantDao.getAllLunch().first()
        restaurantMinimalDao.updateRestaurantMinimals(lunchList)
    }

    override suspend fun updateDinner() {
        val dinnerList = restaurantDao.getAllDinner().first()
        restaurantMinimalDao.updateRestaurantMinimals(dinnerList)
    }

    override suspend fun insertSettingPreference(settingPreference: SettingPreference) {
        settingPreferenceDao.insert(settingPreference)
    }

    override fun isMenuLessRestaurantHidden(): Flow<Boolean> {
        return settingPreferenceDao.isMenuLessRestaurantHidden()
    }

    override fun isPushEnabled(): Flow<Boolean> {
        return settingPreferenceDao.isPushEnabled()
    }

    override suspend fun updateIsMenuLessRestaurantHidden(isHidden: Boolean) {
        settingPreferenceDao.updateIsMenuLessRestaurantHidden(isHidden)
    }

    override suspend fun updateIsPushEnabled(isEnabled: Boolean) {
        settingPreferenceDao.updateIsPushEnabled(isEnabled)
    }

    override suspend fun insertSearchHistory(searchHistory: SearchHistory) {
        searchHistoryDao.insert(searchHistory)
    }

    override fun getAllSearchHistory(): Flow<List<SearchHistory>> {
        return searchHistoryDao.getAll()
    }

    override suspend fun deleteAllSearchHistory() {
        searchHistoryDao.deleteAll()
    }

    override suspend fun deleteSearchHistoryById(id: Long) {
        searchHistoryDao.deleteById(id)
    }
}