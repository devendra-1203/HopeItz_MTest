package com.example.devhopeitmtest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager

class IconsAdapter(private  val icons: List<Icons>, private  val onClick : (Icons) -> Unit) :
    RecyclerView.Adapter<IconsAdapter.IconViewHolder>() {

    class IconViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val title : TextView = view.findViewById(R.id.icon_title)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconsAdapter.IconViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.icon_item, parent,false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconsAdapter.IconViewHolder, position: Int) {
        val icon = icons[position]
        holder.title.text = icon.title
      //  holder.itemView.setOnClickListener(onClick(icon))
    }

    override fun getItemCount(): Int= icons.size

}
