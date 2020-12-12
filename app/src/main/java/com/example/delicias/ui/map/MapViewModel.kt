package com.example.delicias.ui.map

import android.app.AlertDialog
import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.*
import com.example.delicias.domain.Restaurant
import com.example.delicias.domain.repository.RestaurantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder


class MapViewModel(private val repository: RestaurantRepository, private val context: Context) : ViewModel() {
    var isRestaurantInfoCardViewVisible = MutableLiveData<Boolean>()
    var isRestaurantSearchResultVisible = MutableLiveData<Boolean>()
    var mapSizePercentage = MutableLiveData<Float>(100F)

    fun onFavoriteButtonClick(restaurant: Restaurant){
        if (restaurant.isFavorite){
            restaurant.isFavorite = false
            viewModelScope.launch {
                repository.insertRestaurant(restaurant)
            }
        }
        else {
            restaurant.isFavorite = true
            viewModelScope.launch {
                repository.insertRestaurant(restaurant)
            }
        }
    }

    fun onCopyAddressButtonClick(restaurant: Restaurant){
        val clipboard: ClipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(
            "restaurant address",
            "서울시 관악구 관악로 1 서울대학교 " + restaurant.place
        )
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "${restaurant.name} 주소를 클립보드에 복사했습니다.", Toast.LENGTH_LONG).show()
    }

    fun onFindRoadOkButtonClick(restaurant: Restaurant){
        val findRoadIntent: Intent = Uri.parse(
            "nmap://route/walk?dlat=${restaurant.latitude}&dlng=${restaurant.longitude}&dname=${
                URLEncoder.encode(
                    restaurant.name,
                    "UTF-8"
                )
            }&appname=com.example.myapp"
        ).let { findRoad ->
            Intent(Intent.ACTION_VIEW, findRoad)
        }
        try {
            context.startActivity(findRoadIntent)
        } catch (e: Exception){
            Toast.makeText(context, "네이버 지도 앱이 설치되지 않았습니다.", Toast.LENGTH_LONG).show()
        }
    }

    fun setNMapAppDeeplinkCardViewVisible(restaurant: Restaurant){
        val builder = AlertDialog.Builder(context)

        builder.setTitle("길 찾기").setMessage("네이버 지도 앱으로 이동합니다.")

        builder.setPositiveButton("확인") { dialog, which ->
            onFindRoadOkButtonClick(restaurant)
        }

        builder.setNegativeButton("취소") { dialog, which ->
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun setRestaurantInfoVisible(){
        isRestaurantInfoCardViewVisible.value = true
    }

    fun setRestaurantInfoInVisible(){
        isRestaurantInfoCardViewVisible.value = false
    }

    fun setRestaurantDetailInfoInvisible(){
        mapSizePercentage.value = 100F
    }

    fun setRestaurantSearchResultVisible(){
        isRestaurantSearchResultVisible.value = true
    }

    fun setRestaurantSearchResultInvisible(){
        isRestaurantSearchResultVisible.value = false
    }

    fun searchRestaurant(query: String): LiveData<List<Restaurant>> {
        return repository.searchRestaurant(query).asLiveData(Dispatchers.Default + viewModelScope.coroutineContext)
    }
}