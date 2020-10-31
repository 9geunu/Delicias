package com.example.delicias.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.delicias.R
import com.example.delicias.domain.Menu

class MenuAdapter(var menuList: List<Menu>): RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var itemView = inflater.inflate(R.layout.item_menu, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = menuList.get(position)

        holder.menuName.text = item.name
        holder.menuPrice.text = item.price.toString()
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var menuName: TextView = itemView.findViewById(R.id.tv_menu_name)
        var menuPrice: TextView = itemView.findViewById(R.id.tv_menu_price)
    }
}