package store.msolapps.flamingo.presentation.home.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentProfileBinding
import store.msolapps.flamingo.databinding.FragmentSettingsBinding
import store.msolapps.flamingo.presentation.auth.AuthActivity
import store.msolapps.flamingo.presentation.auth.AuthViewModel
import store.msolapps.flamingo.presentation.home.MainActivity

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val viewModel: AuthViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        setupListeners()
        return binding.root
    }

    fun setupListeners() {
        binding.linearLayout9.setOnClickListener {
            val deleteAccountBottomSheet = DeleteAccountBottomSheet()
            deleteAccountBottomSheet.show(parentFragmentManager, deleteAccountBottomSheet.tag)
        }
        binding.backIc.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.parentLang.setOnClickListener {
            val changeLangBottomSheet = ChangeLangBottomSheet()
            changeLangBottomSheet.show(parentFragmentManager, changeLangBottomSheet.tag)
        }
        if (viewModel.getLanguage() == "en") {
            binding.langNameTv.setText("English")
        } else {
            binding.langNameTv.setText("العربية")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}