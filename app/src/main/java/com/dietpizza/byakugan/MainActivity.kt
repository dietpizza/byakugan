package com.dietpizza.byakugan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.dietpizza.byakugan.adapters.MangaCardAdapter
import com.dietpizza.byakugan.databinding.ActivityMainBinding
import com.dietpizza.byakugan.models.MangaCardModel
import com.google.android.material.color.DynamicColors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable Material 3 dynamic colors (Android 12+)
        DynamicColors.applyToActivityIfAvailable(this)

        // Inflate view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup settings button click listener
        binding.settingsButton.setOnClickListener {
            // Handle settings button click
        }

        // Setup RecyclerView with GridLayoutManager
        binding.imagesRecyclerView.layoutManager = GridLayoutManager(this, 2)

        // Create dummy image data
        val imageItems = listOf(
            MangaCardModel(R.drawable.placeholder_image_1, "Mountain View", "2.4 MB"),
            MangaCardModel(R.drawable.placeholder_image_2, "Ocean Sunset", "1.8 MB"),
            MangaCardModel(R.drawable.placeholder_image_3, "Forest Path", "3.2 MB"),
            MangaCardModel(R.drawable.placeholder_image_4, "City Lights", "2.1 MB"),
            MangaCardModel(R.drawable.placeholder_image_5, "Desert Dunes", "1.5 MB"),
            MangaCardModel(R.drawable.placeholder_image_6, "Cherry Blossom", "2.7 MB"),
            MangaCardModel(R.drawable.placeholder_image_1, "Northern Lights", "3.5 MB"),
            MangaCardModel(R.drawable.placeholder_image_2, "Tropical Beach", "2.2 MB"),
            MangaCardModel(R.drawable.placeholder_image_3, "Snowy Peak", "2.9 MB"),
            MangaCardModel(R.drawable.placeholder_image_4, "Autumn Forest", "1.9 MB"),
            MangaCardModel(R.drawable.placeholder_image_5, "Lake Reflection", "2.6 MB"),
            MangaCardModel(R.drawable.placeholder_image_6, "Starry Night", "3.1 MB")
        )

        // Set adapter
        binding.imagesRecyclerView.adapter = MangaCardAdapter(imageItems)
    }
}