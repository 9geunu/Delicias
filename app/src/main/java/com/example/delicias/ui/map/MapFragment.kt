package com.example.delicias.ui.map

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.delicias.R
import com.example.delicias.data.repository.RestaurantDataRepository
import com.example.delicias.databinding.FragmentMapBinding
import com.example.delicias.domain.Menu
import com.example.delicias.domain.Restaurant
import com.example.delicias.ui.AnimationOnClickListener
import com.example.delicias.ui.MenuAdapter
import com.example.delicias.ui.SearchActivity
import com.example.delicias.util.Constants
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
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
    private val pinDetailImage = OverlayImage.fromResource(R.drawable.pin_detail)
    private var markers = mutableListOf<Marker>()
    private lateinit var animatorListener: AnimationOnClickListener
    private lateinit var ivSearchRestaurant: ImageView
    private val selectedRestaurantId = MutableLiveData<Long>(0)
    private lateinit var isSelectedRestaurantFavorite: LiveData<Boolean>
    private val scope = CoroutineScope(Dispatchers.IO)
    private val listener = Overlay.OnClickListener { overlay ->
        val marker = overlay as Marker
        Log.d("delitag", "setMarkers: ${marker.tag} 클릭됨")

        scope.launch{
            val restaurant = restaurantDataRepository.getRestaurantById(marker.tag as Long).first()
            withContext(Dispatchers.Main) {
                selectedRestaurantId.value = restaurant.id
                binding.restaurant = restaurant
                binding.rvMenuItem.layoutManager = LinearLayoutManager(context)
                binding.rvMenuItem.adapter = MenuAdapter(restaurant.getCurrentMeal())
            }
        }
        if (binding.cvRestaurantInfo.isVisible) {
            mapViewModel.setRestaurantInfoInVisible()
            marker.icon = pinImage
            moveDefaultLocationButtonToBottomOfMap()
        }
        else if (animatorListener.isRestaurantDetailInfoShown) {
            marker.icon = pinImage
            animatorListener.onMapClick()
        }
        else {
            moveDefaultLocationButtonToTopOfInfoCard()
            mapViewModel.setRestaurantInfoVisible()
            marker.icon = pinDetailImage
        }

        markers.filter { otherMarker ->
            otherMarker.tag != marker.tag && otherMarker.icon == pinDetailImage
        }.forEach { otherMarker ->
            otherMarker.icon = pinImage
        }
        true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)

        restaurantDataRepository = RestaurantDataRepository(requireContext())
        val factory = MapViewModelFactory(restaurantDataRepository, requireContext())
        mapViewModel = ViewModelProviders.of(this, factory).get(MapViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = mapViewModel

        binding.btnDefaultLocation.setOnClickListener {
            val cameraUpdate = CameraUpdate.scrollTo(LatLng(Constants.DEFAULT_LAT, Constants.DEFAULT_LNG))
                .animate(CameraAnimation.Easing)
            naverMap.moveCamera(cameraUpdate)
        }

        isSelectedRestaurantFavorite = Transformations.switchMap(selectedRestaurantId) { id ->
            restaurantDataRepository.getIsFavoriteOfRestaurant(id).asLiveData()
        }

        isSelectedRestaurantFavorite.observe(viewLifecycleOwner, androidx.lifecycle.Observer { isFavorite ->
            binding.btnFavorite.isChecked = isFavorite?: false
        })

        val mapTitle: View = binding.mapTitle
        ivSearchRestaurant = (mapTitle as ConstraintLayout).getViewById(R.id.iv_search_restaurant) as ImageView
        ivSearchRestaurant.setOnClickListener {
            initializeMarkerIcons()

            if (binding.cvRestaurantInfo.isVisible) {
                mapViewModel.setRestaurantInfoInVisible()
                moveDefaultLocationButtonToBottomOfMap()
            }

            val intent = Intent(activity, SearchActivity::class.java)

            activity?.startActivityForResult(intent, Constants.REQUEST_SEARCH_RESULT)
        }

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

        animatorListener = AnimationOnClickListener(requireContext(), binding.cvRestaurantInfo, binding.map, naverMap, binding)
        binding.cvRestaurantInfo.setOnClickListener(
            animatorListener
        )

        executorService.submit {
            setNaverMapOnClickListener(naverMap)
            setMarkers(naverMap)
            isMapReady = true
        }
    }

    private fun setMarkers(naverMap: NaverMap) {
        if (markers.isNotEmpty())
            markers.clear()

        if (markers.isEmpty()) {
            scope.launch {
                restaurantDataRepository.getAllRestaurants().first().forEach { restaurant ->
                    markers.add(Marker().apply {
                        tag = restaurant.id
                        icon = pinImage
                        position = LatLng(restaurant.latitude, restaurant.longitude)
                        onClickListener = listener
                    })
                }

                withContext(Dispatchers.Main) {
                    markers.forEach { marker ->
                        marker.map = naverMap
                    }
                }
            }
        }
    }

    private fun setNaverMapOnClickListener(naverMap: NaverMap) {
        naverMap.setOnMapClickListener { pointF, latLng ->
            if (binding.cvRestaurantInfo.isVisible) {
                mapViewModel.setRestaurantInfoInVisible()
                moveDefaultLocationButtonToBottomOfMap()
            }

            initializeMarkerIcons()

            animatorListener.onMapClick()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    fun onBackPress(): Boolean{
        initializeMarkerIcons()

        return if (binding.cvRestaurantInfo.isVisible) {
            moveDefaultLocationButtonToBottomOfMap()
            mapViewModel.setRestaurantInfoInVisible()
            false
        } else if(animatorListener.isRestaurantDetailInfoShown){
            animatorListener.onMapClick()
            false
        }
        else true
    }

    private fun initializeMarkerIcons() {
        markers.filter { marker ->
            marker.icon == pinDetailImage
        }.forEach { marker ->
            marker.icon = pinImage
        }
    }

    private fun Restaurant.getCurrentMeal(): List<Menu>? {
        val calendar: Calendar = Calendar.getInstance()
        val hourOfDay: Int = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hourOfDay) {
            in 0..9 -> this.breakfast?.menus
            in 10..14 -> this.lunch?.menus
            in 15..23 -> this.dinner?.menus
            else -> error("No Such Hour $hourOfDay")
        }
    }

    fun goToDestination(restaurantName: String?) {
        if (restaurantName != null) {
            scope.launch {
                restaurantDataRepository.getAllRestaurants().first().filter {
                    it.name == restaurantName
                }.forEach {
                    val restaurant = restaurantDataRepository.getRestaurantById(it.id).first()
                    val cameraUpdate = CameraUpdate.scrollTo(LatLng(restaurant.latitude, restaurant.longitude))
                        .animate(CameraAnimation.Easing)
                    naverMap.moveCamera(cameraUpdate)
                }
            }
        }
    }

    private fun moveDefaultLocationButtonToBottomOfMap() {
        binding.btnDefaultLocation.translationY = 0F
    }

    private fun moveDefaultLocationButtonToTopOfInfoCard() {
        binding.btnDefaultLocation.translationY = -350F
    }
}