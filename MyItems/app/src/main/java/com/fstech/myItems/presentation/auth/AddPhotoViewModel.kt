package store.msolapps.flamingo.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import store.msolapps.domain.models.response.ProfileUpdateResponseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import store.msolapps.data.sp.AuthSharedPreference
import store.msolapps.domain.useCase.auth.UpdateProfileUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

@HiltViewModel
class AddPhotoViewModel @Inject constructor(
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val asp: AuthSharedPreference
) : ViewModel() {
    //loginLiveData
    private val _isLoad = MutableLiveData<Boolean>(false)
    private val _updateProfile = MutableLiveData<ProfileUpdateResponseModel>()
    private val _error = MutableLiveData<String>()
    val isLoad: LiveData<Boolean> get() = _isLoad
    val updateProfile: LiveData<ProfileUpdateResponseModel> get() = _updateProfile
    val error: LiveData<String> get() = _error

    fun updateProfile(
        name: String,
        email: String,
        image: MultipartBody.Part?,
        birth_date: String?
    ) {
        viewModelScope.launch {
            updateProfileUseCase.invoke(name,email,image,birth_date).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _updateProfile.postValue(requestStatus.data)
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
}