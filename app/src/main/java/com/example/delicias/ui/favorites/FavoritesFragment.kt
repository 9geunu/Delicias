package com.example.delicias.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.example.delicias.databinding.FragmentFavoritesBinding
import com.example.delicias.domain.Date
import com.example.delicias.ui.DateAdapter
import com.example.delicias.ui.MealTimeAdapter
import com.example.delicias.ui.RestaurantMinimalAdapter
import com.example.delicias.util.Util

class FavoritesFragment : Fragment() {
    lateinit var favoritesViewModel: FavoritesViewModel
    lateinit var binding: FragmentFavoritesBinding
    var dateData = MutableLiveData<ArrayList<Date>>()
    lateinit var restaurantMinimalAdapter: RestaurantMinimalAdapter
    lateinit var lifecycleOwner: LifecycleOwner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites, container, false)
        lifecycleOwner = viewLifecycleOwner
        val restaurantDao = LocalRestaurantDataStore.getInstance(this.requireContext()).restaurantDao()

        val factory = FavoritesViewModelFactory(RestaurantDataRepository(restaurantDao))
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
                    favoritesViewModel.getAllFavoriteBreakfast().observe(lifecycleOwner, androidx.lifecycle.Observer {
                        restaurantMinimalAdapter.submitList(it)
                    })
                }
                1 -> {
                    favoritesViewModel.getAllFavoriteLunch().observe(lifecycleOwner, androidx.lifecycle.Observer {
                        restaurantMinimalAdapter.submitList(it)
                    })
                }
                2 -> {
                    favoritesViewModel.getAllFavoriteDinner().observe(lifecycleOwner, androidx.lifecycle.Observer {
                        restaurantMinimalAdapter.submitList(it)
                    })
                }
                else -> error("no such position: $position")
            }
        }
    }
}