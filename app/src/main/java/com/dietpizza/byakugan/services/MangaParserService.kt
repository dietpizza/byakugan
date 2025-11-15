package com.dietpizza.byakugan.services

import com.dietpizza.byakugan.AppConstants

class MangaParserService(filepath: String) {
    companion object {
        fun isSupportedFormat(filepath: String): Boolean {
            val ext: String = filepath.substring(filepath.lastIndexOf("."));
            return AppConstants.SupportedFileTypes.contains(ext)
        }
    }

    fun getMangaMetadata() {
        // TODO: Implement manga metadata fetching with validation
    }
}