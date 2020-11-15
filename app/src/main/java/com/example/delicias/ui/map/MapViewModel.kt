package com.example.delicias.ui.map

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delicias.domain.Restaurant
import com.example.delicias.domain.repository.RestaurantRepository
import kotlinx.coroutines.launch
import java.net.URLEncoder


class MapViewModel(private val repository: RestaurantRepository, private val context: Context) : ViewModel() {
    var isNMapAppDeeplinkCardViewVisible = MutableLiveData<Boolean>()
    var isRestaurantInfoCardViewVisible = MutableLiveData<Boolean>()
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
            setNMapAppDeeplinkCardViewInvisible()
        } catch (e: Exception){
            setNMapAppDeeplinkCardViewInvisible()
            Toast.makeText(context, "네이버 지도 앱이 설치되지 않았습니다.", Toast.LENGTH_LONG).show()
        }
    }

    fun onFindRoadCancelButtonClicked(){
        setNMapAppDeeplinkCardViewInvisible()
    }

    fun onRestaurantInfoClicked(){
        mapSizePercentage.value = 40F
        isRestaurantInfoCardViewVisible.value = false
    }

    fun setNMapAppDeeplinkCardViewVisible(){
        isNMapAppDeeplinkCardViewVisible.value = true
        isRestaurantInfoCardViewVisible.value = false
    }

    fun setNMapAppDeeplinkCardViewInvisible(){
        isNMapAppDeeplinkCardViewVisible.value = false
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
}