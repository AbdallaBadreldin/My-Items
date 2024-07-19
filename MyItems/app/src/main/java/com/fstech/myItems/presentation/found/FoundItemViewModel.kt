package com.fstech.myItems.presentation.found

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class FoundItemViewModel: ViewModel()  {
    var capturedImageUri = mutableStateOf<Uri>(Uri.EMPTY)

}