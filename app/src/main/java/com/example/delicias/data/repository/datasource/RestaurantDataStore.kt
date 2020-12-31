package com.example.delicias.data.repository.datasource

import com.example.delicias.domain.Restaurant
import kotlinx.coroutines.flow.Flow

interface RestaurantDataStore {
    fun getAllRestaurants() : Flow<List<Restaurant>>
}