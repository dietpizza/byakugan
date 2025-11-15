package com.dietpizza.byakugan.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dietpizza.byakugan.databinding.WidgetMangaCardBinding
import com.dietpizza.byakugan.models.ImageItem

class MangaCardAdapter(private val imageItems: List<ImageItem>) :
    RecyclerView.Adapter<MangaCardAdapter.ImageViewHolder>() {

    class ImageViewHolder(private val binding: WidgetMangaCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ImageItem) {
            binding.imageView.setImageResource(item.imageResource)
            binding.imageName.text = item.name
            binding.imageSize.text = item.size
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = WidgetMangaCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageItems[position])
    }

    override fun getItemCount(): Int = imageItems.size
}