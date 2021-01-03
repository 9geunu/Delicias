package com.example.delicias.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.delicias.domain.Date
import com.example.delicias.domain.RestaurantMinimal
import com.example.delicias.domain.repository.RestaurantRepository
import com.example.delicias.util.Util
import kotlinx.coroutines.flow.Flow

class FavoritesViewModel(private val repository: RestaurantRepository) : ViewModel() {
    private var _dateLiveData = Util.getDates().asLiveData(viewModelScope.coroutineContext)

    val dateLiveData: LiveData<ArrayList<Date>>
        get() = _dateLiveData

    suspend fun updateBreakfast() {
        repository.updateBreakfast()
    }

    suspend fun updateLunch() {
        repository.updateLunch()
    }

    suspend fun updateDinner() {
        repository.updateDinner()
    }

    fun getFavoriteRestaurantMinimalOrderByName(): LiveData<List<RestaurantMinimal>> {
        return repository.getFavoriteRestaurantMinimalOrderByName().asLiveData(viewModelScope.coroutineContext)
    }

    fun getFavoriteRestaurantMinimalOrderByDistance(): LiveData<List<RestaurantMinimal>> {
        return repository.getFavoriteRestaurantMinimalOrderByDistance().asLiveData(viewModelScope.coroutineContext)
    }
}