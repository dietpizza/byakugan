package com.dietpizza.byakugan.services

import android.graphics.Bitmap
import android.util.LruCache

object ImageCacheService {
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 8

    private val cache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    fun get(panelId: String): Bitmap? {
        return cache.get(panelId)
    }

    fun put(panelId: String, bitmap: Bitmap) {
        if (cache.get(panelId) == null) {
            cache.put(panelId, bitmap)
        }
    }

    fun clear() {
        cache.evictAll()
    }
}
