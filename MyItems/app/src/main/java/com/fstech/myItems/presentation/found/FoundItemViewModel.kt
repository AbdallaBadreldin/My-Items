package com.fstech.myItems.presentation.found

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel

class FoundItemViewModel: ViewModel()  {
    val list =  mutableStateListOf<Uri>()

    fun updateItem(itemIndex: Int, item: Uri) {
        list.set(itemIndex, item) // sets the element at 'itemIndex' to 'item'
    } // You can fill the list with initial values if you like, for testing

    fun addItem(uri: Uri?) {
list.add(uri!!)
    }
}