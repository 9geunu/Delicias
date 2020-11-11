package com.example.delicias.ui.map

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delicias.domain.Restaurant
import com.example.delicias.domain.repository.RestaurantRepository
import com.naver.maps.map.NaverMap
import kotlinx.coroutines.launch
import java.net.URLEncoder


class MapViewModel(private val repository: RestaurantRepository, private val context: Context) : ViewModel() {
    fun setUpMarkers(naverMap: NaverMap){

    }

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
        val clip = ClipData.newPlainText("restaurant address", "서울시 관악구 관악로 1 서울대학교 " + restaurant.place)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "${restaurant.name} 주소를 클립보드에 복사했습니다.", Toast.LENGTH_LONG).show()
    }

    fun onFindRoadButtonClick(restaurant: Restaurant){
        Toast.makeText(context, "onFindRoadButtonClicked!", Toast.LENGTH_LONG).show()

        val findRoadIntent: Intent = Uri.parse("nmap://route/walk?dlat=${restaurant.latitude}&dlng=${restaurant.longitude}&dname=${URLEncoder.encode(restaurant.name, "UTF-8")}&appname=com.example.myapp").let { findRoad ->
            Intent(Intent.ACTION_VIEW, findRoad)
        }
        context.startActivity(findRoadIntent)
    }
}