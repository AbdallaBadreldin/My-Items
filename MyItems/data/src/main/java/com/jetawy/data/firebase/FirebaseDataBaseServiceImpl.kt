package com.jetawy.data.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDataBaseServiceImpl @Inject constructor(private val db: FirebaseStorage) :
    FirebaseDataBaseService {
    private val _uploadLostItemPhotos: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uploadLostItemPhotos: Flow<UiState> =
        _uploadLostItemPhotos.asStateFlow()

    private val _uploadFoundItemPhotos: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uploadFoundItemPhotos: Flow<UiState> =
        _uploadFoundItemPhotos.asStateFlow()

    override suspend fun uploadLostItemPhotos(photoUris: List<Uri>): Flow<UiState> {
        photoUris.forEach { uri ->
            val fileName = uri.lastPathSegment ?: "image.jpg" // Get file name or use a default
            val imageRef =
                db.reference.child("LostItemsImages/$fileName")// Create a reference to the image in Firebase Storage
            _uploadLostItemPhotos.emit(UiState.Loading)
            try {
                imageRef.putFile(uri).await() // Upload the image
                val downloadUrl = imageRef.downloadUrl.await() // Get the download URL
                Log.d("FirebaseStorage", "Upload successful. Download URL: $downloadUrl")
                _uploadLostItemPhotos.emit(UiState.Success<String>(downloadUrl.toString()))
                // Store the download URL in your database or use it as needed
            } catch (e: Exception) {
                // Handle upload errors
                _uploadLostItemPhotos.emit(UiState.Error(e.message.toString()))
            }
        }
        return uploadLostItemPhotos
    }

    override suspend fun uploadFoundItemPhotos(photoUris: List<Uri>): Flow<UiState> {
        photoUris.forEach { uri ->
            val fileName = uri.lastPathSegment ?: "image.jpg" // Get file name or use a default
            val imageRef =
                db.reference.child("LostItemsImages/$fileName")// Create a reference to the image in Firebase Storage
            _uploadFoundItemPhotos.emit(UiState.Loading)
            try {
                imageRef.putFile(uri).await() // Upload the image
                val downloadUrl = imageRef.downloadUrl.await() // Get the download URL
                Log.d("FirebaseStorage", "Upload successful. Download URL: $downloadUrl")
                _uploadFoundItemPhotos.emit(UiState.Success<String>(downloadUrl.toString()))
                // Store the download URL in your database or use it as needed
            } catch (e: Exception) {
                // Handle upload errors
                _uploadFoundItemPhotos.emit(UiState.Error(e.message.toString()))
            }
        }
        return uploadFoundItemPhotos
    }

    override suspend fun uploadLostItemData(): Flow<UiState> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadFoundItemData(): Flow<UiState> {
        TODO("Not yet implemented")
    }
}