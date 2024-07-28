package com.jetawy.data.firebase

import android.net.Uri
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow

interface FirebaseDataBaseService {
    suspend fun uploadLostItemPhotos(photoUris: List<Uri>): Flow<UiState>
    suspend fun uploadFoundItemPhotos(photoUris: List<Uri>): Flow<UiState>

    suspend fun uploadLostItemData(): Flow<UiState>
    suspend fun uploadFoundItemData(): Flow<UiState>
}
