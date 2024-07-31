package com.fstech.myItems.presentation.lost

import android.location.Address
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstech.myItems.BuildConfig.apiKey
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.jetawy.data.repositories.LostItemsRepositoryImpl
import com.jetawy.domain.models.ItemLost
import com.jetawy.domain.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LostItemViewModel @Inject constructor(private val lostItemsRepositoryImpl: LostItemsRepositoryImpl) :
    ViewModel() {
    var addresses: MutableList<Address>? = null
    val list = mutableStateListOf<Uri>()
    val latLng = mutableStateOf<LatLng?>(null)

    var userDescription = mutableStateOf<String?>("")
    var type = mutableStateOf<String?>("")
    var model = mutableStateOf<String?>("")
    var brand = mutableStateOf<String?>("")
    var category = mutableStateOf(listOf(""))  //generate it
    var itemState = mutableStateOf<String?>("")
    var colors = mutableStateOf(listOf(""))  //add three colors together
    var color1 = mutableStateOf<String?>("")
    var color2 = mutableStateOf<String?>("")
    var color3 = mutableStateOf<String?>("")

    fun addList(list: List<Uri>) {
        this.list.clear()
        this.list.addAll(list)
    }

    fun updateList(itemIndex: Int, item: Uri) {
        list.set(itemIndex, item) // sets the element at 'itemIndex' to 'item'
    } // You can fill the list with initial values if you like, for testing

    fun addList(uri: Uri?) {
        list.add(uri!!)
    }

    fun removeList(itemIndex: Int) {
        list.removeAt(itemIndex)
    }

    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    var uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val _uploadItems: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    var uploadItems: StateFlow<UiState> =
        _uploadItems.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    fun sendPrompt(
        inputs: String,
        prompt: String
    ) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(inputs)
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    Log.e(
                        "LostItemScreenViewModel",
                        outputContent
                    )
                    when (outputContent.trim().toString().trimIndent()) {
                        "true" -> {
                            _uiState.emit(UiState.Success(outputContent))
                        }

                        "false" -> {
                            _uiState.emit(UiState.Error("Check your inputs"))
                        }

                        else -> _uiState.emit(UiState.Error(outputContent))
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    fun translatePrompt(
        inputs: String,
        prompt: String
    ) {
        _uiState.value = UiState.Loading
        Log.e("translatePrompt inputs", inputs)
        Log.e("translatePrompt prompt", prompt)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(inputs)
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    Log.e("outputContent", outputContent)

                    try {
                        val data = convertJsonToDataClass(outputContent)
                        _uiState.emit(UiState.Success(data))
                    } catch (e: Exception) {
                        _uiState.emit(UiState.Error(e.localizedMessage ?: ""))

                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    private fun convertJsonToDataClass(jsonString: String): ItemLost? {
        var string = jsonString.trimIndent().trim()

        if (string.isEmpty()) {
            return null
        }
        string = string.removePrefix("```json").removeSuffix("```")
        val gson = Gson()
        val jsonObject = gson.fromJson(string, ItemLost::class.java)
        return jsonObject
    }

    fun uploadItems(
        imageUris: List<Uri>,
        addresses: Address,
        aiResponse: ItemLost,
        userDescription: String
    ) {
        viewModelScope.launch {
            lostItemsRepositoryImpl.uploadLostItems(
                imageUris,
                addresses = addresses,
                aiResponse,
                userDescription
            ).collect { req ->
                _uploadItems.emit(req)
            }
        }
    }

    fun resetStates() {
        _uiState.value = UiState.Initial
    }

    fun resetUploadItems() {
        viewModelScope.launch {
            _uploadItems.emit(UiState.Initial)
        }
    }

    private val _closeActivity = MutableSharedFlow<Unit>()
    val closeActivity = _closeActivity.asSharedFlow()

    fun triggerCloseActivity() {
        viewModelScope.launch {
            _closeActivity.emit(Unit)
        }
    }
}