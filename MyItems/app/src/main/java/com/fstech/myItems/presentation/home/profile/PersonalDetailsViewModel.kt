package store.msolapps.flamingo.presentation.home.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import store.msolapps.data.sp.AuthSharedPreference
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.request.LoginRequest
import store.msolapps.domain.models.response.DeleteImageResponse
import store.msolapps.domain.models.response.LoginResponse
import store.msolapps.domain.models.response.LogoutResponseModel
import store.msolapps.domain.models.response.ProfileUpdateResponseModel
import store.msolapps.domain.models.response.UserData
import store.msolapps.domain.useCase.auth.DeleteImageUseCase
import store.msolapps.domain.useCase.auth.GetUserDataUseCase
import store.msolapps.domain.useCase.auth.LogoutUseCase
import store.msolapps.domain.useCase.auth.UpdateProfileUseCase
import store.msolapps.domain.utils.RequestStatus
import store.msolapps.flamingo.util.Event
import javax.inject.Inject

@HiltViewModel
class PersonalDetailsViewModel @Inject constructor(
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val deleteImageUseCase: DeleteImageUseCase,
) : ViewModel() {
    private val _isLoad = MutableLiveData<Boolean>(false)
    private val _updateProfile = MutableLiveData<Event<ProfileUpdateResponseModel>>()
    private val _error = MutableLiveData<String>()
    val isLoad: LiveData<Boolean> get() = _isLoad
    val updateProfile: LiveData<Event<ProfileUpdateResponseModel>> get() = _updateProfile
    val error: LiveData<String> get() = _error

    private val _isLoadData = MutableLiveData(false)
    private val _profileData = MutableLiveData<UserData>()
    private val _errorData = MutableLiveData<String>()
    val isLoadData: LiveData<Boolean> get() = _isLoadData
    val profileData: LiveData<UserData> get() = _profileData
    val errorData: LiveData<String> get() = _errorData

    private val _isLoadDeleteImage = MutableLiveData(false)
    private val _deleteImage = MutableLiveData<DeleteImageResponse?>()
    private val _errorDeleteImage = MutableLiveData<String>()
    val isLoadDeleteImage: LiveData<Boolean> get() = _isLoadDeleteImage
    val deleteImage: LiveData<DeleteImageResponse?> get() = _deleteImage
    val errorDeleteImage: LiveData<String> get() = _errorDeleteImage


    fun updateProfile(
        name: String,
        email: String,
        image: MultipartBody.Part?,
        birth_date: String?
    ) {
        viewModelScope.launch {
            updateProfileUseCase.invoke(name, email, image, birth_date).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _updateProfile.postValue(Event(requestStatus.data))
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _error.postValue(requestStatus.message)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoad.postValue(true)
                    }
                }
            }
        }
    }

    fun getUserData() {
        viewModelScope.launch {
            getUserDataUseCase.invoke().collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _profileData.postValue(requestStatus.data)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _errorData.postValue(requestStatus.message)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoad.postValue(true)
                    }
                }
            }
        }
    }

    fun deleteImage() {
        viewModelScope.launch {
            deleteImageUseCase.invoke().collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _deleteImage.postValue(requestStatus.data)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _errorDeleteImage.postValue(requestStatus.message)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoadDeleteImage.postValue(true)
                    }
                }
            }
        }
    }

    fun clearObservers() {
        _deleteImage.postValue(null)

    }
}