package store.msolapps.flamingo.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import store.msolapps.flamingo.R

class DeleteConfirmationDialog(context: Context,listener:DeleteConfirmationDialogActions) : Dialog(context) {

    init {
        // Set the dialog properties
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_delete_confirmation)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        setCancelable(false)

        // Find the views
        val btnDelete = findViewById<TextView>(R.id.btnDelete)
        val btnCancel = findViewById<TextView>(R.id.btnCancel)

        // Set the button click listeners
        btnDelete.setOnClickListener {
            // Handle the delete action
            listener.onClickButtonDelete()

            //then dismiss the dialog
            dismiss()
        }

        btnCancel.setOnClickListener {
            // Handle the cancel action
            dismiss()
        }
    }
}