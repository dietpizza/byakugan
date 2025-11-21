package com.dietpizza.byakugan.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.graphics.scale
import com.dietpizza.byakugan.AppConstants
import com.dietpizza.byakugan.models.MangaMetadataModel
import com.dietpizza.byakugan.models.MangaPanelModel
import java.io.File
import java.io.InputStream
import java.security.MessageDigest
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

private const val TAG = "MangaParserService"


fun String.md5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

class MangaParserService(val filepath: String, val context: Context) {

    companion object {
        fun isSupportedFormat(ext: String): Boolean {
            return AppConstants.SupportedFileTypes.contains(ext)
        }

        fun isSupportedImage(ext: String): Boolean {
            return AppConstants.SupportedImageTypes.contains(ext)
        }

        fun findMangaFiles(
            path: String,
            context: Context,
            onProgress: ((progress: Float) -> Unit)? = null
        ): List<MangaMetadataModel> {
            val folder = File(path)

            if (!folder.exists()) {
                throw IllegalArgumentException("Folder does not exist: $path")
            }

            if (!folder.isDirectory) {
                throw IllegalArgumentException("Path is not a directory: $path")
            }

            val mangaList = mutableListOf<MangaMetadataModel>()

            // Get all supported files first to calculate total count
            val supportedFiles = folder.listFiles()?.filter { file ->
                file.isFile && isSupportedFormat(file.extension)
            } ?: emptyList()

            val totalFiles = supportedFiles.size
            var processedFiles = 0

            supportedFiles.forEach { file ->
                try {
                    val metadata =
                        MangaParserService(file.absolutePath, context).getMangaMetadata()

                    mangaList.add(metadata)
                } catch (e: Exception) {
                    // Skip files that can't be parsed
                    Log.w(TAG, "Failed to parse ${file.name}", e)
                } finally {
                    processedFiles++
                    // Calculate and report progress percentage (0.0 to 100.0)
                    val progress = if (totalFiles > 0) {
                        (processedFiles.toFloat() / totalFiles.toFloat()) * 100f
                    } else {
                        100f
                    }
                    onProgress?.invoke(progress)
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

        val id = file.name.md5()
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

        val coverFile = File(context.filesDir, "cover_$id")
        val isCoverExists = coverFile.exists()

        if (!isCoverExists) {
            getEntryStream(zipEntries.first().name)?.use { inputStream ->
                // Decode the image
                val originalBitmap = BitmapFactory.decodeStream(inputStream)

                // Scale to half resolution
                val scaledWidth = originalBitmap.width / 4
                val scaledHeight = originalBitmap.height / 4
                val scaledBitmap = originalBitmap.scale(scaledWidth, scaledHeight)

                // Save the scaled bitmap
                coverFile.outputStream().use { outputStream ->
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                }

                // Clean up bitmaps
                originalBitmap.recycle()
                scaledBitmap.recycle()
            }
        }

        return MangaMetadataModel(
            id = id,
            title = file.nameWithoutExtension,
            path = file.absolutePath,
            size = size,
            pageCount = zipEntries.count(),
            coverImagePath = coverFile.absolutePath,
            lastPage = null,
            timestamp = file.lastModified()
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

    fun getMangaModelFromEntry(mangaId: String, entry: ZipEntry): MangaPanelModel? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        getEntryStream(entry.name).use {
            try {
                // Decode just the bounds without loading the full bitmap
                BitmapFactory.decodeStream(it, null, options)

                val width = options.outWidth
                val height = options.outHeight

                if (width > 0 && height > 0) {
                    val aspectRatio = height.toFloat() / width.toFloat()
                    val id = UUID.randomUUID().toString()

                    return MangaPanelModel(
                        id = id,
                        mangaId = mangaId,
                        panelName = entry.name,
                        height = height,
                        width = width,
                        aspectRatio = aspectRatio
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error decoding entry ${entry.name}: $e")
            }

            return null
        }
    }

    fun getPanelsMetadata(mangaId: String, onProgress: ((Float) -> Unit)?): List<MangaPanelModel> {
        val file = File(filepath)

        ZipFile(file).use { zipFile ->
            val images = zipFile.entries().asSequence()
                .filter { entry ->
                    val ext = entry.name.lowercase().substringAfterLast('.')
                    !entry.isDirectory && AppConstants.SupportedImageTypes.contains(ext)
                }
                .toList()

            val totalImages = images.size
            var processedImages = 0
            val panels = mutableListOf<MangaPanelModel>()

            images.forEach { entry ->
                try {
                    val panel = getMangaModelFromEntry(mangaId, entry)
                    if (panel != null) {
                        panels.add(panel)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse panel ${entry.name}", e)
                } finally {
                    processedImages++
                    // Calculate and report progress percentage (0.0 to 100.0)
                    val progress = if (totalImages > 0) {
                        (processedImages.toFloat() / totalImages.toFloat()) * 100f
                    } else {
                        100f
                    }
                    onProgress?.invoke(progress)
                }
            }

            return panels
        }
    }
}