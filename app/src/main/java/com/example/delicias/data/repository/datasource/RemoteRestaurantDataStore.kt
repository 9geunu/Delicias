package com.example.delicias.data.repository.datasource

import com.example.delicias.domain.Restaurant
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.net.URL

class RemoteRestaurantDataStore : RestaurantDataStore {

    override fun getAllRestaurants(): Flow<List<Restaurant>> = flow<List<Restaurant>> {
        val response = URL("https://ce63f316-f695-4c2e-9e69-2c85ef983182.mock.pstmn.io/cafeteria").readText()

        val restaurantList = Gson().fromJson<List<Restaurant>>(response, object : TypeToken<List<Restaurant>>(){}.type)

        emit(restaurantList)
    }.flowOn(Dispatchers.Default)
}