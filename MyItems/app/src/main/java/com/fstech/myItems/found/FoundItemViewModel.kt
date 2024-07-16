package com.fstech.myItems.found

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class FoundItemViewModel: ViewModel()  {
    var capturedImageUri = mutableStateOf<Uri>(Uri.EMPTY)

}