package store.msolapps.flamingo.util

import android.app.AlertDialog
import android.content.Context
import store.msolapps.flamingo.R

class Dialogs(
    private val context: Context
) {
    fun showOkDialog(title: String, message: String) {
        val alertDialog: AlertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.ok)
        ) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

}