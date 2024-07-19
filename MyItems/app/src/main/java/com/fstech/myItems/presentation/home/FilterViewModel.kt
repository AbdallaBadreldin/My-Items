package store.msolapps.flamingo.presentation.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
) : ViewModel() {
    var minValue: Int? = null
    var maxValue: Int? = null
    var filterValue: String? = null

    fun resetFilter(){
        minValue= null
        maxValue= null
        filterValue= null
    }
}