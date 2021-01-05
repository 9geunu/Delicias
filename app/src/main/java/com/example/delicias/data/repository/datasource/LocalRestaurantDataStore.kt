package com.example.delicias.data.repository.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.delicias.data.Converters
import com.example.delicias.domain.Restaurant
import com.example.delicias.domain.RestaurantMinimal
import com.example.delicias.domain.SearchHistory
import com.example.delicias.domain.SettingPreference

@Database(entities = [RestaurantMinimal::class, Restaurant::class, SearchHistory::class, SettingPreference::class], version = 10)
@TypeConverters(Converters::class)
abstract class LocalRestaurantDataStore : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
    abstract fun restaurantMinimalDao(): RestaurantMinimalDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun settingPreferenceDao(): SettingPreferenceDao

    companion object {
        val DB_NAME = "restaurant-db"

        private var instance: LocalRestaurantDataStore? = null

        fun getInstance(context: Context): LocalRestaurantDataStore {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): LocalRestaurantDataStore {
            return Room.databaseBuilder(context.applicationContext, LocalRestaurantDataStore::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}