package com.fstech.myItems.presentation.lost

import android.graphics.Bitmap
import android.location.Address
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstech.myItems.BuildConfig.apiKey
import com.fstech.myItems.presentation.getAppLanguage
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
    lateinit var aiResponse: ItemLost
    var addresses: MutableList<Address>? = null
    val list = mutableStateListOf<Uri>()
    val latLng = mutableStateOf<LatLng?>(null)

    var translatedDescription = mutableStateOf<String?>("")
    var userDescription = mutableStateOf<String?>("")
    var imageDescription = mutableStateOf<String?>("")
    var type = mutableStateOf<String?>("")
    var model = mutableStateOf<String?>("")
    var brand = mutableStateOf<String?>("")
    var category = mutableStateOf("")  //generate it
    var itemState = mutableStateOf<String?>("")
    var colors = mutableStateListOf<String>()  //add three colors together
    var color1 = mutableStateOf<String?>("")
    var color2 = mutableStateOf<String?>("")
    var color3 = mutableStateOf<String?>("")
    var userDescriptionError = mutableStateOf<String?>("")
    var strictDescription = mutableStateOf<Boolean?>(true)

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
        bitmap: MutableList<Bitmap>,
    ) {
        _uiState.value = UiState.Loading
        userDescriptionError.value = ""

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (bitmap.isNotEmpty()) {
                    val checkImages = "is the main object in images is type of \"${
                        type.value.toString().trim().trimIndent()
                    }\"? if the question answer is yes then reply with only true word else reply false and translate the reason to ${getAppLanguage()} and reply with this reason"
                    val checkImagesResponse = generativeModel.generateContent(
                        content {
                            bitmap.forEach { image(it) }
                            text(checkImages)
                        }
                    )
                    checkImagesResponse.text?.let { outputContent ->

                        when (outputContent.trim().trimIndent().lowercase().contains("true")) {
                            true -> {}
                            false -> {
                                userDescriptionError.value = (outputContent)
                                _uiState.emit(UiState.Error("Check Images"))
                                return@launch
                            }
                        }
                    }


                    val generateDescriptionRequest =
                        "can you describe object in the image and return the description in string? translate it to ${getAppLanguage()}"
                    val generateDescriptionResponse = generativeModel.generateContent(
                        content {
                            bitmap.forEach { image(it) }
                            text(generateDescriptionRequest)
                        }
                    )
                    generateDescriptionResponse.text?.let { outputContent ->
                        imageDescription.value = outputContent
                    }
                }
                val checkType =
                    "is \"${type.value.toString()}\" valid type of an physical item that can be lost ? return only true or if false return the reason and translate it to ${getAppLanguage()}"
                val checkTypeResponse = generativeModel.generateContent(
                    content {
                        text(checkType)
                    }
                )
                checkTypeResponse.text?.let { outputContent ->

                    when (outputContent.trim().trimIndent().lowercase().contains("true")) {
                        true -> {}
                        false -> {
                            userDescriptionError.value = (outputContent)
                            _uiState.emit(UiState.Error("Check your inputs"))
                            return@launch
                        }
                    }
                }
                if (color1.value.toString().isNotEmpty()) {

                    val checkColor1 =
                        "is word \"${color1.value.toString()}\" a color ? return with true or if false return the reason and translate it to ${getAppLanguage()}"

                    val checkColor1Response = generativeModel.generateContent(
                        content {
                            text(checkColor1)
                        }
                    )
                    checkColor1Response.text?.let { outputContent ->

                        when (outputContent.trim().trimIndent().lowercase().contains("true")) {
                            true -> {}
                            false -> {
                                userDescriptionError.value = (outputContent)
                                _uiState.emit(UiState.Error("Check your inputs"))
                                return@launch
                            }
                        }
                    }
                }
                if (color2.value.toString().isNotEmpty()) {

                    val checkColor2 =
                        "is word \"${color2.value.toString()}\" a color ? return with true or if false return the reason in ${getAppLanguage()}"

                    val checkColor2Response = generativeModel.generateContent(
                        content {
                            text(checkColor2)
                        }
                    )
                    checkColor2Response.text?.let { outputContent ->
                        when (outputContent.trim().trimIndent().lowercase().contains("true")) {
                            true -> {}
                            false -> {
                                userDescriptionError.value = (outputContent)
                                _uiState.emit(UiState.Error("Check your inputs"))
                                return@launch
                            }
                        }
                    }
                }
                if (color3.value.toString().isNotEmpty()) {
                    val checkColor3 =
                        "is word \"${color3.value.toString()}\" a color ? return with true or if false return the reason and translate it to ${getAppLanguage()}"

                    val checkColor3Response = generativeModel.generateContent(
                        content {
                            text(checkColor3)
                        }
                    )
                    checkColor3Response.text?.let { outputContent ->

                        when (outputContent.trim().trimIndent().lowercase().contains("true")) {
                            true -> {}
                            false -> {
                                userDescriptionError.value = (outputContent)
                                _uiState.emit(UiState.Error("Check your inputs"))
                                return@launch
                            }
                        }
                    }
                }

                val checkUserDescription = if (strictDescription.value == true)
                    "is \"${userDescription.value.toString()}\" describing \"${type.value.toString()}\" ? then check is \"${userDescription.value.toString()}\" making sense ? return true or if false return the reason and translate it to ${getAppLanguage()}"
                else
                    "is \"${userDescription.value.toString()}\" making sense ? return true or if false return the reason and translate it to ${getAppLanguage()}"

                val checkUserDescriptionResponse = generativeModel.generateContent(
                    content {
                        text(checkUserDescription)
                    }
                )
                checkUserDescriptionResponse.text?.let { outputContent ->

                    when (outputContent.trim().trimIndent().lowercase().contains("true")) {
                        true -> {
                            _uiState.emit(UiState.Success(outputContent))
                        }

                        false -> {
                            userDescriptionError.value = (outputContent)
                            _uiState.emit(UiState.Error("Check your inputs"))
                            return@launch
                        }
                    }
                }


            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
                return@launch
            }

        }
    }

    fun translatePrompt(
        inputs: ItemLost,
        prompt: String,
    ) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val translateDescription = "can you translate the following \"${
                    userDescription.value.toString().trimIndent().trim()
                }\" to English and return it as string? if you are not able to translate return false word only or empty string"
                val translateDescriptionResponse = generativeModel.generateContent(
                    content {
                        text(translateDescription)
                    }
                )
                translateDescriptionResponse.text?.let { outputContent ->
                    when (outputContent.trim().trimIndent().lowercase().contains("false")) {
                        true -> {
                            _uiState.emit(UiState.Error("Check your inputs"))
                        }

                        false -> {
                            translatedDescription.value = outputContent
                            inputs.userDescription = userDescription.value
                        }
                    }

                }
                val inputsJson = Gson().toJson(inputs)
                val response = generativeModel.generateContent(
                    content {
                        text(inputsJson)
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->

                    try {
                        val data = convertJsonToDataClass(outputContent)
                        _uiState.emit(UiState.Success(data))
                        aiResponse = data!!
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
        userDescription: String,
        userResponse: ItemLost
    ) {
        colors.clear()
        colors.add(color1.value.toString())
        if (color2.value.toString().isNotEmpty())
            colors.add(color2.value.toString())
        if (color3.value.toString().isNotEmpty())
            colors.add(color3.value.toString())
        aiResponse.colors = colors
        aiResponse.userDescription = userDescription
        viewModelScope.launch {
            lostItemsRepositoryImpl.uploadLostItems(
                imageUris,
                addresses = addresses,
                aiResponse,
                userResponse = userResponse
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