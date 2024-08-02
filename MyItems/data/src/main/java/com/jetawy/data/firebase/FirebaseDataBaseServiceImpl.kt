package com.jetawy.data.firebase

import android.location.Address
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.jetawy.domain.models.ItemFound
import com.jetawy.domain.models.ItemLost
import com.jetawy.domain.models.ProfileItem
import com.jetawy.domain.models.get.found.ItemFoundResponse
import com.jetawy.domain.models.get.lost.ItemLostResponse
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDataBaseServiceImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val db: FirebaseDatabase
) : FirebaseDataBaseService {
    private val _uploadFoundItem: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uploadFoundItem: Flow<UiState> =
        _uploadFoundItem.asStateFlow()

    private val _getFoundItemData: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val getFoundItemData: Flow<UiState> =
        _getFoundItemData.asStateFlow()

    private val _uploadLostItem: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uploadLostItem: Flow<UiState> =
        _uploadLostItem.asStateFlow()

    private val _getLostItemData: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val getLostItemData: Flow<UiState> =
        _getLostItemData.asStateFlow()

    override suspend fun uploadFoundItems(
        imageUris: List<Uri>,
        addresses: Address,
        aiResponse: ItemFound,
    ): Flow<UiState> {
        //first we will upload photos to firebase storage
        _uploadFoundItem.emit(UiState.Loading)
        CoroutineScope(Dispatchers.IO).launch {
            val listOfDownloadUrls = mutableListOf<String>()
            val myRef = db.getReference("foundItems/${addresses.countryName}").push()
            val myProfileRef =
                db.getReference("profiles/${FirebaseAuth.getInstance().currentUser?.uid}/foundItems")
                    .push()
            imageUris.forEach { uri ->
                val fileName = uri.lastPathSegment ?: "image.jpg" // Get file name or use a default
                val imageRef =
                    storage.reference.child("FoundItemsImages/${myRef.key}/$fileName")// Create a reference to the image in Firebase Storage
                try {
                    imageRef.putFile(uri).await() // Upload the image
                    val downloadUrl = imageRef.downloadUrl.await() // Get the download URL
                    listOfDownloadUrls.add(downloadUrl.toString())
                    // Store the download URL in your database or use it as needed
                } catch (e: Exception) {
                    // Handle upload errors
                    _uploadFoundItem.emit(UiState.Error(e.message.toString()))
                    return@launch
                }
            }
            //second we will upload data to firebase database
            try {
                myRef.child("location").setValue("${addresses.latitude},${addresses.longitude}")
                    .await()
                myRef.child("images").setValue(listOfDownloadUrls.toList()).await()
                myRef.child("addressUrl").setValue(addresses.url).await()
                myRef.child("aiResponse").setValue(aiResponse).await()
                myRef.child("objectID").setValue(myRef.key).await()
//                myRef.child("userDescription").setValue(userDescription).await()
                myRef.child("user").setValue(FirebaseAuth.getInstance().currentUser?.uid).await()
                myRef.child("timestamp").setValue(System.currentTimeMillis()).await()
            } catch (e: Exception) {
                _uploadFoundItem.emit(UiState.Error(e.message.toString()))
                return@launch
            }

            //third we will upload reference to uploaded data in user profile

            try {
                myProfileRef.child("id").setValue(myRef.key).await()
                myProfileRef.child("countryName").setValue(addresses.countryName).await()
                _uploadFoundItem.emit(UiState.Success(myProfileRef.toString()))
            } catch (e: Exception) {
                _uploadFoundItem.emit(UiState.Error(e.message.toString()))
                return@launch
            }
        }
        return uploadFoundItem
    }

    override suspend fun getFoundItemData(): Flow<UiState> {
        _getFoundItemData.emit(UiState.Loading)
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val foundRef =
                    db.getReference("/profiles/${FirebaseAuth.getInstance().currentUser?.uid}/lostItems")
                val profileItemDetails = mutableListOf<ItemFoundResponse>()
                //get list of items Ids from Profile
                val item = foundRef.get().await()
                item?.children?.forEach { profileItemSnapShot ->
                    //start getting data for each item by using Ids that we got in profile
                    val profileItem = profileItemSnapShot.getValue(ProfileItem::class.java)
                    val itemRef =
                        db.getReference("/lostItems/${profileItem?.countryName}/${profileItem?.objectID}")
                    val lostItemsSnapShot = itemRef.get().await()
//                    lostItemsSnapShot?.children?.forEach { it ->
                    Log.d("TAG", "getFoundItemData: ${lostItemsSnapShot}")
                    lostItemsSnapShot.getValue(ItemFoundResponse::class.java)?.let {
                        profileItemDetails.add(it)
//                        }
                    }
                }
                Log.d("TAG", "getFoundItemData: $profileItemDetails")
                _getFoundItemData.emit(UiState.Success(profileItemDetails))
            }
        } catch (e: Exception) {
            _getFoundItemData.emit(UiState.Error(e.message.toString()))
        }
        return getFoundItemData
    }

    override suspend fun uploadLostItems(
        imageUris: List<Uri>,
        addresses: Address,
        aiResponse: ItemLost,
        userResponse: ItemLost,
    ): Flow<UiState> {
        //first we will upload photos to firebase storage
        _uploadLostItem.emit(UiState.Loading)
        CoroutineScope(Dispatchers.IO).launch {
            val listOfDownloadUrls = mutableListOf<String>()
            val myRef = db.getReference("lostItems/${addresses.countryName}").push()
            val myProfileRef =
                db.getReference("profiles/${FirebaseAuth.getInstance().currentUser?.uid}/lostItems")
                    .push()
            imageUris.forEach { uri ->
                val fileName = uri.lastPathSegment ?: "image.jpg" // Get file name or use a default
                val imageRef =
                    storage.reference.child("LostItemsImages/${myRef.key}/$fileName")// Create a reference to the image in Firebase Storage
                try {
                    imageRef.putFile(uri).await() // Upload the image
                    val downloadUrl = imageRef.downloadUrl.await() // Get the download URL
                    listOfDownloadUrls.add(downloadUrl.toString())
                    // Store the download URL in your database or use it as needed
                } catch (e: Exception) {
                    // Handle upload errors
                    _uploadLostItem.emit(UiState.Error(e.message.toString()))
                    return@launch
                }
            }
            //second we will upload data to firebase database
            try {
                myRef.child("location").setValue("${addresses.latitude},${addresses.longitude}")
                    .await()
                myRef.child("images").setValue(listOfDownloadUrls.toList()).await()
                myRef.child("addressUrl").setValue(addresses.url).await()
                myRef.child("aiResponse").setValue(aiResponse).await()
                myRef.child("userResponse").setValue(userResponse).await()
                myRef.child("objectID").setValue(myRef.key).await()
//                myRef.child("userDescription").setValue(userDescription).await()
                myRef.child("user").setValue(FirebaseAuth.getInstance().currentUser?.uid).await()
                myRef.child("timestamp").setValue(System.currentTimeMillis()).await()
            } catch (e: Exception) {
                _uploadLostItem.emit(UiState.Error(e.message.toString()))
                return@launch
            }

            //third we will upload reference to uploaded data in user profile

            try {
                myProfileRef.child("objectID").setValue(myRef.key).await()
                myProfileRef.child("countryName").setValue(addresses.countryName).await()
                _uploadLostItem.emit(UiState.Success(myProfileRef.toString()))
            } catch (e: Exception) {
                _uploadLostItem.emit(UiState.Error(e.message.toString()))
                return@launch
            }
        }
        return uploadLostItem
    }

    override suspend fun getLostItemData(): Flow<UiState> {
        _getLostItemData.emit(UiState.Loading)
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val foundRef =
                    db.getReference("/profiles/${FirebaseAuth.getInstance().currentUser?.uid}/lostItems")
                val profileItemDetails = mutableListOf<ItemLostResponse>()
                //get list of items Ids from Profile
                val item = foundRef.get().await()
                item?.children?.forEach { profileItemSnapShot ->
                    //start getting data for each item by using Ids that we got in profile
                    val profileItem = profileItemSnapShot.getValue(ProfileItem::class.java)
                    val itemRef =
                        db.getReference("/lostItems/${profileItem?.countryName}/${profileItem?.objectID}")
                    val lostItemsSnapShot = itemRef.get().await()
//                    lostItemsSnapShot?.children?.forEach { it ->
                    Log.d("TAG2", "getLostItemData: $lostItemsSnapShot")
                    lostItemsSnapShot.getValue(ItemLostResponse::class.java)?.let {
                        profileItemDetails.add(it)
                    }
//                    }
                }
                Log.d("TA1G", "getFoundItemData: $profileItemDetails")
                _getLostItemData.emit(UiState.Success(profileItemDetails))
            }
        } catch (e: Exception) {
            _getLostItemData.emit(UiState.Error(e.message.toString()))
        }
        return getLostItemData
    }


}