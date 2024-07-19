/*Author ِAbdalla Badr
@abdalla-badreldin
Abdalla.badr852@gmail.com*/
package store.msolapps.flamingo.util

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.fragment.app.FragmentActivity

class Utilities {
    companion object {

        fun numbersEnglishToArabic(str: String): String {
            var result = ""
            var en = '0'
            for (ch in str) {
                en = ch
                when (ch) {
                    '0' -> en = '۰'
                    '1' -> en = '۱'
                    '2' -> en = '٢'
                    '3' -> en = '۳'
                    '4' -> en = '٤'
                    '5' -> en = '۵'
                    '6' -> en = '٦'
                    '7' -> en = '۷'
                    '8' -> en = '۸'
                    '9' -> en = '۹'
                }
                result = "${result}$en"
            }
            return result
        }



    }
}