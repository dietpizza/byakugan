package com.dietpizza.byakugan.adapters

import com.dietpizza.byakugan.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dietpizza.byakugan.databinding.WidgetMangaCardBinding
import com.dietpizza.byakugan.models.MangaMetadataModel
import java.io.File

class MangaCardAdapter(private val imageItems: List<MangaMetadataModel>) :
    RecyclerView.Adapter<MangaCardAdapter.ImageViewHolder>() {

    class ImageViewHolder(private val binding: WidgetMangaCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MangaMetadataModel) {
            binding.imageView.load(item.coverImagePath?.let { File(it) }) {
                crossfade(true)
                placeholder(R.drawable.placeholder_image_5)
                error(R.drawable.placeholder_image_2)
            }
            binding.imageName.text = item.filename
            binding.imageSize.text = "${item.pageCount} pages"
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