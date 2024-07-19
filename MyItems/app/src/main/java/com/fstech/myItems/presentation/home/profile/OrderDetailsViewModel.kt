package store.msolapps.flamingo.presentation.home.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.request.ReOrderRequest
import store.msolapps.domain.models.response.OrderResponseModel1
import store.msolapps.domain.models.response.ReOrderResponse
import store.msolapps.domain.models.response.ShowOrderDetailsResponse
import store.msolapps.domain.useCase.profile.GetMyOrdersCancelledUseCase
import store.msolapps.domain.useCase.profile.GetMyOrdersDeliveredUseCase
import store.msolapps.domain.useCase.profile.GetMyOrdersOnTheWayUseCase
import store.msolapps.domain.useCase.profile.ReorderUseCase
import store.msolapps.domain.useCase.profile.ShowOrderDetailsUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val orderDetailsUseCase: ShowOrderDetailsUseCase,
    private val psp: ProfileSharedPreference
) : ViewModel() {

    private val _orderDetails = MutableLiveData<ShowOrderDetailsResponse?>()
    private val _errorOrderDetails = MutableLiveData<String>()
    private val _isLoad = MutableLiveData<Boolean>()
    val orderDetails: LiveData<ShowOrderDetailsResponse?> get() = _orderDetails
    val errorOrderDetails: LiveData<String> get() = _errorOrderDetails
    val isLoad: LiveData<Boolean> get() = _isLoad


    fun showOrderDetails(
        id: String,
    ) {
        viewModelScope.launch {
            orderDetailsUseCase.invoke(
                id,
                psp.getLang(),
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _orderDetails.postValue(requestStatus.data)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _errorOrderDetails.postValue(requestStatus.message)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoad.postValue(true)
                    }
                }
            }
        }
    }
    fun clearObserver(){
        _orderDetails.postValue(null)
    }
}