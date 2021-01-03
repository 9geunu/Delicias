package com.example.delicias.data.repository.datasource

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.delicias.domain.RestaurantMinimal
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RestaurantMinimalDao: BaseDao<RestaurantMinimal> {

    @Query("SELECT * FROM restaurant_minimal")
    abstract fun getRestaurantMinimals(): Flow<List<RestaurantMinimal>>

    @Query("SELECT * FROM restaurant_minimal WHERE restaurant_minimal.menus IS NOT NULL ORDER BY name")
    abstract fun getRestaurantMinimalOrderByName(): Flow<List<RestaurantMinimal>>

    @Query("SELECT m.* FROM restaurant_minimal AS m INNER JOIN restaurant ON m.id = restaurant.id WHERE m.menus IS NOT NULL ORDER BY restaurant.distanceOrder")
    abstract fun getRestaurantMinimalOrderByDistance(): Flow<List<RestaurantMinimal>>

    @Query("SELECT m.* FROM restaurant_minimal AS m INNER JOIN restaurant ON m.id = restaurant.id WHERE m.menus IS NOT NULL AND restaurant.isFavorite = 1 ORDER BY m.name")
    abstract fun getFavoriteRestaurantMinimalOrderByName(): Flow<List<RestaurantMinimal>>

    @Query("SELECT m.* FROM restaurant_minimal AS m INNER JOIN restaurant ON m.id = restaurant.id WHERE m.menus IS NOT NULL AND restaurant.isFavorite = 1 ORDER BY restaurant.distanceOrder")
    abstract fun getFavoriteRestaurantMinimalOrderByDistance(): Flow<List<RestaurantMinimal>>

    @Query("DELETE  FROM restaurant_minimal")
    abstract suspend fun deleteAll()

    @Transaction
    open suspend fun updateRestaurantMinimals(restaurantMinimals: List<RestaurantMinimal>){
        deleteAll()
        insertAll(restaurantMinimals)
    }
}