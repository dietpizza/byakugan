package com.dietpizza.byakugan

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme


class AppConstants {
    companion object {
        val SupportedFileTypes = arrayOf("zip", "cbz")
        val SupportedImageTypes = arrayOf("jpg", "jpeg", "png", "webp", "bmp")
    }
}

fun dynamicColorScheme(context: Context): ColorScheme {
    return dynamicDarkColorScheme(context)
}
