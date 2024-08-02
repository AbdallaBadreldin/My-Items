package com.jetawy.domain.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

class Converter {
    fun uriToBitmap(contentResolver: ContentResolver, imageUri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(imageUri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            // Handle exceptions (e.g., file not found, invalid URI)
            null
        }
    }}
