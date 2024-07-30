package com.jetawy.domain.repository

import android.location.Address
import android.net.Uri
import com.jetawy.domain.models.ItemResponse
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow

interface LostItemsRepository  {
    suspend fun getLostItemData(): Flow<UiState>
    suspend fun uploadLostItems(
        imageUris: List<Uri>,
        addresses: Address,
        AiResponse: ItemResponse,
        userDescription: String
    ): Any
}