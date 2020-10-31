package com.example.delicias.data.repository.datasource

import com.example.delicias.domain.Restaurant
import com.example.delicias.domain.RestaurantMinimal
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

interface RestaurantDataStore {
    fun getAllRestaurants() : Flow<List<Restaurant>>
}