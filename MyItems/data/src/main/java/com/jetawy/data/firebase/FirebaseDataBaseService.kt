package com.jetawy.data.firebase

import android.location.Address
import android.net.Uri
import com.jetawy.domain.models.ItemResponse
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow

interface FirebaseDataBaseService {

    suspend fun getLostItemData(): Flow<UiState>
    suspend fun uploadLostItems(
        imageUris: List<Uri>,
        addresses: Address,
        AiResponse: ItemResponse
    ): Flow<UiState>

    suspend fun getFoundItemData(): Flow<UiState>
    suspend fun uploadFoundItems(
        imageUris: List<Uri>,
        addresses: Address,
        AiResponse: ItemResponse,
        userDescription: String
    ): Flow<UiState>
}