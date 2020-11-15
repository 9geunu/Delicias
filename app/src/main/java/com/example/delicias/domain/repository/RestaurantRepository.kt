package com.example.delicias.domain.repository

import com.example.delicias.domain.Restaurant
import com.example.delicias.domain.RestaurantMinimal
import kotlinx.coroutines.flow.Flow

interface RestaurantRepository {
    fun getRestaurantsFromRemote() : Flow<List<Restaurant>>
    fun getAllRestaurants(): Flow<List<Restaurant>>
    fun getRestaurantById(id: Long): Flow<Restaurant>
    suspend fun insertRestaurant(restaurant: Restaurant)
    suspend fun deleteRestaurant(restaurant: Restaurant)
    suspend fun deleteAllRestaurant()
    fun getAllBreakfast(): Flow<List<RestaurantMinimal>>
    fun getAllLunch(): Flow<List<RestaurantMinimal>>
    fun getAllDinner(): Flow<List<RestaurantMinimal>>
    fun getAllFavoriteBreakfast(): Flow<List<RestaurantMinimal>>
    fun getAllFavoriteLunch(): Flow<List<RestaurantMinimal>>
    fun getAllFavoriteDinner(): Flow<List<RestaurantMinimal>>
    fun searchForBreakfast(searchquery: String): Flow<List<RestaurantMinimal>>
    fun searchForLunch(searchquery: String): Flow<List<RestaurantMinimal>>
    fun searchForDinner(searchquery: String): Flow<List<RestaurantMinimal>>
}