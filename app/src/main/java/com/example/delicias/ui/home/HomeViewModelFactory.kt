package com.example.delicias.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.delicias.domain.repository.RestaurantRepository

class HomeViewModelFactory(
    private val repository: RestaurantRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}