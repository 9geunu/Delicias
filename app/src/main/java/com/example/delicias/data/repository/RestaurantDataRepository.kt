package com.example.delicias.data.repository

import com.example.delicias.data.repository.datasource.RemoteRestaurantDataStore
import com.example.delicias.domain.Restaurant
import com.example.delicias.data.repository.datasource.RestaurantDao
import com.example.delicias.data.repository.datasource.RestaurantMinimalDao
import com.example.delicias.domain.RestaurantMinimal
import com.example.delicias.domain.repository.RestaurantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.lang.Exception

class RestaurantDataRepository(
    private val restaurantDao: RestaurantDao,
    private val restaurantMinimalDao: RestaurantMinimalDao) : RestaurantRepository{

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
}