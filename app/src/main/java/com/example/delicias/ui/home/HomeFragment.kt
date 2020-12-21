package com.example.delicias.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.delicias.R
import com.example.delicias.data.repository.RestaurantDataRepository
import com.example.delicias.data.repository.datasource.LocalRestaurantDataStore
import com.example.delicias.databinding.FragmentHomeBinding
import com.example.delicias.domain.Date
import com.example.delicias.domain.Restaurant
import com.example.delicias.domain.RestaurantMinimal
import com.example.delicias.ui.DateAdapter
import com.example.delicias.ui.MealTimeAdapter
import com.example.delicias.ui.RestaurantMinimalAdapter
import com.example.delicias.util.Util
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*


class HomeFragment : Fragment(), SearchView.OnQueryTextListener {
    lateinit var homeViewModel: HomeViewModel
    lateinit var restaurantMinimalAdapter: RestaurantMinimalAdapter
    var dateData = MutableLiveData<ArrayList<Date>>()
    var restaurantData = MutableLiveData<ArrayList<RestaurantMinimal>>()
    lateinit var binding: FragmentHomeBinding
    lateinit var lifecycleOwner: LifecycleOwner
    var currentMealTime = MealTime.BREAKFAST
    lateinit var restaurantDataRepository :RestaurantDataRepository
    val scope = CoroutineScope(Dispatchers.Default)

    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    enum class MealTime{
        BREAKFAST,
        LUNCH,
        DINNER
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        lifecycleOwner = viewLifecycleOwner
        val restaurantDao = LocalRestaurantDataStore.getInstance(this.requireContext()).restaurantDao()
        val restaurantMinimalDao = LocalRestaurantDataStore.getInstance(this.requireContext()).restaurantMinimalDao()

        restaurantDataRepository = RestaurantDataRepository(restaurantDao, restaurantMinimalDao)
        val factory = HomeViewModelFactory(restaurantDataRepository)
        homeViewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)

        insertAll()

        restaurantMinimalAdapter = RestaurantMinimalAdapter(this.requireContext(), homeViewModel)

        binding.svSearchRestaurant.setOnQueryTextListener(this)

        binding.vpMealTime.adapter = MealTimeAdapter(listOf("아침", "점심", "저녁"))
        binding.vpMealTime.registerOnPageChangeCallback(PageChangeCallback())

        binding.rvCafeteriaMenu.layoutManager = LinearLayoutManager(this.requireContext())
        binding.rvCafeteriaMenu.itemAnimator = DefaultItemAnimator()
        binding.rvCafeteriaMenu.adapter = restaurantMinimalAdapter

        Util.getDates().asLiveData().observe(this.viewLifecycleOwner, androidx.lifecycle.Observer {
            dateData.value = it
            binding.rvDate.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.rvDate.adapter = DateAdapter(dateData)
        })

        val cafeteriaSpinnerItems = arrayOf("사전순", "거리순")
        val cafeteriaSpinnerAdapter = ArrayAdapter(
            this.requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            cafeteriaSpinnerItems
        )
        binding.spnListingOrder1.adapter = cafeteriaSpinnerAdapter

