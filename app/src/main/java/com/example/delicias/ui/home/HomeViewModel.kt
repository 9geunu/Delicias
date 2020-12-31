package com.example.delicias.ui.home

import androidx.lifecycle.*
import com.example.delicias.domain.Restaurant
import com.example.delicias.domain.RestaurantMinimal
import com.example.delicias.domain.repository.RestaurantRepository
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

    suspend fun updateBreakfast() {
        repository.updateBreakfast()
    }

    suspend fun updateLunch() {
        repository.updateLunch()
    }

    suspend fun updateDinner() {
        repository.updateDinner()
    }

    fun searchForBreakfast(query: String): LiveData<List<RestaurantMinimal>>{
        return repository.searchForBreakfast(query).asLiveData(viewModelScope.coroutineContext)
    }

    fun searchForLunch(query: String): LiveData<List<RestaurantMinimal>>{
        return repository.searchForLunch(query).asLiveData(viewModelScope.coroutineContext)
    }

    fun searchForDinner(query: String): LiveData<List<RestaurantMinimal>>{
        return repository.searchForDinner(query).asLiveData(viewModelScope.coroutineContext)
    }

    fun getRestaurantMinimalOrderByName(): LiveData<List<RestaurantMinimal>> {
        return repository.getRestaurantMinimalOrderByName().asLiveData(viewModelScope.coroutineContext)
    }

    fun getRestaurantMinimalOrderByDistance(): LiveData<List<RestaurantMinimal>> {
        return repository.getRestaurantMinimalOrderByDistance().asLiveData(viewModelScope.coroutineContext)
    }
}