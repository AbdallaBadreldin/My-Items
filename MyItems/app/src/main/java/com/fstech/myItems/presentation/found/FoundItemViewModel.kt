package com.fstech.myItems.presentation.found

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstech.myItems.BuildConfig.apiKey
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.jetawy.domain.models.ItemResponse
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FoundItemViewModel : ViewModel() {
    val list = mutableStateListOf<Uri>()
    val latLng =   mutableStateOf<LatLng?>(null)

    fun updateItem(itemIndex: Int, item: Uri) {
        list.set(itemIndex, item) // sets the element at 'itemIndex' to 'item'
    } // You can fill the list with initial values if you like, for testing

    fun addItem(uri: Uri?) {
        list.add(uri!!)
    }

    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    var uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "$apiKey"
    )

    fun sendPrompt(
        bitmap: MutableList<Bitmap>,
        prompt: String
    ) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        bitmap.forEach { image(it) }
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    Log.e("outputContent", outputContent)
                    val data = convertJsonToDataClass(outputContent)
                    if (data == null)
                        _uiState.value = UiState.Error("null")
                    if (data?.name == null)
                        _uiState.value = UiState.Error("null")
                    if (data?.name == "null")
                        _uiState.value = UiState.Error("null")
                    else
                        _uiState.value = UiState.Success(
                            data
                        )
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    fun convertJsonToDataClass(jsonString: String): ItemResponse? {
        var string = jsonString.trimIndent().trim()

        if (string.isEmpty()) {
            return null
        }
        string = string.removePrefix("```json").removeSuffix("```")
        val gson = Gson()
        val jsonObject = gson.fromJson(string, ItemResponse::class.java)
        return jsonObject
    }

    fun resetStates() {
        _uiState.value = UiState.Initial
    }

    fun uploadItem() {
//        TODO("Not yet implemented")
    }
}