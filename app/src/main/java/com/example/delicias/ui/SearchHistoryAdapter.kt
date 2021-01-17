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
import com.example.delicias.util.Constants
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

            if (isSearchingNow.value == true) {
                searchedDateOrSearchResult.setImageResource(R.drawable.search_circle)
                deleteOrGo.setImageResource(R.drawable.search_go)
                searchDate.visibility = View.INVISIBLE
            }
            else {
                searchedDateOrSearchResult.setImageResource(R.drawable.search_time)
                deleteOrGo.setImageResource(R.drawable.close)
                searchDate.visibility = View.VISIBLE
            }

            deleteOrGo.setOnClickListener {
                runBlocking {
                    if (isSearchingNow.value == false)
                        repository.deleteSearchHistoryById(searchHistory.id)
                    else {
                        repository.insertSearchHistory(searchHistory)

                        val intent = Intent()
                        intent.putExtra("DestinationRestaurant", searchHistory.name)
                        (context as AppCompatActivity).setResult(Activity.RESULT_OK, intent)
                        context.finish()
                    }
                }
            }

            layout.setOnClickListener {
                runBlocking {
                    repository.insertSearchHistory(searchHistory)

                    val intent = Intent()
                    intent.putExtra("DestinationRestaurant", searchHistory.name)
                    (context as AppCompatActivity).setResult(Activity.RESULT_OK, intent)
                    context.finish()
                }
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var layout = itemView.findViewById(R.id.ll_search_history_item) as LinearLayout
        var searchedDateOrSearchResult = itemView.findViewById(R.id.iv_searched_date_or_search_result) as ImageView
        var restaurantName = itemView.findViewById(R.id.tv_restaurant_name) as TextView
        var searchDate = itemView.findViewById(R.id.tv_search_date) as TextView
        var deleteOrGo = itemView.findViewById(R.id.iv_delete_or_go) as ImageView
    }
}