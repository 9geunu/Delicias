package com.example.delicias.data.repository.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.delicias.data.Converters
import com.example.delicias.domain.Restaurant
import com.example.delicias.domain.RestaurantMinimal

@Database(entities = [RestaurantMinimal::class, Restaurant::class], version = 3)
@TypeConverters(Converters::class)
abstract class LocalRestaurantDataStore : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao

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