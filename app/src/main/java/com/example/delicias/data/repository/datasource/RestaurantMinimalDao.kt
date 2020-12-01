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

    @Query("DELETE  FROM restaurant_minimal")
    abstract suspend fun deleteAll()

    @Transaction
    open suspend fun updateRestaurantMinimals(restaurantMinimals: List<RestaurantMinimal>){
        deleteAll()
        insertAll(restaurantMinimals)
    }

    @Query("UPDATE restaurant_minimal SET isFavorite = :isFavorite WHERE id = :id")
    protected abstract suspend fun updateIsFavoriteOfRestaurantMinimalById(isFavorite: Boolean, id: Long)

    suspend fun updateRestaurantMinimalAsFavorite(id: Long) = updateIsFavoriteOfRestaurantMinimalById(true, id)

    suspend fun updateRestaurantMinimalAsNotFavorite(id: Long) = updateIsFavoriteOfRestaurantMinimalById(false, id)
}