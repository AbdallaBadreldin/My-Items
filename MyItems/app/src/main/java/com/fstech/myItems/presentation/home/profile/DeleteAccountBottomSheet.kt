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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentDeleteAccountBottomSheetBinding
import store.msolapps.flamingo.presentation.auth.AuthActivity

class DeleteAccountBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDeleteAccountBottomSheetBinding
    private val viewModel: DeleteAccountBottomSheetViewModel by activityViewModels()

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_delete_account_bottom_sheet, container, false)

        binding.backIc.setOnClickListener { dismiss() }

        binding.cancelBtn.setOnClickListener { dismiss() }

        binding.deleteBtn.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://flamingoapis.msol.dev/remove-account/23")))
            viewModel.logoutFromProfile()
            requireActivity().finishAffinity()
            dismiss()
        }

        return binding.root
    }

}