package com.example.delicias.ui.map

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.delicias.R
import com.example.delicias.data.repository.RestaurantDataRepository
import com.example.delicias.data.repository.datasource.LocalRestaurantDataStore
import com.example.delicias.databinding.FragmentMapBinding
import com.example.delicias.ui.AnimationOnClickListener
import com.example.delicias.ui.MenuAdapter
import com.example.delicias.ui.RestaurantSearchAdapter
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

class MapFragment : Fragment(), OnMapReadyCallback, SearchView.OnQueryTextListener {
    lateinit var binding: FragmentMapBinding
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    lateinit var mapViewModel: MapViewModel
    lateinit var restaurantDataRepository: RestaurantDataRepository
    lateinit var restaurantSearchAdapter: RestaurantSearchAdapter
    private val executorService = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private var isMapReady = false
    private val pinImage = OverlayImage.fromResource(R.drawable.pin)
    private val pinDetailImage = OverlayImage.fromResource(R.drawable.pin_detail)
    private var markers = mutableListOf<Marker>()
    private lateinit var animatorListener: AnimationOnClickListener
    private val listener = Overlay.OnClickListener { overlay ->
        val marker = overlay as Marker
        Log.d("delitag", "setMarkers: ${marker.tag} 클릭됨")
        runBlocking{
            val restaurant = restaurantDataRepository.getRestaurantById(marker.tag as Long).first()
            binding.restaurant = restaurant
            binding.rvMenuItem.layoutManager = LinearLayoutManager(context)
            binding.rvMenuItem.adapter = MenuAdapter(restaurant.lunch.menus)
            MenuAdapter(restaurant.lunch.menus)
        }
        if (binding.cvRestaurantInfo.isVisible) {
            mapViewModel.setRestaurantInfoInVisible()
            marker.icon = pinImage
        }
        mapViewModel.setRestaurantInfoVisible()

        marker.icon = pinDetailImage

        markers.filter { otherMarker ->
            otherMarker.tag != marker.tag && otherMarker.icon == pinDetailImage
        }.forEach { otherMarker ->
            otherMarker.icon = pinImage
        }
        true
    }
    private val onCloseListener = SearchView.OnCloseListener {
        mapViewModel.setRestaurantSearchResultInvisible()
        false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        val restaurantDao = LocalRestaurantDataStore.getInstance(this.requireContext()).restaurantDao()
        val restaurantMinimalDao = LocalRestaurantDataStore.getInstance(this.requireContext()).restaurantMinimalDao()

        restaurantDataRepository = RestaurantDataRepository(restaurantDao, restaurantMinimalDao)
        val factory = MapViewModelFactory(restaurantDataRepository, requireContext())
        mapViewModel = ViewModelProviders.of(this, factory).get(MapViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = mapViewModel
        binding.svSearchRestaurant.setOnQueryTextListener(this)

        binding.svSearchRestaurant.setOnSearchClickListener {
            mapViewModel.setRestaurantSearchResultVisible()
        }

        binding.svSearchRestaurant.setOnCloseListener(onCloseListener)

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions,
                grantResults
            )) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(naverMap: NaverMap) {
        Log.d("delitag", "onMapReady!")
        this.naverMap = naverMap
        this.naverMap.locationSource = locationSource
        this.naverMap.mapType = NaverMap.MapType.Basic
        this.naverMap.minZoom = 13.9
        this.naverMap.maxZoom = 19.0
        this.naverMap.isIndoorEnabled = true
        this.naverMap.extent = LatLngBounds(
            LatLng(37.446082, 126.947008), LatLng(
                37.469290,
                126.962146
            )
        )
        val uiSettings = this.naverMap.uiSettings
        uiSettings.isLocationButtonEnabled = false
        uiSettings.isZoomControlEnabled = false

        restaurantSearchAdapter = RestaurantSearchAdapter(naverMap, mapViewModel)

        animatorListener = AnimationOnClickListener(requireContext(), binding.clRestaurantInfo, binding.map, naverMap)
        binding.clRestaurantInfo.setOnClickListener(
            animatorListener
        )

        binding.rvSearchRestaurantList.layoutManager = LinearLayoutManager(this.requireContext())
        binding.rvSearchRestaurantList.itemAnimator = DefaultItemAnimator()
        binding.rvSearchRestaurantList.adapter = restaurantSearchAdapter

        executorService.submit {
            setNaverMapOnClickListener(naverMap)
            setMarkers(naverMap)
            isMapReady = true
        }
    }

    private fun setMarkers(naverMap: NaverMap) {
        Log.d("delitag", "setMarkers")

        if (markers.isNotEmpty())
            markers.clear()

        if (markers.isEmpty()) {
            runBlocking {
                restaurantDataRepository.getAllRestaurants().first().forEach { restaurant ->
                    Log.d("delitag", restaurant.toString())
                    markers.add(Marker().apply {
                        tag = restaurant.id
                        icon = pinImage
                        position = LatLng(restaurant.latitude, restaurant.longitude)
                        onClickListener = listener
                    })
                }
            }
        }

        handler.post {
            markers.forEach { marker ->
                marker.map = naverMap
            }
        }
    }

    private fun setNaverMapOnClickListener(naverMap: NaverMap) {
        naverMap.setOnMapClickListener { pointF, latLng ->
            if (binding.cvRestaurantInfo.isVisible)
                mapViewModel.setRestaurantInfoInVisible()

            if (mapViewModel.mapSizePercentage.value == 40F)
                mapViewModel.setRestaurantDetailInfoInvisible()

            onCloseListener.onClose()

            markers.filter { marker ->
                marker.icon == pinDetailImage
            }.forEach { marker ->
                marker.icon = pinImage
            }

            animatorListener.onMapClick()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    fun onBackPress(): Boolean{
        markers.filter { marker ->
            marker.icon == pinDetailImage
        }.forEach { marker ->
            marker.icon = pinImage
        }

        animatorListener.onMapClick()

        return if (binding.cvRestaurantInfo.isVisible) {
            mapViewModel.setRestaurantInfoInVisible()
            false
        } else if (mapViewModel.mapSizePercentage.value == 40F) {
            mapViewModel.setRestaurantDetailInfoInvisible()
            false
        } else true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query.isNullOrEmpty())
            mapViewModel.setRestaurantSearchResultInvisible()

        if (query != null) {
            getResults(query)
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty())
            mapViewModel.setRestaurantSearchResultInvisible()

        if (newText != null) {
            getResults(newText)
        }
        return false
    }

    private fun getResults(newText: String) {
        if (!binding.svSearchRestaurant.isVisible)
            mapViewModel.setRestaurantSearchResultVisible()

        val queryText = "%$newText%"
        mapViewModel.searchRestaurant(queryText).observe(viewLifecycleOwner, Observer {
            if (it != null)
                restaurantSearchAdapter.submitList(it)
        })
    }
}