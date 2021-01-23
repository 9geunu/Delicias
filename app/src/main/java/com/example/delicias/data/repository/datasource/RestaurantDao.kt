package com.example.delicias.data.repository.datasource

import android.location.Location
import androidx.lifecycle.LiveData
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

    @Query("SELECT * FROM restaurant WHERE id = :id")
    abstract fun getRestaurantById(id: Long): Flow<Restaurant>

    @Query("DELETE  FROM restaurant")
    abstract suspend fun deleteAll()

    @Query("SELECT * FROM restaurant WHERE isFavorite = :isFavorite")
    protected abstract fun getFavoriteRestaurants(isFavorite: Boolean): Flow<List<Restaurant>>

    @Query("SELECT id, name, breakfast_menus AS menus, breakfast_message AS message, breakfast_isValid AS isValid, isFavorite FROM restaurant")
    abstract fun getAllBreakfast(): Flow<List<RestaurantMinimal>>

    @Query("SELECT id, name, lunch_menus AS menus, lunch_message AS message, lunch_isValid AS isValid, isFavorite FROM restaurant")
    abstract fun getAllLunch(): Flow<List<RestaurantMinimal>>

    @Query("SELECT id, name, dinner_menus AS menus, dinner_message AS message, dinner_isValid AS isValid, isFavorite FROM restaurant")
    abstract fun getAllDinner(): Flow<List<RestaurantMinimal>>

    @Query("SELECT id, name, breakfast_menus AS menus, breakfast_message AS message, breakfast_isValid AS isValid, isFavorite FROM restaurant WHERE name LIKE :searchquery")
    abstract fun searchForBreakfast(searchquery: String): Flow<List<RestaurantMinimal>>

    @Query("SELECT id, name, lunch_menus AS menus, lunch_message AS message, lunch_isValid AS isValid, isFavorite FROM restaurant WHERE name LIKE :searchquery")
    abstract fun searchForLunch(searchquery: String): Flow<List<RestaurantMinimal>>

    @Query("SELECT id, name, dinner_menus AS menus, dinner_message AS message, dinner_isValid AS isValid, isFavorite FROM restaurant WHERE name LIKE :searchquery")
    abstract fun searchForDinner(searchquery: String): Flow<List<RestaurantMinimal>>

    @Query("SELECT * FROM restaurant WHERE name LIKE :searchquery")
    abstract fun searchRestaurant(searchquery: String): Flow<List<Restaurant>>

    @Query("UPDATE restaurant SET isFavorite = :isFavorite WHERE id = :id")
    protected abstract suspend fun updateIsFavoriteOfRestaurantById(isFavorite: Boolean, id: Long)

    suspend fun updateRestaurantAsFavorite(id: Long) = updateIsFavoriteOfRestaurantById(true, id)

    suspend fun updateRestaurantAsNotFavorite(id: Long) = updateIsFavoriteOfRestaurantById(false, id)

    @Query("UPDATE restaurant SET isFavorite = NOT isFavorite WHERE id = :id")
    abstract suspend fun toggleIsFavoriteOfRestaurantById(id: Long)

    @Query("SELECT isFavorite FROM restaurant WHERE id = :id")
    abstract fun getIsFavoriteOfRestaurant(id: Long): Flow<Boolean>

    @Query("UPDATE restaurant SET distanceOrder = :distanceOrder WHERE id = :id")
    abstract suspend fun updateDistanceOrderOfRestaurantById(id: Long, distanceOrder: Int)
}