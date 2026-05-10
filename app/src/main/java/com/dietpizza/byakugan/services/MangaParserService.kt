package com.dietpizza.byakugan.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.dietpizza.byakugan.AppConstants
import com.dietpizza.byakugan.models.MangaMetadataModel
import com.dietpizza.byakugan.models.MangaPanelModel
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.security.MessageDigest
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

private const val TAG = "MangaParserService"

// Performance tuning constants
private const val BUFFER_SIZE = 32 * 1024 // 32KB buffer for faster I/O
private const val TARGET_COVER_WIDTH = 600 // Target width for cover images in dp
private const val TARGET_COVER_HEIGHT = 900 // Target height for cover images in dp
private const val COVER_QUALITY = 100 // JPEG quality (0-100, lower = faster + smaller)


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
            try {
                getEntryStream(zipEntries.first().name)?.use { inputStream ->
                    scaleAndSaveImageFast(inputStream, coverFile)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to scale cover image, saving original: ${e.message}")
                // Fallback: save original image if scaling fails
                getEntryStream(zipEntries.first().name)?.use { inputStream ->
                    FileOutputStream(coverFile).use { outputStream ->
                        inputStream.copyTo(outputStream, BUFFER_SIZE)
                    }
                }
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

    /**
     * Ultra-fast image scaling and compression for cover thumbnails.
     * Uses inSampleSize to decode at lower resolution directly, avoiding full-res decoding.
     * This is significantly faster than decoding full image then scaling down.
     */
    private fun scaleAndSaveImageFast(
        inputStream: InputStream,
        outputFile: File
    ) {
        // Read entire stream into memory once for efficient bounds + decode
        val imageData: ByteArray
        BufferedInputStream(inputStream, BUFFER_SIZE).use { bufferedInput ->
            imageData = bufferedInput.readBytes()
        }

        // Phase 1: Decode bounds only to calculate optimal inSampleSize
        val boundsOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeByteArray(imageData, 0, imageData.size, boundsOptions)

        val originalWidth = boundsOptions.outWidth
        val originalHeight = boundsOptions.outHeight

        // Calculate inSampleSize to get image close to target size without exceeding
        // This is the key to performance - decode smaller image directly
        val inSampleSize = calculateInSampleSize(originalWidth, originalHeight)

        // Phase 2: Decode at reduced resolution from byte array (no stream issues)
        val decodeOptions = BitmapFactory.Options().apply {
            this.inSampleSize = inSampleSize
            inPreferredConfig = Bitmap.Config.RGB_565 // Faster than ARGB_8888 for covers
        }

        val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size, decodeOptions)

        bitmap?.let { bmp ->
            try {
                // Phase 3: Save with optimized compression
                BufferedOutputStream(FileOutputStream(outputFile), BUFFER_SIZE).use { output ->
                    val compressed = bmp.compress(Bitmap.CompressFormat.JPEG, COVER_QUALITY, output)
                    if (!compressed) {
                        Log.w(TAG, "Failed to compress image to JPEG")
                    }
                }
            } finally {
                // Always recycle to prevent memory leaks
                bmp.recycle()
            }
        } ?: Log.e(TAG, "Failed to decode image stream")
    }

    /**
     * Calculate optimal inSampleSize for fast decoding.
     * inSampleSize of N means decode 1/N pixels, reducing memory 1/(N^2) and speed proportionally.
     * Higher inSampleSize = much faster, but trades quality.
     */
    private fun calculateInSampleSize(width: Int, height: Int): Int {
        var inSampleSize = 1

        // Keep downsampling until both dimensions are acceptable
        while ((width / inSampleSize > TARGET_COVER_WIDTH ||
                    height / inSampleSize > TARGET_COVER_HEIGHT) &&
            (width / inSampleSize > 1 || height / inSampleSize > 1)
        ) {
            inSampleSize *= 2
        }

        return inSampleSize
    }

    fun getEntryStream(entryName: String): InputStream? {
        val file = File(filepath)

        if (!file.exists()) {
            throw IllegalArgumentException("File does not exist: $filepath")
        }

        val zipFile = ZipFile(file)

        val entry = zipFile.getEntry(entryName)
            ?: return null

        // Note: ZipFile is kept open because the returned stream is a live reference
        // The caller is responsible for closing this stream, which will close the ZipFile
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

    fun checkIfExists(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists() && file.isFile
    }

    suspend fun getPanelsMetadata(
        mangaId: String,
        onProgress: ((Float) -> Unit)?
    ): MutableList<MangaPanelModel> {
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