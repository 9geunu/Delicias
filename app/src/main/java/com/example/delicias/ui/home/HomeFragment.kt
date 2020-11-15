package com.example.delicias.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
import com.example.delicias.domain.RestaurantMinimal
import com.example.delicias.ui.DateAdapter
import com.example.delicias.ui.MealTimeAdapter
import com.example.delicias.ui.RestaurantMinimalAdapter
import com.example.delicias.util.Util
import kotlinx.coroutines.runBlocking

class HomeFragment : Fragment(), SearchView.OnQueryTextListener {
    lateinit var homeViewModel: HomeViewModel
    lateinit var restaurantMinimalAdapter: RestaurantMinimalAdapter
    var dateData = MutableLiveData<ArrayList<Date>>()
    var restaurantData = MutableLiveData<ArrayList<RestaurantMinimal>>()
    lateinit var binding: FragmentHomeBinding
    lateinit var lifecycleOwner: LifecycleOwner
    var currentMealTime = MealTime.BREAKFAST

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

        val factory = HomeViewModelFactory(RestaurantDataRepository(restaurantDao))
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
                    }
                    1 -> {
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        return binding.root
    }

    private inner class PageChangeCallback: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
             when (position) {
                0 -> {
                    currentMealTime = MealTime.BREAKFAST
                    homeViewModel.getAllBreakfast().observe(lifecycleOwner, androidx.lifecycle.Observer {
                        restaurantMinimalAdapter.submitList(it)
                    })
                }
                1 -> {
                    currentMealTime = MealTime.LUNCH
                    homeViewModel.getAllLunch().observe(lifecycleOwner, androidx.lifecycle.Observer {
                        restaurantMinimalAdapter.submitList(it)
                    })
                }
                2 -> {
                    currentMealTime = MealTime.DINNER
                    homeViewModel.getAllDinner().observe(lifecycleOwner, androidx.lifecycle.Observer {
                        restaurantMinimalAdapter.submitList(it)
                    })
                }
                else -> error("no such position: $position")
            }
        }
    }

    fun insertAll(){
        runBlocking {
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
                homeViewModel.searchForBreakfast(queryText).observe(lifecycleOwner, Observer {
                    if (it != null)
                        restaurantMinimalAdapter.submitList(it)
                })
            }
            MealTime.LUNCH -> {
                homeViewModel.searchForLunch(queryText).observe(lifecycleOwner, Observer {
                    if (it != null)
                        restaurantMinimalAdapter.submitList(it)
                })
            }
            MealTime.DINNER -> {
                homeViewModel.searchForDinner(queryText).observe(lifecycleOwner, Observer {
                    if (it != null)
                        restaurantMinimalAdapter.submitList(it)
                })
            }
        }
    }
}