package store.msolapps.flamingo.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.AuthSharedPreference
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.request.ForgetPasswordRequest
import store.msolapps.domain.models.response.ForgetPasswordResponse
import store.msolapps.domain.useCase.auth.ForgetPasswordUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(
    private val forgetPasswordUseCase: ForgetPasswordUseCase,
    private val psp: ProfileSharedPreference
) : ViewModel() {
    private val _isLoad = MutableLiveData<Boolean>(false)
    private val _error = MutableLiveData<String>()
    private val _forgetPassword = MutableLiveData<ForgetPasswordResponse>()
    val isLoad: LiveData<Boolean> get() = _isLoad
    val error: LiveData<String> get() = _error
    val forgetPassword: LiveData<ForgetPasswordResponse> get() = _forgetPassword

    fun forgetPassword(email: String) {
        viewModelScope.launch {
            forgetPasswordUseCase.invoke(
                forgetPasswordRequest = ForgetPasswordRequest(
                    email,
                    psp.getLang()
                )
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _forgetPassword.postValue(requestStatus.data)
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