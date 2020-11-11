package com.example.delicias.ui.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.delicias.domain.repository.RestaurantRepository

class MapViewModelFactory(private val repository: RestaurantRepository, private val context: Context) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MapViewModel(repository, context) as T
    }
}