package com.dietpizza.byakugan.services

import android.util.Log
import com.dietpizza.byakugan.AppConstants
import com.dietpizza.byakugan.models.MangaMetadataModel
import java.io.File
import java.util.zip.ZipFile

val TAG = "MangaParserService"

class MangaParserService(val filepath: String) {

    companion object {
        fun isSupportedFormat(filepath: String): Boolean {
            val ext: String = filepath.substring(filepath.lastIndexOf("."));
            return AppConstants.SupportedFileTypes.contains(ext)
        }

        fun isSupportedImage(filename: String): Boolean {
            val ext: String = filename.substring(filename.lastIndexOf("."));
            return AppConstants.SupportedImageTypes.contains(ext)
        }

        fun findMangaFiles(path: String): List<MangaMetadataModel> {
            val folder = File(path)

            if (!folder.exists()) {
                throw IllegalArgumentException("Folder does not exist: $path")
            }

            if (!folder.isDirectory) {
                throw IllegalArgumentException("Path is not a directory: $path")
            }

            val mangaList = mutableListOf<MangaMetadataModel>()

            folder.listFiles()?.forEach { file ->
                if (file.isFile && isSupportedFormat(file.absolutePath)) {
                    try {
                        val metadata = MangaParserService(file.absolutePath).getMangaMetadata()
                        mangaList.add(metadata)
                    } catch (e: Exception) {
                        // Skip files that can't be parsed
                        Log.w(TAG, "Failed to parse ${file.name}", e)
                    }
                }
            }

            return mangaList
        }
    }

    fun getMangaMetadata(): MangaMetadataModel {
        val file = File(filepath)

        if (!file.exists()) {
            throw IllegalArgumentException("File does not exist: $filepath")
        }

        if (!isSupportedFormat(filepath)) {
            throw IllegalArgumentException("Unsupported file format: $filepath")
        }

        val filename = file.nameWithoutExtension
        val size = file.length()

        // Count image files in the zip
        val pageCount = ZipFile(file).use { zipFile ->
            zipFile.entries().asSequence()
                .filter { entry ->
                    !entry.isDirectory && entry.name.substringAfterLast('.', "")
                        .lowercase() in AppConstants.SupportedImageTypes
                }
                .count()
        }

        return MangaMetadataModel(
            filename = filename,
            size = size,
            pageCount = pageCount,
            coverImagePath = null,
            lastPage = null
        )
    }

}