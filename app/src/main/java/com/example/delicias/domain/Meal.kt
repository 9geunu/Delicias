package com.example.delicias.domain

import com.google.gson.annotations.SerializedName

data class Meal(
    @SerializedName("menus")
    val menus: List<Menu>,
    @SerializedName("message")
    val message: String,
    @SerializedName("isValid")
    val isValid: Boolean
)