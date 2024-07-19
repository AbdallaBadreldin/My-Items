package store.msolapps.flamingo.presentation.home.checkout.actions

import store.msolapps.domain.models.response.DataGetSlotsResponse

interface TimeAdapterActions {
    fun onClickTimeAdapterItem(position: Int, item: DataGetSlotsResponse)
    fun setDataToViewModel(position: Int, item: DataGetSlotsResponse)
}