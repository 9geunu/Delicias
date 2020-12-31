package com.example.delicias.domain

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "restaurant")
data class Restaurant(
    @PrimaryKey
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("place")
    val place: String,
    @SerializedName("contact")
    val contact: String,
    @SerializedName("breakfastTime")
    val breakfastTime: String,
    @SerializedName("lunchTime")
    val lunchTime: String,
    @SerializedName("dinnerTime")
    val dinnerTime: String,
    @SerializedName("date")
    val date: String,
    @Embedded(prefix = "breakfast_")
    val breakfast: Meal,
    @Embedded(prefix = "lunch_")
    val lunch: Meal,
    @Embedded(prefix = "dinner_")
    val dinner: Meal,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    var isFavorite: Boolean,
    var distanceOrder: Int = 0
)