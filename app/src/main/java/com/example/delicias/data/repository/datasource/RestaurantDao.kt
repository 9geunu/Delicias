package com.example.delicias.data.repository.datasource

import androidx.room.Dao
import androidx.room.Query
import com.example.delicias.domain.Restaurant
import com.example.delicias.domain.RestaurantMinimal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
abstract class RestaurantDao : BaseDao<Restaurant>{
    @Query("SELECT * FROM restaurant")
    abstract fun getRestaurants(): Flow<List<Restaurant>>

    @Query("SELECT * FROM restaurant WHERE name = :name")
    abstract fun getRestaurantByName(name: String): Flow<Restaurant>

    @Query("DELETE  FROM restaurant")
    abstract suspend fun deleteAll()

    @Query("SELECT * FROM restaurant WHERE isFavorite = :isFavorite")
    protected abstract fun getFavoriteRestaurants(isFavorite: Boolean): Flow<List<Restaurant>>

    fun getFavoriteRestaurants(): Flow<List<Restaurant>>
    = getFavoriteRestaurants(true).distinctUntilChanged()

    @Query("SELECT name, breakfast_menus AS menus, breakfast_message AS message, breakfast_isValid AS isValid, isFavorite FROM restaurant")
    abstract fun getAllBreakfast(): Flow<List<RestaurantMinimal>>

    @Query("SELECT name, lunch_menus AS menus, lunch_message AS message, lunch_isValid AS isValid, isFavorite FROM restaurant")
    abstract fun getAllLunch(): Flow<List<RestaurantMinimal>>

    @Query("SELECT name, dinner_menus AS menus, dinner_message AS message, dinner_isValid AS isValid, isFavorite FROM restaurant")
    abstract fun getAllDinner(): Flow<List<RestaurantMinimal>>

    @Query("SELECT name, breakfast_menus AS menus, breakfast_message AS message, breakfast_isValid AS isValid, isFavorite FROM restaurant WHERE isFavorite = :isFavorite")
    protected abstract fun getAllFavoriteBreakfast(isFavorite: Boolean): Flow<List<RestaurantMinimal>>

    fun getAllFavoriteBreakfast() = getAllFavoriteBreakfast(true).distinctUntilChanged()

    @Query("SELECT name, lunch_menus AS menus, lunch_message AS message, lunch_isValid AS isValid, isFavorite FROM restaurant WHERE isFavorite = :isFavorite")
    protected abstract fun getAllFavoriteLunch(isFavorite: Boolean): Flow<List<RestaurantMinimal>>

    fun getAllFavoriteLunch() = getAllFavoriteLunch(true).distinctUntilChanged()

    @Query("SELECT name, dinner_menus AS menus, dinner_message AS message, dinner_isValid AS isValid, isFavorite FROM restaurant WHERE isFavorite = :isFavorite")
    protected abstract fun getAllFavoriteDinner(isFavorite: Boolean): Flow<List<RestaurantMinimal>>

    fun getAllFavoriteDinner() = getAllFavoriteDinner(true).distinctUntilChanged()
}