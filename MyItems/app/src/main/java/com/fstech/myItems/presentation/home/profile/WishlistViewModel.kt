package store.msolapps.flamingo.presentation.home.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.response.AddToFavourite.AddToFavouriteResponseModel
import store.msolapps.domain.models.response.CartResponseModel
import store.msolapps.domain.models.response.FavouriteResponseModel
import store.msolapps.domain.useCase.cart.GetCartUseCase
import store.msolapps.domain.useCase.profile.AddAllWishlistUseCase
import store.msolapps.domain.useCase.profile.getWishlistUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val getWishlistUseCase: getWishlistUseCase,
    private val addAllWishlistUseCase: AddAllWishlistUseCase,
    private val getCartUseCase: GetCartUseCase,
    private val psp: ProfileSharedPreference
) : ViewModel() {
    private val _isLoadWishlist = MutableLiveData<Boolean>(false)
    private val _getWishlist = MutableLiveData<FavouriteResponseModel?>()
    private val _errorWishlist = MutableLiveData<String>()
    val isLoadWishlist: LiveData<Boolean> get() = _isLoadWishlist
    val getWishlist: LiveData<FavouriteResponseModel?> get() = _getWishlist
    val errorWishlist: LiveData<String> get() = _errorWishlist

    private val _isLoadCart = MutableLiveData<Boolean>(false)
    private val _cart = MutableLiveData<CartResponseModel?>()
    private val _errorCart = MutableLiveData<String>()
    val isLoadCart: LiveData<Boolean> get() = _isLoadCart
    val cart: LiveData<CartResponseModel?> get() = _cart
    val errorCart: LiveData<String> get() = _errorCart

    private val _isLoadAddAllCart = MutableLiveData<Boolean>(false)
    private val _addAllcart = MutableLiveData<AddToFavouriteResponseModel?>()
    private val _errorAddAllCart = MutableLiveData<String>()
    val isLoadAddAllCart: LiveData<Boolean> get() = _isLoadAddAllCart
    val addAllcart: LiveData<AddToFavouriteResponseModel?> get() = _addAllcart
    val errorAddAllCart: LiveData<String> get() = _errorAddAllCart
    fun getWishlist() {
        viewModelScope.launch {
            getWishlistUseCase.invoke(psp.getLang()).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getWishlist.postValue(requestStatus.data)
                        _isLoadWishlist.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _errorWishlist.postValue(requestStatus.message)
                        _isLoadWishlist.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoadWishlist.postValue(true)
                    }
                }
            }
        }
    }

    /*fun addAllWishlist() {
        viewModelScope.launch {
            addAllWishlistUseCase.invoke("01").collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _isLoadWishlist.postValue(false)
                        *//*getCartData()
                        getWishlist()*//*
                    }

                    is RequestStatus.Error -> {
                        _errorWishlist.postValue(requestStatus.message)
                        _isLoadWishlist.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoadWishlist.postValue(true)
                    }
                }
            }
        }
    }*/
    fun addAllWishlist() {
        viewModelScope.launch {
            addAllWishlistUseCase.invoke("01").collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _isLoadAddAllCart.postValue(false)
                        _addAllcart.postValue(requestStatus.data)
                        getCartData()
                        getWishlist()
                    }

                    is RequestStatus.Error -> {
                        _errorAddAllCart.postValue(requestStatus.message)
                        _isLoadAddAllCart.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoadAddAllCart.postValue(true)
                    }
                }
            }
        }
    }

    fun getCartData() {
        viewModelScope.launch {
            getCartUseCase.invoke().collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _cart.postValue(requestStatus.data)
                        _isLoadCart.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _errorCart.postValue(requestStatus.message)
                        _isLoadCart.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoadCart.postValue(true)
                    }
                }
            }
        }
    }

    fun clearObserver() {
        _getWishlist.postValue(null)
        _cart.postValue(null)
        _addAllcart.postValue(null)
    }
}