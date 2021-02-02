package com.example.delicias.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.delicias.R
import com.example.delicias.domain.Notification

class NotificationAdapter() : ListAdapter<Notification, NotificationAdapter.ViewHolder>(diffUtil){
    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<Notification>() {
            override fun areContentsTheSame(oldItem: Notification, newItem: Notification) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Notification, newItem: Notification) =
                oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_notification, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = getItem(position)

        with(holder) {
            title.text = notification.notificationSummary
            date.text = notification.date

            layout.setOnClickListener {

            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var layout: ConstraintLayout = itemView.findViewById(R.id.cl_notification_layout)
        var title: TextView = itemView.findViewById(R.id.tv_notification_title)
        var date: TextView = itemView.findViewById(R.id.tv_notification_date)
    }
}