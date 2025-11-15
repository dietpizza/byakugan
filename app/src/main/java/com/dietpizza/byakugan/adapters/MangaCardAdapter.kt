package com.dietpizza.byakugan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dietpizza.byakugan.R
import com.dietpizza.byakugan.models.ImageItem

class MangaCardAdapter(private val imageItems: List<ImageItem>) :
    RecyclerView.Adapter<MangaCardAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
        val imageName: TextView = itemView.findViewById(R.id.image_name)
        val imageSize: TextView = itemView.findViewById(R.id.image_size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.widget_manga_card, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = imageItems[position]
        holder.imageView.setImageResource(item.imageResource)
        holder.imageName.text = item.name
        holder.imageSize.text = item.size
    }

    override fun getItemCount(): Int = imageItems.size
}