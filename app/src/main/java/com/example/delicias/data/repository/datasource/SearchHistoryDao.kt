package com.example.delicias.data.repository.datasource

import androidx.room.Dao
import androidx.room.Query
import com.example.delicias.domain.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SearchHistoryDao: BaseDao<SearchHistory> {

    @Query("SELECT * FROM search_history")
    abstract fun getAll(): Flow<List<SearchHistory>>

    @Query("DELETE FROM search_history")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM search_history WHERE id = :id")
    abstract suspend fun deleteById(id: Long)
}