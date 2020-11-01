package com.example.delicias.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.delicias.R
import com.example.delicias.databinding.FragmentMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.LocationButtonView

class MapFragment : Fragment(), OnMapReadyCallback {
    lateinit var binding: FragmentMapBinding
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as com.naver.maps.map.MapFragment?
            ?: com.naver.maps.map.MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(naverMap: NaverMap) {
        Log.d("delishas", "onMapReady!")
        this.naverMap = naverMap
        this.naverMap.locationSource = locationSource
        this.naverMap.mapType = NaverMap.MapType.Basic
        this.naverMap.minZoom = 7.0
        this.naverMap.maxZoom = 19.0
        this.naverMap.isIndoorEnabled = true
        this.naverMap.extent = LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0))
        val uiSettings = this.naverMap.uiSettings
        uiSettings.isLocationButtonEnabled = true
        uiSettings.isZoomControlEnabled = false

        setMarkers(naverMap)
    }

    private fun setMarkers(naverMap: NaverMap) {
        val studentsHallMarker = Marker()
        studentsHallMarker.icon = OverlayImage.fromResource(R.drawable.pin)
        studentsHallMarker.position = LatLng(37.459266, 126.950599)
        studentsHallMarker.map = naverMap

        val ourHomeMarker = Marker()
        ourHomeMarker.icon = OverlayImage.fromResource(R.drawable.pin)
        ourHomeMarker.position = LatLng(37.462069, 126.957797)
        ourHomeMarker.map = naverMap

        val threeZeroOneMarker = Marker()
        threeZeroOneMarker.icon = OverlayImage.fromResource(R.drawable.pin)
        threeZeroOneMarker.position = LatLng(37.450286, 126.952648)
        threeZeroOneMarker.map = naverMap

        val thirdRestaurantMarker = Marker()
        thirdRestaurantMarker.icon = OverlayImage.fromResource(R.drawable.pin)
        thirdRestaurantMarker.position = LatLng(37.456067, 126.948640)
        thirdRestaurantMarker.map = naverMap
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

}