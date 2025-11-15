package com.dietpizza.byakugan.services


import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore


class StorageService {

    fun getFilePathFromUri(context: Context, uri: Uri): String? {
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
