package com.example.delicias.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.delicias.R
import com.example.delicias.domain.SearchHistory
import com.example.delicias.domain.repository.RestaurantRepository
import com.example.delicias.ui.map.MapFragment
import kotlinx.coroutines.runBlocking


class SearchHistoryAdapter(
    private val repository: RestaurantRepository,
    private val isSearchingNow: LiveData<Boolean>,
    private val context: Context) :
    ListAdapter<SearchHistory, SearchHistoryAdapter.ViewHolder>(diffUtil){
    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<SearchHistory>() {
            override fun areContentsTheSame(oldItem: SearchHistory, newItem: SearchHistory) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: SearchHistory, newItem: SearchHistory) =
                oldItem.id == newItem.id
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var itemView = inflater.inflate(R.layout.item_search_history, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchHistory = getItem(position)

        with(holder){
            restaurantName.text = searchHistory.name
            searchDate.text = searchHistory.searchDate

            if (isSearchingNow.value == true)
                deleteOrGo.setImageResource(R.drawable.search_go)
            else
                deleteOrGo.setImageResource(R.drawable.close)

            deleteOrGo.setOnClickListener {
                runBlocking {
                    if (isSearchingNow.value == true)
                        repository.deleteSearchHistoryById(searchHistory.id)
                    else {
                        //TODO Move To Map Fragment!!!!
                        val bundle = Bundle()
                        bundle.putString("Destination", searchHistory.name)

                        val mapFragment = MapFragment()
                        mapFragment.arguments = bundle

                        val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                        val fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.add(0, mapFragment)
                        fragmentTransaction.commit()

                        val intent = Intent(context, MapFragment::class.java)

                        context.startActivity(intent)
                    }
                }
            }

            layout.setOnClickListener {
                runBlocking {
                    repository.insertSearchHistory(searchHistory)
                }
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var layout = itemView.findViewById(R.id.ll_search_history_item) as LinearLayout
        var restaurantName = itemView.findViewById(R.id.tv_restaurant_name) as TextView
        var searchDate = itemView.findViewById(R.id.tv_search_date) as TextView
        var deleteOrGo = itemView.findViewById(R.id.iv_delete_or_go) as ImageView
    }
}