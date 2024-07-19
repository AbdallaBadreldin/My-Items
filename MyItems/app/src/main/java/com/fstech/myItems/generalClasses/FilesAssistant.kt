package com.msolapps.oscar.GeneralClasses

import android.net.Uri
import androidx.fragment.app.FragmentActivity

class FilesAssistant {
    fun getFilePath(uri: Uri, activity: FragmentActivity):String{
        return GetFilePath.getRealPath(activity,uri)!!
    }
}