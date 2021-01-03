package com.example.delicias.domain

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurant_minimal")
data class RestaurantMinimal(
    @PrimaryKey
    val id: Long,
    val name: String,
    @Embedded
    val meal: Meal?,
    var isFavorite: Boolean
)