package store.msolapps.flamingo.presentation.auth

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.AuthSharedPreference
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.request.IsPhoneTakenRequest
import store.msolapps.domain.models.request.SignupRequest
import store.msolapps.domain.models.response.SignupResponse
import store.msolapps.domain.models.smsmasr.SMSMasrRequest
import store.msolapps.domain.models.smsmasr.SMSMasrResponse
import store.msolapps.domain.models.smsmasr.SMSMasrValidateRequest
import store.msolapps.domain.useCase.auth.IsPhoneTakenUseCase
import store.msolapps.domain.useCase.auth.RegisterUseCase
import store.msolapps.domain.useCase.auth.SendSmsUseCase
import store.msolapps.domain.useCase.auth.VerifySmsCodeUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val isPhoneTakenUseCase: IsPhoneTakenUseCase,
    private val registerUseCase: RegisterUseCase,
    private val sendSmsUseCase: SendSmsUseCase,
    private val verifySmsCodeUseCase: VerifySmsCodeUseCase,
    private val asp: AuthSharedPreference,
    private val psp: ProfileSharedPreference
) : ViewModel() {
    lateinit var phone: String
//    lateinit var token: PhoneAuthProvider.ForceResendingToken
//    lateinit var verificationId: String

    //loginLiveData
    private val _isLoad = MutableLiveData<Boolean?>(false)
    private var _error = MutableLiveData<String?>()
    private val _isPhoneTaken = MutableLiveData<Boolean?>()
    private val _register = MutableLiveData<SignupResponse?>()
    val isLoad: MutableLiveData<Boolean?> get() = _isLoad
    val error: MutableLiveData<String?> get() = _error
    val isPhoneTaken: MutableLiveData<Boolean?> get() = _isPhoneTaken
    val register: MutableLiveData<SignupResponse?> get() = _register

    fun isLoggedIn(): Boolean {
        return asp.isLogged()
    }

    fun setIsRememberMe(checked: Boolean) {
        asp.setIsRememberMe(checked)
    }

    fun isRememberMe() = asp.isRememberMe()
    fun logout() {
        asp.clearAllCache()
    }

    fun checkIfPhoneTaken(phone: String) {
        viewModelScope.launch {
            isPhoneTakenUseCase.invoke(
                isPhoneTakenRequest = IsPhoneTakenRequest(
                    psp.getLang(), phone
                )
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _isPhoneTaken.postValue(requestStatus.data)
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

    fun register(signupRequest: SignupRequest) {
        viewModelScope.launch {
            registerUseCase.invoke(
                signupRequest = signupRequest
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _register.postValue(requestStatus.data)
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

    fun isPhoneValid(phone: String): Boolean {
        return if (phone.isEmpty()) false
        else if (phone.length != 11) false
        else if (phone.startsWith("018") || phone.startsWith("015") || phone.startsWith("014") || phone.startsWith(
                "011"
            ) || phone.startsWith("012") || phone.startsWith("010") || phone.startsWith("017")
        ) true
        else false
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length > 6
    }

    fun isTwoPasswordIdentical(password1: String, password2: String): Boolean {
        return password1 == password2
    }

    fun isNameValid(name: String): Boolean {
        return name.length > 2
    }

    fun getLanguage(): String = psp.getLang()

    //sendSmsMasr
    private val _SmsMasrIsLoad = MutableLiveData<Boolean>(false)
    private val _SmsMasrError = MutableLiveData<String>()
    private val _SmsMasrData = MutableLiveData<SMSMasrResponse>()
    val SmsMasrIsLoad: LiveData<Boolean> get() = _SmsMasrIsLoad
    val SmsMasrError: LiveData<String> get() = _SmsMasrError
    val SmsMasrData: LiveData<SMSMasrResponse> get() = _SmsMasrData
    fun sendSmsMasr(phone: String) {
        viewModelScope.launch {
            val smsMasrRequest = SMSMasrRequest(psp.getLang(), phone)
            sendSmsUseCase.invoke(
                smsMasrRequest
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _SmsMasrData.postValue(requestStatus.data)
                        _SmsMasrIsLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _SmsMasrError.postValue(requestStatus.message)
                        _SmsMasrIsLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _SmsMasrIsLoad.postValue(true)
                    }
                }
            }
        }
    }

    //validateSmsMasrCode
    private val _validateSmsMasrIsLoad = MutableLiveData<Boolean?>(false)
    private val _validateSmsMasrError = MutableLiveData<String?>()
    private val _validateSmsMasrData = MutableLiveData<SMSMasrResponse?>()
    val validateSmsMasrIsLoad: MutableLiveData<Boolean?> get() = _validateSmsMasrIsLoad
    val validateSmsMasrError: MutableLiveData<String?> get() = _validateSmsMasrError
    val validateSmsMasrData: MutableLiveData<SMSMasrResponse?> get() = _validateSmsMasrData
    fun veriftSmsMasrCode(code: String) {
        viewModelScope.launch {
            val req = SMSMasrValidateRequest(code = code, psp.getLang(), phone = "2$phone")
            verifySmsCodeUseCase.invoke(
                req
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _validateSmsMasrData.postValue(requestStatus.data)
                        _validateSmsMasrIsLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _validateSmsMasrError.postValue(requestStatus.message)
                        _validateSmsMasrIsLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _validateSmsMasrIsLoad.postValue(true)
                    }
                }
            }
        }
    }

    fun resetLiveData() {
        _isLoad.value = null
        _error.value = null
        _isPhoneTaken.value = null
        _register.value = null

        _validateSmsMasrIsLoad.value = null
        _validateSmsMasrError.value = null
        _validateSmsMasrData.value = null
    }
}