package store.msolapps.flamingo.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.AuthSharedPreference
import store.msolapps.domain.models.request.AddAddressPostModel
import store.msolapps.domain.models.response.AddAddressResponseModel
import store.msolapps.domain.models.response.DeleteAddressResponseModel
import store.msolapps.domain.useCase.auth.AddAddressUseCase
import store.msolapps.domain.useCase.profile.EditAddressesUseCase
import store.msolapps.domain.utils.RequestStatus
import store.msolapps.flamingo.util.Event
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val addAddressUseCase: AddAddressUseCase,
    private val localDataSource: AuthSharedPreference,
    private val editAddressesUseCase: EditAddressesUseCase
) : ViewModel() {
    private val _isLoad = MutableLiveData<Boolean>(false)
    private val _addAddress = MutableLiveData<AddAddressResponseModel?>()
    private val _error = MutableLiveData<String?>()
    val isLoad: LiveData<Boolean> get() = _isLoad
    val addAddress: LiveData<AddAddressResponseModel?> get() = _addAddress
    val error: LiveData<String?> get() = _error

    private val _isLoadEdit = MutableLiveData<Boolean>(false)
    private val _editAddress = MutableLiveData<Event<DeleteAddressResponseModel?>>()
    private val _errorEdit = MutableLiveData<String>()
    val isLoadEdit: LiveData<Boolean> get() = _isLoadEdit
    val editAddress: LiveData<Event<DeleteAddressResponseModel?>> get() = _editAddress
    val errorEdit: LiveData<String> get() = _errorEdit
    fun addAddress(
        addAddressPostModel: AddAddressPostModel
    ) {
        viewModelScope.launch {
            addAddressUseCase.invoke(addAddressPostModel).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _addAddress.postValue(requestStatus.data)
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

    fun editAddress(
        addressId: String,
        addAddressPostModel: AddAddressPostModel
    ) {
        viewModelScope.launch {
            editAddressesUseCase.invoke(addressId, addAddressPostModel).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _editAddress.postValue(Event(requestStatus.data))
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _errorEdit.postValue(requestStatus.message)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoad.postValue(true)
                    }
                }
            }
        }
    }

    fun clearObeservers(){
        _addAddress.postValue(null)
        _error.postValue(null)
    }
    fun getUsername() = localDataSource.getUserName()
}