package store.msolapps.flamingo.presentation.home.profile

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.ChangeLangBottomSheetBinding
import store.msolapps.flamingo.databinding.FragmentCancelOrderBottomSheetBinding
import store.msolapps.flamingo.databinding.FragmentDeleteAccountBottomSheetBinding
import store.msolapps.flamingo.presentation.auth.AuthActivity
import store.msolapps.flamingo.presentation.auth.AuthViewModel
import store.msolapps.flamingo.presentation.home.MainActivity

class ChangeLangBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: ChangeLangBottomSheetBinding
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    private val viewModel: AuthViewModel by activityViewModels()

    private var lang = -1

    @SuppressLint("UseCompatLoadingForDrawables", "ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil
                .inflate(
                    layoutInflater,
                    R.layout.change_lang_bottom_sheet,
                    container,
                    false
                )

        binding.backIc.setOnClickListener { dismiss() }
        binding.ENGLISH.background = requireActivity().getDrawable(R.drawable.border_whitish_shadow)
        binding.ARABIC.background = requireActivity().getDrawable(R.drawable.border_whitish_shadow)

        if (viewModel.getLanguage() == "en") {
            binding.radioButtonEnglish.isChecked = true
        } else {
            binding.radioButtonArabic.isChecked = true
        }
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables", "ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.radioButtonEnglish.setOnClickListener {
            binding.ENGLISH.background = requireActivity().getDrawable(R.drawable.border_dark_blue)
            binding.ARABIC.background =
                requireActivity().getDrawable(R.drawable.border_whitish_shadow)

            binding.langName.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
            binding.titleLang.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))

            binding.langNameAr.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.black
                )
            )
            binding.titleLangAr.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.white_shadow
                )
            )

            binding.radioButtonArabic.isChecked = false
            lang = 1
        }
        binding.radioButtonArabic.setOnClickListener {
            binding.ENGLISH.background =
                requireActivity().getDrawable(R.drawable.border_whitish_shadow)
            binding.ARABIC.background = requireActivity().getDrawable(R.drawable.border_dark_blue)

            binding.langNameAr.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.white
                )
            )
            binding.titleLangAr.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.white
                )
            )

            binding.langName.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black))
            binding.titleLang.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.white_shadow
                )
            )

            binding.radioButtonEnglish.isChecked = false
            lang = 2
        }
        if (binding.radioButtonEnglish.isChecked) {
            binding.ENGLISH.background = requireActivity().getDrawable(R.drawable.border_dark_blue)
            binding.langName.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
            binding.titleLang.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
            lang = 1
        } else {
            binding.ARABIC.background = requireActivity().getDrawable(R.drawable.border_dark_blue)
            binding.langNameAr.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.white
                )
            )
            binding.titleLangAr.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.white
                )
            )
            lang = 2
        }
        binding.applyBottomSheetBtn.setOnClickListener {
            dismiss()
            if (lang == 1) {
                viewModel.setLanguage("en")
            } else {
                viewModel.setLanguage("ar")
            }
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            activity?.finish()
        }
    }
}