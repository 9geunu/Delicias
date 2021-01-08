package com.example.delicias.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.delicias.domain.Restaurant
import com.example.delicias.ui.map.MapViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap


class RestaurantSearchAdapter(val naverMap: NaverMap) :
    ListAdapter<Restaurant, RestaurantSearchAdapter.ViewHolder>(diffUtil){
    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<Restaurant>() {
            override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant) =
                oldItem.name == newItem.name
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var itemView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = getItem(position)
        holder.mTextView.text = restaurant.name

        holder.mTextView.setOnClickListener {
            val cameraUpdate = CameraUpdate.scrollTo(LatLng(restaurant.latitude, restaurant.longitude))
                .animate(CameraAnimation.Easing)
            naverMap.moveCamera(cameraUpdate)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var mTextView = itemView.findViewById(android.R.id.text1) as TextView
    }
}