package com.example.delicias.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.delicias.domain.Date
import com.example.delicias.domain.RestaurantMinimal
import com.example.delicias.domain.repository.RestaurantRepository
import com.example.delicias.util.Util

class FavoritesViewModel(private val repository: RestaurantRepository) : ViewModel() {
    private var _dateLiveData = Util.getDates().asLiveData(viewModelScope.coroutineContext)

    val dateLiveData: LiveData<ArrayList<Date>>
        get() = _dateLiveData

    fun getAllFavoriteBreakfast(): LiveData<List<RestaurantMinimal>> {
        return repository.getAllFavoriteBreakfast().asLiveData(viewModelScope.coroutineContext)
    }

    fun getAllFavoriteLunch(): LiveData<List<RestaurantMinimal>> {
        return repository.getAllFavoriteLunch().asLiveData(viewModelScope.coroutineContext)
    }

    fun getAllFavoriteDinner(): LiveData<List<RestaurantMinimal>> {
        return repository.getAllFavoriteDinner().asLiveData(viewModelScope.coroutineContext)
    }

    fun getAllRestaurantMinimal(): LiveData<List<RestaurantMinimal>> {
        return repository.getAllRestaurantMinimals().asLiveData(viewModelScope.coroutineContext)
    }
}