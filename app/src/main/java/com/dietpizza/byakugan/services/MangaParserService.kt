package com.dietpizza.byakugan.services

import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile

import android.content.Context
import android.util.Log
import com.dietpizza.byakugan.AppConstants
import com.dietpizza.byakugan.models.MangaMetadataModel

val TAG = "MangaParserService"

class MangaParserService(val filepath: String, val context: Context) {

    companion object {
        fun isSupportedFormat(ext: String): Boolean {
            return AppConstants.SupportedFileTypes.contains(ext)
        }

        fun isSupportedImage(ext: String): Boolean {
            return AppConstants.SupportedImageTypes.contains(ext)
        }

        fun findMangaFiles(path: String, context: Context): List<MangaMetadataModel> {
            val folder = File(path)

            if (!folder.exists()) {
                throw IllegalArgumentException("Folder does not exist: $path")
            }

            if (!folder.isDirectory) {
                throw IllegalArgumentException("Path is not a directory: $path")
            }

            val mangaList = mutableListOf<MangaMetadataModel>()

            folder.listFiles()?.forEach { file ->
                if (file.isFile && isSupportedFormat(file.extension)) {
                    try {
                        val metadata =
                            MangaParserService(file.absolutePath, context).getMangaMetadata()

                        Log.i(TAG, "CoverImagePath ${metadata.coverImagePath}")
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

        if (!isSupportedFormat(file.extension)) {
            throw IllegalArgumentException("Unsupported file format: $filepath")
        }

        val filename = file.nameWithoutExtension
        val size = file.length()

        // Count image files in the zip
        val zipEntries = ZipFile(file).use { zipFile ->
            zipFile.entries().asSequence()
                .filter { entry ->
                    !entry.isDirectory && entry.name.substringAfterLast('.', "")
                        .lowercase() in AppConstants.SupportedImageTypes
                }
                .toList()
        }

        val coverFile = File(context.filesDir, "cover_$filename")
        val isCoverExists = coverFile.exists()

        if (!isCoverExists) {
            getEntryStream(zipEntries.first().name)?.use { inputStream ->
                coverFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                    inputStream.close()
                }
            }
        }

        return MangaMetadataModel(
            filename = filename,
            size = size,
            pageCount = zipEntries.count(),
            coverImagePath = coverFile.absolutePath,
            lastPage = null
        )
    }

    fun getEntryStream(entryName: String): InputStream? {
        val file = File(filepath)

        if (!file.exists()) {
            throw IllegalArgumentException("File does not exist: $filepath")
        }

        val zipFile = ZipFile(file)

        val entry = zipFile.getEntry(entryName)
        return zipFile.getInputStream(entry)
    }

}