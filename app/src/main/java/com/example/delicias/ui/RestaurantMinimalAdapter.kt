package com.example.delicias.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ToggleButton
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.delicias.R
import com.example.delicias.data.repository.datasource.LocalRestaurantDataStore
import com.example.delicias.domain.RestaurantMinimal
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RestaurantMinimalAdapter(val context: Context, val viewModel: ViewModel) :
    ListAdapter<RestaurantMinimal, RestaurantMinimalAdapter.ViewHolder>(diffUtil) {
    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<RestaurantMinimal>() {
            override fun areContentsTheSame(oldItem: RestaurantMinimal, newItem: RestaurantMinimal) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: RestaurantMinimal, newItem: RestaurantMinimal) =
                oldItem.name == newItem.name
        }
    }
    private val restaurantDao = LocalRestaurantDataStore.getInstance(context.applicationContext).restaurantDao()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var itemView = inflater.inflate(R.layout.item_cafeteria, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurantMinimal = getItem(position)

        with(holder) {
            name.text = restaurantMinimal.name
            menus.layoutManager = LinearLayoutManager(context)
            menus.adapter = MenuAdapter(restaurantMinimal.meal.menus)
            favoriteButton.isChecked = restaurantMinimal.isFavorite
        }

        holder.favoriteButton.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked){
                viewModel.viewModelScope.launch {
                    val restaurant = restaurantDao.getRestaurantByName(restaurantMinimal.name).first()
                    restaurant.isFavorite = true
                    restaurantDao.insert(restaurant)
                }
            }
            else{
                viewModel.viewModelScope.launch {
                    val restaurant = restaurantDao.getRestaurantByName(restaurantMinimal.name).first()
                    restaurant.isFavorite = false
                    restaurantDao.insert(restaurant)
                }
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var cardView: CardView = itemView.findViewById(R.id.cv_cafeteria)
        var name: TextView = itemView.findViewById(R.id.tv_cafeteria_name)
        var menus: RecyclerView = itemView.findViewById(R.id.rv_menu_item)
        var favoriteButton: ToggleButton = itemView.findViewById(R.id.btn_favorite)
        var shareButton: ImageButton = itemView.findViewById(R.id.btn_share)

    }
}