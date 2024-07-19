package com.fstech.myItems.generalClasses

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.msolapps.oscar.GeneralClasses.GetFilePath

class FilesAssistant {
    fun getFilePath(uri: Uri, activity: FragmentActivity):String{
        return GetFilePath.getRealPath(activity, uri)!!
    }
}