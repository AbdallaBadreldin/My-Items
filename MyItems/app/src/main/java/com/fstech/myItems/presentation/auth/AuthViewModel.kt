package store.msolapps.flamingo.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.AuthSharedPreference
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.request.LoginRequest
import store.msolapps.domain.models.response.LoginResponse
import store.msolapps.domain.useCase.auth.LoginUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val asp: AuthSharedPreference,
    private val psp: ProfileSharedPreference
) : ViewModel() {
    //loginLiveData
    private val _isLoad = MutableLiveData<Boolean>(false)
    private val _login = MutableLiveData<LoginResponse>()
    private val _error = MutableLiveData<String>()
    val isLoad: LiveData<Boolean> get() = _isLoad
    val login: LiveData<LoginResponse> get() = _login
    val error: LiveData<String> get() = _error

    fun login(loginData: LoginRequest) {
        viewModelScope.launch {
            loginUseCase.invoke(loginData).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _login.postValue(requestStatus.data)
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

    fun isLoggedIn(): Boolean {
        return asp.isLogged()
    }

    fun setIsRememberMe(checked: Boolean) {
        asp.setIsRememberMe(checked)
    }

    fun isRememberMe() = asp.isRememberMe()
    fun isFirstOpen() = asp.isFirstOpen()
    fun logout() {
        asp.logout()
    }

    fun setLanguage(s: String) {
        psp.setLang(s)
    }
    fun getLanguage():String {
        return psp.getLang()
    }

    fun setAppIsOpened() {
        asp.setAppOpenedFirstTime()
    }
}