package com.jetawy.data.repositories

import android.location.Address
import android.net.Uri
import com.jetawy.data.firebase.FirebaseDataBaseService
import com.jetawy.domain.models.ItemFound
import com.jetawy.domain.repository.FoundItemsRepository
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FoundItemsRepositoryImpl @Inject constructor(private val fbs: FirebaseDataBaseService) :
    FoundItemsRepository {

    override suspend fun getFoundItemsById(): Flow<UiState> {
        return fbs.getFoundItemsById()
    }

    override suspend fun uploadFoundItems(
        imageUris: List<Uri>,
        addresses: Address,
        aiResponse: ItemFound,
    ): Flow<UiState> {
        return fbs.uploadFoundItems(
            imageUris,
            addresses,
            aiResponse,
        )
    }

    override suspend fun getFoundItemsByCountry(country: String): Flow<UiState> {
      return  fbs.getFoundItemsByCountry(country)
    }


}