package com.example.devhopeitmtest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class AndroidAppAdapter(private  val data: List<AndroidApp>, private  val onClick : (AndroidApp) -> Unit) :
    RecyclerView.Adapter<AndroidAppAdapter.IconViewHolder>() {

    class IconViewHolder(view : View) : RecyclerView.ViewHolder(view) {
     //   val title : TextView = view.findViewById(R.id.icon_title)
        val image_view : ImageView = view.findViewById(R.id.image_view)}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AndroidAppAdapter.IconViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.icon_item, parent,false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: AndroidAppAdapter.IconViewHolder, position: Int) {
        val icon = data[position]
        // Load image with Glide
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(FitCenter(), RoundedCorners(16))

        Glide.with(holder.itemView.context)
            .load(icon.logo)
            .apply(requestOptions)
          //  .skipMemoryCache(true)//for caching the image url in case phone is offline
            .into(holder.image_view)

       // holder.title.text = icon.name
        holder.itemView.setOnClickListener {
            onClick(icon)
        }
    }

    override fun getItemCount(): Int= data.size



}
