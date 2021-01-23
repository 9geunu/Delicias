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

    @Query("SELECT m.id, restaurant.isFavorite AS isFavorite, m.menus, m.isValid, m.message, m.name FROM restaurant_minimal m INNER JOIN restaurant USING(id) LEFT OUTER JOIN setting_preference s WHERE CASE WHEN s.isMenuLessRestaurantHidden THEN m.menus IS NOT NULL ELSE m.menus IS NOT NULL OR m.menus IS NULL END ORDER BY m.name")
    abstract fun getRestaurantMinimalOrderByName(): Flow<List<RestaurantMinimal>>

    @Query("SELECT m.id, restaurant.isFavorite AS isFavorite, m.menus, m.isValid, m.message, m.name FROM restaurant_minimal m INNER JOIN restaurant USING(id) LEFT OUTER JOIN setting_preference s WHERE CASE WHEN s.isMenuLessRestaurantHidden THEN m.menus IS NOT NULL ELSE m.menus IS NOT NULL OR m.menus IS NULL END ORDER BY restaurant.distanceOrder")
    abstract fun getRestaurantMinimalOrderByDistance(): Flow<List<RestaurantMinimal>>

    @Query("SELECT m.* FROM restaurant_minimal m INNER JOIN restaurant USING(id) LEFT OUTER JOIN setting_preference s WHERE CASE WHEN s.isMenuLessRestaurantHidden THEN m.menus IS NOT NULL ELSE m.menus IS NOT NULL OR m.menus IS NULL END AND restaurant.isFavorite = 1 ORDER BY m.name")
    abstract fun getFavoriteRestaurantMinimalOrderByName(): Flow<List<RestaurantMinimal>>

    @Query("SELECT m.* FROM restaurant_minimal m INNER JOIN restaurant USING(id) LEFT OUTER JOIN setting_preference s WHERE CASE WHEN s.isMenuLessRestaurantHidden THEN m.menus IS NOT NULL ELSE m.menus IS NOT NULL OR m.menus IS NULL END AND restaurant.isFavorite = 1 ORDER BY restaurant.distanceOrder")
    abstract fun getFavoriteRestaurantMinimalOrderByDistance(): Flow<List<RestaurantMinimal>>

    @Query("DELETE  FROM restaurant_minimal")
    abstract suspend fun deleteAll()

    @Transaction
    open suspend fun updateRestaurantMinimals(restaurantMinimals: List<RestaurantMinimal>){
        deleteAll()
        insertAll(restaurantMinimals)
    }
}