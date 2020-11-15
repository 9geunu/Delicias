package com.example.delicias.data.repository

import com.example.delicias.data.repository.datasource.RemoteRestaurantDataStore
import com.example.delicias.domain.Restaurant
import com.example.delicias.data.repository.datasource.RestaurantDao
import com.example.delicias.domain.RestaurantMinimal
import com.example.delicias.domain.repository.RestaurantRepository
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

class RestaurantDataRepository(private val restaurantDao: RestaurantDao) : RestaurantRepository{

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

    override fun getAllBreakfast(): Flow<List<RestaurantMinimal>> {
        return restaurantDao.getAllBreakfast()
    }

    override fun getAllLunch(): Flow<List<RestaurantMinimal>> {
        return restaurantDao.getAllLunch()
    }

    override fun getAllDinner(): Flow<List<RestaurantMinimal>> {
        return restaurantDao.getAllDinner()
    }

    override fun getAllFavoriteBreakfast(): Flow<List<RestaurantMinimal>> {
        return restaurantDao.getAllFavoriteBreakfast()
    }

    override fun getAllFavoriteLunch(): Flow<List<RestaurantMinimal>> {
        return restaurantDao.getAllFavoriteLunch()
    }

    override fun getAllFavoriteDinner(): Flow<List<RestaurantMinimal>> {
        return restaurantDao.getAllFavoriteDinner()
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
}