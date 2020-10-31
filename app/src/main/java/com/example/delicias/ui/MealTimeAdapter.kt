package com.example.delicias.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.delicias.R

class MealTimeAdapter(var mealTimeList: List<String>) : RecyclerView.Adapter<MealTimeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var itemView = inflater.inflate(R.layout.item_meal_time, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mealTimeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mealTimeList.get(position)

        holder.mealTime.text = item
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var mealTime: TextView = itemView.findViewById(R.id.tv_meal_time)
    }
}