        binding.spnListingOrder1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> {
                        when (currentMealTime) {
                            MealTime.BREAKFAST -> {
                                scope.launch {
                                    val breakfastList =
                                        restaurantDataRepository.getAllBreakfast().first()
                                    withContext(Dispatchers.Main) {
                                        sortByName(breakfastList)
                                    }
                                }
                            }
                            MealTime.LUNCH -> {
                                scope.launch {
                                    val lunchList = restaurantDataRepository.getAllLunch().first()
                                    withContext(Dispatchers.Main) {
                                        sortByName(lunchList)
                                    }
                                }
                            }
                            MealTime.DINNER -> {
                                scope.launch {
                                    val dinnerList = restaurantDataRepository.getAllDinner().first()
                                    withContext(Dispatchers.Main) {
                                        sortByName(dinnerList)
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        Log.d("delitag", Util.getLocation(requireContext()).toString())
                        when (currentMealTime) {
                            MealTime.BREAKFAST -> {
                                scope.launch {
                                    val breakfastList =
                                        restaurantDataRepository.getAllBreakfast().first()
                                    withContext(Dispatchers.Main){
                                        sortByLocation(breakfastList)
                                    }
                                }
                            }
                            MealTime.LUNCH -> {
                                scope.launch {
                                    val lunchList = restaurantDataRepository.getAllLunch().first()
                                    withContext(Dispatchers.Main){
                                        sortByLocation(lunchList)
                                    }
                                }
                            }
                            MealTime.DINNER -> {
                                scope.launch {
                                    val dinnerList = restaurantDataRepository.getAllDinner().first()
                                    withContext(Dispatchers.Main){
                                        sortByLocation(dinnerList)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        runBlocking {
            val breakfastList = restaurantDataRepository.getAllBreakfast().first()
            restaurantDataRepository.updateRestaurantMinimals(breakfastList)
        }

        homeViewModel.getAllRestaurantMinimal().observe(
            lifecycleOwner,
            androidx.lifecycle.Observer {
                restaurantMinimalAdapter.submitList(it)
            })

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting()
        } else {
            checkRunTimePermission()
        }

        return binding.root
    }

    private inner class PageChangeCallback: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
             when (position) {
                 0 -> {
                     currentMealTime = MealTime.BREAKFAST
                     scope.launch {
                         val breakfastList = restaurantDataRepository.getAllBreakfast().first()
                         when (binding.spnListingOrder1.selectedItemPosition) {
                             0 -> {
                                 withContext(Dispatchers.Main) {
                                     sortByName(breakfastList)
                                 }
                             }
                             1 -> {
                                 withContext(Dispatchers.Main) {
                                     sortByLocation(breakfastList)
                                 }
                             }
                         }
                     }
                 }
                 1 -> {
                     currentMealTime = MealTime.LUNCH
                     scope.launch {
                         val lunchList = restaurantDataRepository.getAllLunch().first()
                         when (binding.spnListingOrder1.selectedItemPosition) {
                             0 -> {
                                 withContext(Dispatchers.Main) {
                                     sortByName(lunchList)
                                 }
                             }
                             1 -> {
                                 withContext(Dispatchers.Main) {
                                     sortByLocation(lunchList)
                                 }
                             }
                         }
                     }
                 }
                 2 -> {
                     currentMealTime = MealTime.DINNER
                     scope.launch {
                         val dinnerList = restaurantDataRepository.getAllDinner().first()
                         when (binding.spnListingOrder1.selectedItemPosition) {
                             0 -> {
                                 withContext(Dispatchers.Main) {
                                     sortByName(dinnerList)
                                 }
                             }
                             1 -> {
                                 withContext(Dispatchers.Main) {
                                     sortByLocation(dinnerList)
                                 }
                             }
                         }
                     }
                 }
                else -> error("no such position: $position")
            }
        }
    }

    fun insertAll(){
        scope.launch {
            homeViewModel.getRestaurantsFromRemote()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            getResults(query)
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            getResults(newText)
        }
        return false
    }

    private fun getResults(newText: String) {
        val queryText = "%$newText%"

        when (currentMealTime){
            MealTime.BREAKFAST -> {
                scope.launch {
                    val searchedBreakfastList = restaurantDataRepository.searchForBreakfast(
                        queryText
                    ).first()
                    restaurantDataRepository.updateRestaurantMinimals(searchedBreakfastList)
                }
            }
            MealTime.LUNCH -> {
                scope.launch {
                    val searchedLunchList =
                        restaurantDataRepository.searchForLunch(queryText).first()
                    restaurantDataRepository.updateRestaurantMinimals(searchedLunchList)
                }
            }
            MealTime.DINNER -> {
                scope.launch {
                    val searchedDinnerList =
                        restaurantDataRepository.searchForDinner(queryText).first()
                    restaurantDataRepository.updateRestaurantMinimals(searchedDinnerList)
                }
            }
        }
    }

    private fun sortByName(restaurantMinimals: List<RestaurantMinimal>) {
        Util.sortByName(restaurantMinimals)
        restaurantMinimalAdapter.submitList(restaurantMinimals)
    }

    private fun sortByLocation(restaurantMinimals: List<RestaurantMinimal>) {
        Collections.sort(restaurantMinimals, kotlin.Comparator { left, right ->
            val leftRestaurant: Restaurant
            val rightRestaurant: Restaurant
            val currentLocation = Util.getLocation(requireContext())

            runBlocking {
                leftRestaurant = restaurantDataRepository.getRestaurantById(left.id).first()
                rightRestaurant = restaurantDataRepository.getRestaurantById(right.id).first()
            }

            if (Util.calculateRestaurantDistance
                    (currentLocation, leftRestaurant.latitude, leftRestaurant.longitude) >
                Util.calculateRestaurantDistance
                    (currentLocation, rightRestaurant.latitude, rightRestaurant.longitude))
                return@Comparator 1
            else if (Util.calculateRestaurantDistance
                    (currentLocation, leftRestaurant.latitude, leftRestaurant.longitude) <
                Util.calculateRestaurantDistance
                    (currentLocation, rightRestaurant.latitude, rightRestaurant.longitude))
                return@Comparator -1
            else return@Comparator 0
        })

        restaurantMinimalAdapter.submitList(restaurantMinimals)
    }

    override fun onRequestPermissionsResult(
        permsRequestCode: Int,
        permissions: Array<String>,
        grandResults: IntArray
    ) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.size == REQUIRED_PERMISSIONS.size) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            var check_result = true

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (result in grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            if (check_result) {
                //위치 값을 가져올 수 있음
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this.requireActivity(),
                        REQUIRED_PERMISSIONS[0]
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        this.requireActivity(),
                        REQUIRED_PERMISSIONS[1]
                    )
                ) {
                    Toast.makeText(
                        this.requireContext(),
                        "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this.requireContext(),
                        "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE ->
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    Log.d("delitag", "onActivityResult : GPS 활성화 되있음")
                    checkRunTimePermission()
                    return
                }
        }
    }

    private fun checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this.requireActivity(),
                    REQUIRED_PERMISSIONS[0]
                )
            ) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(
                    this.requireContext(),
                    "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                    Toast.LENGTH_LONG
                ).show()
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this.requireActivity(), REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this.requireActivity(), REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    private fun showDialogForLocationServiceSetting() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            """
            앱을 사용하기 위해서는 위치 서비스가 필요합니다.
            위치 설정을 수정하실래요?
            """.trimIndent()
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        }
        builder.setNegativeButton("취소") { dialog, id ->
            dialog.cancel()
        }
        builder.create().show()
    }

    private fun checkLocationServicesStatus(): Boolean {
        val locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager?
        return (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }
}