package com.jetawy.domain.repository

import android.location.Address
import android.net.Uri
import com.jetawy.domain.models.ItemLost
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow

interface LostItemsRepository  {
    suspend fun getLostItemById(): Flow<UiState>
    suspend fun uploadLostItems(
        imageUris: List<Uri>,
        addresses: Address,
        aiResponse: ItemLost,
        userResponse: ItemLost,
    ): Any
}