package com.example.delicias.ui.map

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delicias.R
import com.example.delicias.domain.Restaurant
import com.example.delicias.domain.repository.RestaurantRepository
import kotlinx.coroutines.launch
import java.net.URLEncoder


class MapViewModel(private val repository: RestaurantRepository, private val context: Context) : ViewModel() {
    var isRestaurantInfoCardViewVisible = MutableLiveData<Boolean>()

    fun onFavoriteButtonClick(restaurant: Restaurant){
        viewModelScope.launch {
            repository.toggleIsFavoriteOfRestaurantById(restaurant.id)
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

    @Throws(Exception::class)
    fun onFindRoadOkButtonClick(restaurant: Restaurant) {
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

        context.startActivity(findRoadIntent)
    }

    fun setNMapAppDeeplinkCardViewVisible(restaurant: Restaurant){
        val builder = AlertDialog.Builder(context)

        val inflater = (context as Activity).layoutInflater

        val view = inflater.inflate(R.layout.item_dialog, null)

        val dialog = builder.setView(view)
            .setTitle("길 찾기")
            .setMessage("네이버 지도 앱으로 이동합니다.")
            .create()

        val okButton = view.findViewById<Button>(R.id.btn_positive)

        okButton.setOnClickListener {
            try {
                onFindRoadOkButtonClick(restaurant)
            } catch (e: Exception){
                Toast.makeText(context, "네이버 지도 앱이 설치되지 않았습니다.", Toast.LENGTH_LONG).show()
                dialog.cancel()
            }
        }

        val cancelButton = view.findViewById<Button>(R.id.btn_negative)
        cancelButton.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()
    }

    fun setRestaurantInfoVisible(){
        isRestaurantInfoCardViewVisible.value = true
    }

    fun setRestaurantInfoInVisible(){
        isRestaurantInfoCardViewVisible.value = false
    }
}