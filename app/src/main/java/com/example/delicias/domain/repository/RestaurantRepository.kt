package com.example.delicias.domain.repository

import com.example.delicias.domain.Restaurant
import com.example.delicias.domain.RestaurantMinimal
import kotlinx.coroutines.flow.Flow

interface RestaurantRepository {
    fun getRestaurantsFromRemote() : Flow<List<Restaurant>>
    suspend fun insertRestaurant(restaurant: Restaurant)
    suspend fun deleteRestaurant(restaurant: Restaurant)
    suspend fun deleteAllRestaurant()
    fun getAllBreakfast(): Flow<List<RestaurantMinimal>>
    fun getAllLunch(): Flow<List<RestaurantMinimal>>
    fun getAllDinner(): Flow<List<RestaurantMinimal>>
    fun getAllFavoriteBreakfast(): Flow<List<RestaurantMinimal>>
    fun getAllFavoriteLunch(): Flow<List<RestaurantMinimal>>
    fun getAllFavoriteDinner(): Flow<List<RestaurantMinimal>>
}