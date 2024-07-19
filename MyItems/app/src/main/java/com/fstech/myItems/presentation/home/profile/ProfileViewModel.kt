package store.msolapps.flamingo.presentation.home.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.AuthSharedPreference
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.request.LoginRequest
import store.msolapps.domain.models.response.CategoriesResponseModel
import store.msolapps.domain.models.response.GetDefaultAddressResponse
import store.msolapps.domain.models.response.LoginResponse
import store.msolapps.domain.models.response.LogoutResponseModel
import store.msolapps.domain.useCase.auth.LogoutUseCase
import store.msolapps.domain.useCase.cart.GetDefaultAddressUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getDefaultAddressUseCase: GetDefaultAddressUseCase,
    private val asp: AuthSharedPreference,
    private val psp: ProfileSharedPreference
) : ViewModel() {
    private val _isLoad = MutableLiveData<Boolean?>(false)
    private val _logout = MutableLiveData<LogoutResponseModel?>()
    private val _error = MutableLiveData<String?>()
    val isLoad: LiveData<Boolean?> get() = _isLoad
    val logout: LiveData<LogoutResponseModel?> get() = _logout
    val error: LiveData<String?> get() = _error

    private val _getAddressData = MutableLiveData<GetDefaultAddressResponse?>()
    private val _getAddressError = MutableLiveData<String>()
    private val _getAddressLoading = MutableLiveData<Boolean>(false)
    val getAddressData: LiveData<GetDefaultAddressResponse?> get() = _getAddressData
    val getAddressError: LiveData<String> get() = _getAddressError
    val getAddressLoading: LiveData<Boolean> get() = _getAddressLoading

    fun getDefaultAddress() {
        viewModelScope.launch {
            getDefaultAddressUseCase.invoke().collect() { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getAddressData.postValue(requestStatus.data)
                        _getAddressLoading.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _getAddressError.postValue(requestStatus.message)
                        _getAddressLoading.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _getAddressLoading.postValue(true)
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase.invoke(psp.getLang()).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _logout.postValue(requestStatus.data)
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

    fun clearObservers() {
        _logout.postValue(null)
        _error.postValue(null)
        _isLoad.postValue(null)
        _getAddressData.postValue(null)
    }

    fun logoutFromProfile() {
        asp.logout()
    }
}