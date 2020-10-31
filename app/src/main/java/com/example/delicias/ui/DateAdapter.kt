package com.example.delicias.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.delicias.R
import com.example.delicias.domain.Date
import com.example.delicias.ui.home.HomeViewModel

class DateAdapter(val dateList: LiveData<ArrayList<Date>>) : RecyclerView.Adapter<DateAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var itemView = inflater.inflate(R.layout.item_date, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        dateList.value!!.get(position).let {
                date ->
            with(holder) {
                dayOfWeekTextView.text = date.dayOfWeek
                dateTextView.text = date.date
                todayTextView.text = date.isToday.toString()
                if (date.isToday){
                    dayOfWeekTextView.setTextColor(Color.parseColor("#343a40"))
                    dateTextView.setBackgroundResource(R.drawable.shape_orange_circle)
                    dateTextView.setTextColor(Color.parseColor("#ffffff"))
                    todayTextView.text = "오늘"
                    todayTextView.setTextColor(Color.parseColor("#ed4e31"))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dateList.value!!.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var dayOfWeekTextView: TextView = itemView.findViewById(R.id.tv_day_of_the_week)
        var dateTextView: TextView = itemView.findViewById(R.id.tv_date)
        var todayTextView: TextView = itemView.findViewById(R.id.tv_today)
    }
}