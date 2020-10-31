package com.example.delicias.ui

import android.app.Application
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.NaverMapSdk.NaverCloudPlatformClient


class MainApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        NaverMapSdk.getInstance(this).client = NaverCloudPlatformClient("5s5dn4qedb")
    }
}