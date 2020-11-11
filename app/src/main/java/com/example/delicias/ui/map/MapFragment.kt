package com.example.delicias.ui.map

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.delicias.R
import com.example.delicias.data.repository.RestaurantDataRepository
import com.example.delicias.data.repository.datasource.LocalRestaurantDataStore
import com.example.delicias.databinding.FragmentMapBinding
import com.example.delicias.domain.Restaurant
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

class MapFragment : Fragment(), OnMapReadyCallback {
    lateinit var binding: FragmentMapBinding
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    lateinit var mapViewModel: MapViewModel
    lateinit var restaurantDataRepository: RestaurantDataRepository
    private val executorService = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private var isMapReady = false
    private val pinImage = OverlayImage.fromResource(R.drawable.pin)
    private val listener = Overlay.OnClickListener { overlay ->
        Log.d("delitag", "setMarkers: ${overlay.tag} 클릭됨")
        runBlocking{
            binding.restaurant = restaurantDataRepository.getRestaurantById(overlay.tag as Long).first()
        }
        if (binding.cvRestaurantInfo.isVisible) {
            binding.cvRestaurantInfo.visibility = View.INVISIBLE
        } else
            binding.cvRestaurantInfo.visibility = View.VISIBLE
        true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        val restaurantDao = LocalRestaurantDataStore.getInstance(this.requireContext()).restaurantDao()

        restaurantDataRepository = RestaurantDataRepository(restaurantDao)
        val factory = MapViewModelFactory(restaurantDataRepository, requireContext())
        mapViewModel = ViewModelProviders.of(this, factory).get(MapViewModel::class.java)

        binding.viewModel = mapViewModel;

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

    override fun onResume() {
        super.onResume()
        if (isMapReady) {
            executorService.submit {
                setMarkers(naverMap)
            }
        }
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

        executorService.submit {
            setMarkers(naverMap)
            isMapReady = true
        }
    }

    private fun setMarkers(naverMap: NaverMap) {
        naverMap.setOnMapClickListener { pointF, latLng ->
            if (binding.cvRestaurantInfo.isVisible)
                binding.cvRestaurantInfo.visibility = View.INVISIBLE
        }

        val studentsHallMarker = Marker()
        studentsHallMarker.tag = 1.toLong()
        studentsHallMarker.icon = pinImage
        studentsHallMarker.position = LatLng(37.459266, 126.950599)
        studentsHallMarker.onClickListener = listener

        val thirdRestaurantMarker = Marker()
        thirdRestaurantMarker.tag = 2.toLong()
        thirdRestaurantMarker.icon = pinImage
        thirdRestaurantMarker.position = LatLng(37.456067, 126.948640)
        thirdRestaurantMarker.onClickListener = listener

        val ourHomeMarker = Marker()
        ourHomeMarker.tag = 3.toLong()
        ourHomeMarker.icon = pinImage
        ourHomeMarker.position = LatLng(37.462069, 126.957797)
        ourHomeMarker.onClickListener = listener

        val threeZeroOneMarker = Marker()
        threeZeroOneMarker.tag = 4.toLong()
        threeZeroOneMarker.icon = pinImage
        threeZeroOneMarker.position = LatLng(37.450286, 126.952648)
        threeZeroOneMarker.onClickListener = listener

        handler.post {
            studentsHallMarker.map = naverMap
            thirdRestaurantMarker.map = naverMap
            ourHomeMarker.map = naverMap
            threeZeroOneMarker.map = naverMap
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

}