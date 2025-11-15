package com.dietpizza.byakugan

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dietpizza.byakugan.adapters.MangaCardAdapter
import com.dietpizza.byakugan.models.ImageItem
import com.google.android.material.color.DynamicColors

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable Material 3 dynamic colors (Android 12+)
        DynamicColors.applyToActivityIfAvailable(this)

        setContentView(R.layout.activity_main)

        // Setup settings button click listener
        findViewById<ImageButton>(R.id.settings_button).setOnClickListener {
            // Handle settings button click
        }

        // Setup RecyclerView with GridLayoutManager
        val recyclerView = findViewById<RecyclerView>(R.id.images_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Create dummy image data
        val imageItems = listOf(
            ImageItem(R.drawable.placeholder_image_1, "Mountain View", "2.4 MB"),
            ImageItem(R.drawable.placeholder_image_2, "Ocean Sunset", "1.8 MB"),
            ImageItem(R.drawable.placeholder_image_3, "Forest Path", "3.2 MB"),
            ImageItem(R.drawable.placeholder_image_4, "City Lights", "2.1 MB"),
            ImageItem(R.drawable.placeholder_image_5, "Desert Dunes", "1.5 MB"),
            ImageItem(R.drawable.placeholder_image_6, "Cherry Blossom", "2.7 MB"),
            ImageItem(R.drawable.placeholder_image_1, "Northern Lights", "3.5 MB"),
            ImageItem(R.drawable.placeholder_image_2, "Tropical Beach", "2.2 MB"),
            ImageItem(R.drawable.placeholder_image_3, "Snowy Peak", "2.9 MB"),
            ImageItem(R.drawable.placeholder_image_4, "Autumn Forest", "1.9 MB"),
            ImageItem(R.drawable.placeholder_image_5, "Lake Reflection", "2.6 MB"),
            ImageItem(R.drawable.placeholder_image_6, "Starry Night", "3.1 MB")
        )

        // Set adapter
        recyclerView.adapter = MangaCardAdapter(imageItems)
    }
}