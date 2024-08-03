package com.jetawy.domain.repository

import android.location.Address
import android.net.Uri
import com.jetawy.domain.models.ItemFound
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow

interface FoundItemsRepository {
    suspend fun getFoundItemsById(): Flow<UiState>
    suspend fun uploadFoundItems(
        imageUris: List<Uri>,
        addresses: Address,
        aiResponse: ItemFound,
    ): Any

    suspend fun getFoundItemsByCountry(country: String): Flow<UiState>
}