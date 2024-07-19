package store.msolapps.flamingo.presentation.home.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.AuthSharedPreference
import store.msolapps.domain.models.request.LoginRequest
import store.msolapps.domain.models.response.LoginResponse
import store.msolapps.domain.models.response.LogoutResponseModel
import store.msolapps.domain.useCase.auth.LogoutUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

@HiltViewModel
class DeleteAccountBottomSheetViewModel @Inject constructor(
    private val asp: AuthSharedPreference
) : ViewModel() {
    fun logoutFromProfile() {
        asp.logout()
    }
}