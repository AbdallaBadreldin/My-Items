package com.jetawy.data.repositories

import android.location.Address
import android.net.Uri
import com.jetawy.data.firebase.FirebaseDataBaseService
import com.jetawy.domain.models.ItemLost
import com.jetawy.domain.repository.LostItemsRepository
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LostItemsRepositoryImpl @Inject constructor(private val fbs: FirebaseDataBaseService) :
    LostItemsRepository {

    override suspend fun getLostItemById(): Flow<UiState> {
        return fbs.getLostItemById()
    }

    override suspend fun uploadLostItems(
        imageUris: List<Uri>,
        addresses: Address,
        AiResponse: ItemLost,
        userResponse: ItemLost,
    ): Flow<UiState> {
        return fbs.uploadLostItems(
            imageUris,
            addresses,
            AiResponse,
            userResponse
        )
    }


}