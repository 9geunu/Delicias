package com.example.delicias.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.delicias.domain.repository.RestaurantRepository

class FavoritesViewModelFactory(
    private val repository: RestaurantRepository
) : ViewModelProvider.Factory  {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FavoritesViewModel(repository) as T
    }
}