package com.dietpizza.byakugan.services


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.core.net.toUri


private const val TAG = "StorageService"

class StorageService {

    companion object {

        fun checkStoragePermission(): Boolean {
            return Environment.isExternalStorageManager()
        }

        fun checkAndRequestStoragePermission(
            context: Context,
            launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
        ) {
            // Android 11+ requires MANAGE_EXTERNAL_STORAGE
            val isStoragePermissionGranted = checkStoragePermission()

            if (!isStoragePermissionGranted) {
                Log.i(TAG, "Requesting MANAGE_EXTERNAL_STORAGE permission")
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = "package:${context.packageName}".toUri()
                    launcher.launch(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Error requesting storage permission", e)
                    // Fallback to general settings if specific intent fails
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    launcher.launch(intent)
                }
            } else {
                Log.i(TAG, "MANAGE_EXTERNAL_STORAGE permission already granted")
            }
        }

        fun getFilePathFromUri(context: Context, uri: Uri?): String? {
            if (uri == null) return null

            // Handle content:// scheme
            if (uri.scheme == "content") {
                // For media content URIs
                if (uri.authority == MediaStore.AUTHORITY) {
                    val projection = arrayOf(MediaStore.MediaColumns.DATA)
                    context.contentResolver.query(uri, projection, null, null, null)
                        ?.use { cursor ->
                            if (cursor.moveToFirst()) {
                                val columnIndex =
                                    cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                                return cursor.getString(columnIndex)
                            }
                        }
                }
                if ("com.android.externalstorage.documents" == uri.authority) {
                    // Check first if it's a document URI
                    if (DocumentsContract.isDocumentUri(context, uri)) {
                        // Original code for document URIs
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split =
                            docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val type = split[0]
                        return if ("primary".equals(type, ignoreCase = true)) {
                            Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                        } else {
                            "/storage/" + type + "/" + split[1]
                        }
                    } else if (DocumentsContract.isTreeUri(uri)) {
                        val treeId = DocumentsContract.getTreeDocumentId(uri)
                        val split =
                            treeId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        val type = split[0]
                        return if ("primary".equals(type, ignoreCase = true)) {
                            Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                        } else {
                            // Handle secondary storage or SD card
                            "/storage/" + type + "/" + split[1]
                        }
                    }
                }

                // For DocumentProvider URIs
                val docId = DocumentsContract.getDocumentId(uri)
                if (docId.startsWith("raw:")) {
                    // For raw file paths
                    return docId.substring(4)
                }
            }

            // For file:// scheme
            if (uri.scheme == "file") {
                return uri.path
            }

            return null
        }
    }
}
