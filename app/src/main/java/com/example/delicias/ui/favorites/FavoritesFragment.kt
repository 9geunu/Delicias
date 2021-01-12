package com.example.delicias.ui.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.delicias.R
import com.example.delicias.data.repository.RestaurantDataRepository
import com.example.delicias.data.repository.datasource.LocalRestaurantDataStore
import com.example.delicias.databinding.FragmentFavoritesBinding
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
import kotlin.collections.ArrayList

class FavoritesFragment : Fragment() {
    lateinit var favoritesViewModel: FavoritesViewModel
    lateinit var binding: FragmentFavoritesBinding
    var dateData = MutableLiveData<ArrayList<Date>>()
    lateinit var restaurantMinimalAdapter: RestaurantMinimalAdapter
    lateinit var lifecycleOwner: LifecycleOwner
    lateinit var restaurantDataRepository :RestaurantDataRepository
    var currentMealTime = MealTime.NOT_SET
    lateinit var restaurantMinimalLiveData: LiveData<List<RestaurantMinimal>>
    val scope = CoroutineScope(Dispatchers.Default)
    val spinnerItemLiveData = MutableLiveData<Int>(0)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites, container, false)
        lifecycleOwner = viewLifecycleOwner

        restaurantDataRepository = RestaurantDataRepository(requireContext())
        val factory = FavoritesViewModelFactory(restaurantDataRepository)
        favoritesViewModel = ViewModelProviders.of(this, factory).get(FavoritesViewModel::class.java)

        restaurantMinimalAdapter = RestaurantMinimalAdapter(this.requireContext(), favoritesViewModel)

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

        val cafeteriaSpinnerItems = arrayOf("사전순","거리순")
        val cafeteriaSpinnerAdapter = ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_dropdown_item, cafeteriaSpinnerItems)
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
                    }
                }
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
            when (it) {
                0 -> favoritesViewModel.getFavoriteRestaurantMinimalOrderByName()
                1 -> favoritesViewModel.getFavoriteRestaurantMinimalOrderByDistance()
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
                favoritesViewModel.updateBreakfast()
            }
            MealTime.LUNCH -> {
                favoritesViewModel.updateLunch()
            }
            MealTime.DINNER -> {
                favoritesViewModel.updateDinner()
            }
            MealTime.NOT_SET -> {
                return
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
                        favoritesViewModel.updateBreakfast()
                    }
                }
                1 -> {
                    currentMealTime = MealTime.LUNCH
                    scope.launch {
                        favoritesViewModel.updateLunch()
                    }
                }
                2 -> {
                    currentMealTime = MealTime.DINNER
                    scope.launch {
                        favoritesViewModel.updateDinner()
                    }
                }
                else -> error("no such position: $position")
            }
        }
    }
}