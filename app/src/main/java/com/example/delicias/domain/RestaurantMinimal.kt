package com.example.delicias.domain

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurant_minimal")
data class RestaurantMinimal(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name: String,
    @Embedded
    val meal: Meal,
    var isFavorite: Boolean
)