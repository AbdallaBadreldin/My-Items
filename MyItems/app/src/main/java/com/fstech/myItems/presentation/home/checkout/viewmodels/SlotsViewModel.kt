package store.msolapps.flamingo.presentation.home.checkout.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.DayModel
import store.msolapps.domain.models.response.DataGetSlotsResponse
import store.msolapps.domain.models.response.GetSlotsResponse
import store.msolapps.domain.useCase.cart.GetSlotsUseCase
import store.msolapps.domain.utils.RequestStatus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SlotsViewModel @Inject constructor(
    private val getSlotsUseCase: GetSlotsUseCase,
    private val psp: ProfileSharedPreference,
) : ViewModel() {
    private var selectedSlot = 0
    var flag_id = 0
    var slot_date: String = ""
    var botFrag_slotsId = 0
    var botFrag_slotDate = ""
    var botFrag_flagId = 0
    private val _selectedDaySlotLiveData = MutableLiveData<DayModel?>(null)
    private val _selectedTimeSlotLiveData = MutableLiveData<DataGetSlotsResponse?>(null)
    private val _selectedDaySlotPositionLiveData = MutableLiveData<Int?>(0)
    private val _selectedTimeSlotPositionLiveData = MutableLiveData<Int?>(-1)

    val selectedDaySlotLiveData: LiveData<DayModel?> get() = _selectedDaySlotLiveData
    val selectedTimeSlotLiveData: LiveData<DataGetSlotsResponse?> get() =_selectedTimeSlotLiveData
    val selectedDaySlotPositionLiveData: LiveData<Int?> get() = _selectedDaySlotPositionLiveData
    val selectedTimeSlotPositionLiveData: LiveData<Int?> get() = _selectedTimeSlotPositionLiveData

    private val _getSlotsLoad = MutableLiveData<Boolean>(false)
    private val _getSlotsData = MutableLiveData<GetSlotsResponse>()
    private val _getSlotsError = MutableLiveData<String>()
    val getSlotsLoad: LiveData<Boolean> get() = _getSlotsLoad
    val getSlotsData: LiveData<GetSlotsResponse> get() = _getSlotsData
    val getSlotsError: LiveData<String> get() = _getSlotsError

    fun getSlots(flag_id: Int, slot_date: String) {
        viewModelScope.launch {
            getSlotsUseCase.invoke(flag_id = flag_id, slot_date = slot_date)
                .collect { requestStatus ->
                    when (requestStatus) {
                        is RequestStatus.Success -> {
                            _getSlotsData.postValue(requestStatus.data)
                            _getSlotsLoad.postValue(false)
                        }

                        is RequestStatus.Error -> {
                            _getSlotsError.postValue(requestStatus.message)
                            _getSlotsLoad.postValue(false)
                        }

                        is RequestStatus.Waiting -> {
                            _getSlotsLoad.postValue(true)
                        }
                    }
                }
        }
    }

    fun setSelectedSlot(slot: Int) {
        selectedSlot = slot
    }

    fun getSelectedSlot(): Int {
        return selectedSlot
    }

    fun getWeeklyDaysAndDates(): List<Pair<String, Date>> {
        val daysAndDatesList = mutableListOf<Pair<String, Date>>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val currentDate = calendar.time
        daysAndDatesList.add(Pair(dateFormat.format(currentDate), currentDate))
        for (i in 1 until 3) {
            calendar.add(Calendar.DAY_OF_WEEK, 1)
            val nextDate = calendar.time
            daysAndDatesList.add(Pair(dateFormat.format(nextDate), nextDate))
        }

        Log.d("TAG", "getWeeklyDaysAndDates: $daysAndDatesList")

        return daysAndDatesList
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return dateFormat.format(Date())
    }

    fun getCurrentMonthNameNumber(): Int {
        val dateFormat = SimpleDateFormat("M", Locale.ENGLISH)
        return dateFormat.format(Date()).toInt()
    }

    fun getCurrentDayNameNumber(): Int {
        val dateFormat = SimpleDateFormat("dd", Locale.ENGLISH)
        return dateFormat.format(Date()).toInt()
    }

    fun getTomorrowDate(): String {
        val dateFormat = SimpleDateFormat("dd/M/yyyy", Locale.ENGLISH)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1) // Add one day to the current date
        val tomorrow = calendar.time
        return dateFormat.format(tomorrow)
    }

    fun getTomorrowMonthNameNumber(): Int {
        val dateFormat = SimpleDateFormat("M", Locale.ENGLISH)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1) // Add one day to the current date
        val tomorrow = calendar.time
        return dateFormat.format(tomorrow).toInt()
    }

    fun getTomorrowDayNameNumber(): Int {
        val dateFormat = SimpleDateFormat("dd", Locale.ENGLISH)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1) // Add one day to the current date
        val tomorrow = calendar.time
        return dateFormat.format(tomorrow).toInt()
    }

    fun getAfterTomorrowDate(): String {
        val dateFormat = SimpleDateFormat("dd/M/yyyy", Locale.ENGLISH)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_WEEK, 2) // Add two days to the current date
        val tomorrow = calendar.time
        return dateFormat.format(tomorrow)
    }

    fun getAfterTomorrowMonthNameNumber(): Int {
        val dateFormat = SimpleDateFormat("M", Locale.ENGLISH)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_WEEK_IN_MONTH, 2) // Add two days to the current date
        val tomorrow = calendar.time
        return dateFormat.format(tomorrow).toInt()
    }

    fun getAfterTomorrowDayNameNumber(): Int {
        val dateFormat = SimpleDateFormat("dd", Locale.ENGLISH)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 2) // Add two days to the current date
        val tomorrow = calendar.time
        return dateFormat.format(tomorrow).toInt()
    }

    fun getLanguage(): String = psp.getLang()
    fun postSelectedTimeSlot(mSelectedItem: DataGetSlotsResponse?) {
        _selectedTimeSlotLiveData.postValue(mSelectedItem)
    }
    fun postSelectedTimeSlotPosition(mSelectedItem: Int?) {
        _selectedTimeSlotPositionLiveData.postValue(mSelectedItem)
    }

    fun postSelectedDaySlot(selectedItem: DayModel?) {
        _selectedDaySlotLiveData.postValue(selectedItem)
    }
    fun postSelectedDaySlotPosition(selectedItem: Int?) {
        _selectedDaySlotPositionLiveData.postValue(selectedItem)
    }
    fun clearObserver(){
        _selectedTimeSlotLiveData.postValue(null)
        _selectedTimeSlotPositionLiveData.value = null
        _selectedDaySlotLiveData.value = null
        _selectedDaySlotPositionLiveData.value =null
    }
}