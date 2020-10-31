package com.example.delicias.ui.home

import androidx.lifecycle.*
import com.example.delicias.domain.Restaurant
import com.example.delicias.domain.Date
import com.example.delicias.domain.RestaurantMinimal
import com.example.delicias.domain.repository.RestaurantRepository
import com.example.delicias.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect

class HomeViewModel(private val repository: RestaurantRepository) : ViewModel(){

    suspend fun getRestaurantsFromRemote() {
        val restaurantList = repository.getRestaurantsFromRemote()
        restaurantList.collect { value ->
            for (restaurant: Restaurant in value){
                repository.insertRestaurant(restaurant)
            }
        }
    }

    fun getAllBreakfast(): LiveData<List<RestaurantMinimal>> {
        return repository.getAllBreakfast().asLiveData(Dispatchers.Default + viewModelScope.coroutineContext)
    }

    fun getAllLunch(): LiveData<List<RestaurantMinimal>> {
        return repository.getAllLunch().asLiveData(Dispatchers.Default + viewModelScope.coroutineContext)
    }

    fun getAllDinner(): LiveData<List<RestaurantMinimal>> {
        return repository.getAllDinner().asLiveData(Dispatchers.Default + viewModelScope.coroutineContext)
    }
}