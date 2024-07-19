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
import store.msolapps.domain.useCase.profile.GetMyOrdersCancelledUseCase
import store.msolapps.domain.useCase.profile.GetMyOrdersDeliveredUseCase
import store.msolapps.domain.useCase.profile.GetMyOrdersOnTheWayUseCase
import store.msolapps.domain.useCase.profile.GetMyOrdersPreparingUseCase
import store.msolapps.domain.useCase.profile.GetMyOrdersWaitingUseCase
import store.msolapps.domain.useCase.profile.ReorderUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

@HiltViewModel
class MyOrderHistoryViewModel @Inject constructor(
    private val getMyOrdersCancelledUseCase: GetMyOrdersCancelledUseCase,
    private val getMyOrdersDeliveredUseCase: GetMyOrdersDeliveredUseCase,
    private val getMyOrdersOnTheWayUseCase: GetMyOrdersOnTheWayUseCase,
    private val getMyOrdersPreparingUseCase: GetMyOrdersPreparingUseCase,
    private val getMyOrdersWaitingUseCase: GetMyOrdersWaitingUseCase,
    private val reorderUseCase: ReorderUseCase,
    private val psp: ProfileSharedPreference
) : ViewModel() {

    private val _myOrdersDelivered = MutableLiveData<OrderResponseModel1?>()
    private val _errorDelivered = MutableLiveData<String>()
    val myOrdersDelivered: LiveData<OrderResponseModel1?> get() = _myOrdersDelivered
    val errorDelivered: LiveData<String> get() = _errorDelivered

    private val _myOrdersCancelled = MutableLiveData<OrderResponseModel1?>()
    private val _errorCancelled = MutableLiveData<String>()
    val myOrdersCancelled: LiveData<OrderResponseModel1?> get() = _myOrdersCancelled
    val errorCancelled: LiveData<String> get() = _errorCancelled

    private val _myOrdersOnTheWay = MutableLiveData<OrderResponseModel1?>()
    private val _errorOnTheWay = MutableLiveData<String>()
    val myOrdersOnTheWay: LiveData<OrderResponseModel1?> get() = _myOrdersOnTheWay
    val errorOnTheWay: LiveData<String> get() = _errorOnTheWay

    private val _myOrdersPreparing = MutableLiveData<OrderResponseModel1?>()
    private val _errorOnPreparing = MutableLiveData<String>()
    val myOrdersPreparing: LiveData<OrderResponseModel1?> get() = _myOrdersPreparing
    val errorOnPreparing: LiveData<String> get() = _errorOnPreparing

    private val _myOrdersWaiting = MutableLiveData<OrderResponseModel1?>()
    private val _errorOnWaiting = MutableLiveData<String>()
    val myOrdersWaiting: LiveData<OrderResponseModel1?> get() = _myOrdersWaiting
    val errorOnWaiting: LiveData<String> get() = _errorOnWaiting


    private val _isLoad = MutableLiveData<Boolean>(false)

    private val _reorderStatus = MutableLiveData<ReOrderResponse>()
    private val _errorReorder = MutableLiveData<String>()
    val reorderStatus: LiveData<ReOrderResponse> get() = _reorderStatus

    val isLoad: LiveData<Boolean> get() = _isLoad


    var currentSelection = 4

    fun getMyOrdersCancelled(
        page: Int,
        paginate: Int,
    ) {
        viewModelScope.launch {
            getMyOrdersCancelledUseCase.invoke(
                psp.getLang(),
                "01",
                page,
                paginate
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _myOrdersCancelled.postValue(requestStatus.data)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _errorCancelled.postValue(requestStatus.message)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoad.postValue(true)
                    }
                }
            }
        }
    }

    fun getMyOrdersDelivered(
        page: Int,
        paginate: Int
    ) {
        viewModelScope.launch {
            getMyOrdersDeliveredUseCase.invoke(
                psp.getLang(),
                "01",
                page,
                paginate
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _myOrdersDelivered.postValue(requestStatus.data)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _errorDelivered.postValue(requestStatus.message)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoad.postValue(true)
                    }
                }
            }
        }
    }

    fun getMyOrdersOnTheWay(
        page: Int,
        paginate: Int
    ) {
        viewModelScope.launch {
            getMyOrdersOnTheWayUseCase.invoke(
                psp.getLang(),
                "01",
                page,
                paginate
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _myOrdersOnTheWay.postValue(requestStatus.data)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _errorOnTheWay.postValue(requestStatus.message)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoad.postValue(true)
                    }
                }
            }
        }
    }

    fun getMyOrdersPreparing(
        page: Int,
        paginate: Int
    ) {
        viewModelScope.launch {
            getMyOrdersPreparingUseCase.invoke(
                psp.getLang(),
                "01",
                page,
                paginate
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _myOrdersPreparing.postValue(requestStatus.data)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _errorOnPreparing.postValue(requestStatus.message)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoad.postValue(true)
                    }
                }
            }
        }
    }

    fun getMyOrdersWaiting(
        page: Int,
        paginate: Int
    ) {
        viewModelScope.launch {
            getMyOrdersWaitingUseCase.invoke(
                psp.getLang(),
                "01",
                page,
                paginate
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _myOrdersWaiting.postValue(requestStatus.data)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _errorOnWaiting.postValue(requestStatus.message)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoad.postValue(true)
                    }
                }
            }
        }
    }


    fun reorder(
        orderId: Int
    ) {
        viewModelScope.launch {
            reorderUseCase.invoke(
                ReOrderRequest(orderId),
                psp.getLang(),
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _reorderStatus.postValue(requestStatus.data)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _errorOnTheWay.postValue(requestStatus.message)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoad.postValue(true)
                    }
                }
            }
        }
    }
    fun clearObservers(){
        _myOrdersWaiting.postValue(null)
        _myOrdersPreparing.postValue(null)
        _myOrdersOnTheWay.postValue(null)
        _myOrdersDelivered.postValue(null)
        _myOrdersCancelled.postValue(null)

    }
}