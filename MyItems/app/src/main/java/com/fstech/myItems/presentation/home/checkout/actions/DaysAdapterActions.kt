package store.msolapps.flamingo.presentation.home.checkout.actions

import store.msolapps.domain.models.DayModel

interface DaysAdapterActions {
    fun updateBottomSlotRecycler(date:String)
    fun onClickDaysAdapter(date:DayModel , position:Int)
}