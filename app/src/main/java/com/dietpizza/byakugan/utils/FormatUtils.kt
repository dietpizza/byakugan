package com.dietpizza.byakugan.utils

import kotlin.math.pow

object FormatUtils {
    /**
     * Formats bytes into human-readable file size (e.g., "20MB", "512KB")
     *
     * @param bytes The file size in bytes
     * @return Formatted string (e.g., "20MB", "512KB", "1.5GB")
     */
    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0B"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val unitIndex = (kotlin.math.log10(bytes.toDouble()) / 3).toInt()

        if (unitIndex >= units.size) {
            return String.format("%.2f%s", bytes / 1024.0.pow(units.size - 1.0), units.last())
        }

        val divisor = 1024.0.pow(unitIndex.toDouble())
        val value = bytes / divisor

        return when {
            value >= 100 -> String.format("%.0f%s", value, units[unitIndex])
            value >= 10 -> String.format("%.1f%s", value, units[unitIndex])
            else -> String.format("%.2f%s", value, units[unitIndex])
        }
    }
}

