package store.msolapps.flamingo.presentation.home.profile

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentCancelOrderBottomSheetBinding
import store.msolapps.flamingo.databinding.FragmentDeleteAccountBottomSheetBinding
import store.msolapps.flamingo.presentation.auth.AuthActivity

class CancelOrderBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCancelOrderBottomSheetBinding
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_cancel_order_bottom_sheet, container, false)

        binding.backIc.setOnClickListener { dismiss() }

        binding.cancelBtn.setOnClickListener { dismiss() }

        binding.deleteBtn.setOnClickListener {
            dismiss()
            findNavController().navigateUp()
        }

        return binding.root
    }

}