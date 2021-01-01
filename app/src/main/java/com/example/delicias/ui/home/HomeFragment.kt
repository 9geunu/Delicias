package com.example.delicias.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.delicias.R
import com.example.delicias.data.repository.RestaurantDataRepository
import com.example.delicias.data.repository.datasource.LocalRestaurantDataStore
import com.example.delicias.databinding.FragmentHomeBinding
import com.example.delicias.domain.Date
import com.example.delicias.domain.MealTime
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
    lateinit var restaurantMinimalLiveData: LiveData<List<RestaurantMinimal>>
    lateinit var binding: FragmentHomeBinding
    lateinit var lifecycleOwner: LifecycleOwner
    var currentMealTime = MealTime.BREAKFAST
    lateinit var restaurantDataRepository :RestaurantDataRepository
    val scope = CoroutineScope(Dispatchers.Default)
    val spinnerItemLiveData = MutableLiveData<Int>(0)

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
                if (position == 1){
                    runBlocking {
                        Util.sortByLocation(requireContext(), restaurantDataRepository)
                        refresh()
                        spinnerItemLiveData.postValue(position)
                    }
                }
                else
                    spinnerItemLiveData.postValue(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.srlRefresh.setOnRefreshListener {
            binding.srlRefresh.isRefreshing = true
            runBlocking {
                refresh()
                binding.srlRefresh.isRefreshing = false
            }
        }

        restaurantMinimalLiveData = Transformations.switchMap(spinnerItemLiveData) {
            when(it) {
                0 -> homeViewModel.getRestaurantMinimalOrderByName()
                1 -> homeViewModel.getRestaurantMinimalOrderByDistance()
                else -> error("No Such Position $it")
            }
        }

        restaurantMinimalLiveData.observe(
            lifecycleOwner,
            androidx.lifecycle.Observer {
                restaurantMinimalAdapter.submitList(it)
            })

        return binding.root
    }

    suspend fun refresh() {
        when (currentMealTime) {
            MealTime.BREAKFAST -> {
                homeViewModel.updateBreakfast()
            }
            MealTime.LUNCH -> {
                homeViewModel.updateLunch()
            }
            MealTime.DINNER -> {
                homeViewModel.updateDinner()
            }
        }
    }

    private inner class PageChangeCallback: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
             when (position) {
                 0 -> {
                     currentMealTime = MealTime.BREAKFAST
                     scope.launch {
                         homeViewModel.updateBreakfast()
                     }
                 }
                 1 -> {
                     currentMealTime = MealTime.LUNCH
                     scope.launch {
                         homeViewModel.updateLunch()
                     }
                 }
                 2 -> {
                     currentMealTime = MealTime.DINNER
                     scope.launch {
                         homeViewModel.updateDinner()
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
                    val searchedBreakfastList = restaurantDataRepository.searchForBreakfast(queryText).first()
                    restaurantDataRepository.updateRestaurantMinimals(searchedBreakfastList)
                }
            }
            MealTime.LUNCH -> {
                scope.launch {
                    val searchedLunchList = restaurantDataRepository.searchForLunch(queryText).first()
                    restaurantDataRepository.updateRestaurantMinimals(searchedLunchList)
                }
            }
            MealTime.DINNER -> {
                scope.launch {
                    val searchedDinnerList = restaurantDataRepository.searchForDinner(queryText).first()
                    restaurantDataRepository.updateRestaurantMinimals(searchedDinnerList)
                }
            }
        }
    }


}