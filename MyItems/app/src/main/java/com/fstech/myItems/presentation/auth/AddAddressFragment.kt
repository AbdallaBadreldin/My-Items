package store.msolapps.flamingo.presentation.auth

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import store.msolapps.domain.models.request.AddAddressPostModel
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentAddAddressBinding
import store.msolapps.flamingo.presentation.auth.adapters.SuggestAddAddressAdapter
import store.msolapps.flamingo.presentation.home.MainActivity

private const val TAG = "FragmentAddAddress_TAG"

class AddAddressFragment(
    private var fromHome: Boolean? = false
) : BaseFragment(), FragmentAddAddressNavigator {
    private lateinit var addressData: AddressData
    private val viewModel: AddAddressViewModel by activityViewModels()
    private var _binding: FragmentAddAddressBinding? = null
    private val binding get() = _binding!!
    private var address: String? = null
    private var coordinates: String? = null
    private var adminArea: String? = null
    private var subAdminArea: String? = null
    private var streetName: String? = null
    private var goverment: String? = null

    private lateinit var suggestAddAdapter: SuggestAddAddressAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAddressBinding.inflate(inflater, container, false)
        arguments?.let {
            addressData = it.getParcelable("item")!!
            fromHome = it.getBoolean("fromHome")
        }
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkFieldsForEmptyValues(binding.addressTitle, binding.phoneNumberEditText, binding.buildingNumberEditText,
                    binding.apartmentNumberEditText,binding.floorNoEditText,binding.streetEditText,
                    binding.areaEditText,binding.cityEditText,binding.saveAddressBtn)
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.addressTitle.addTextChangedListener(textWatcher)
        binding.phoneNumberEditText.addTextChangedListener(textWatcher)
        binding.buildingNumberEditText.addTextChangedListener(textWatcher)
        binding.apartmentNumberEditText.addTextChangedListener(textWatcher)
        binding.floorNoEditText.addTextChangedListener(textWatcher)
        binding.streetEditText.addTextChangedListener(textWatcher)
        binding.areaEditText.addTextChangedListener(textWatcher)
        binding.cityEditText.addTextChangedListener(textWatcher)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSuggestAdapter()
        setUpListeners()
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.isLoad.observe(viewLifecycleOwner) {
            //should show loading progressbar or use paging
            Log.d("TAG", it.toString())
            if (it) {
                showLoading()

            } else {
                hideLoading()
            }
        }
        viewModel.error.observe(viewLifecycleOwner) {
            if(it != null){
                Log.d("TAG", it.toString())
                Toast.makeText(requireContext(), getString(R.string.out_of_zone), Toast.LENGTH_LONG)
                    .show()
            }
        }
        viewModel.addAddress.observe(viewLifecycleOwner) {
            if (it != null) {
                    requireActivity().startActivity(Intent(context, MainActivity::class.java))
                    requireActivity().finishAffinity()
            }
        }
    }

    private fun setUpListeners() {
        address = addressData.addressLine
        coordinates = addressData.coordinates
        adminArea = addressData.adminArea
        subAdminArea = addressData.subAdminArea
        streetName = addressData.streetName
        goverment = addressData.goverment
        val fullStreetName = "$streetName, $subAdminArea, $adminArea"
        binding.fullAddressTv.text = address
        binding.streetEditText.text =
            Editable.Factory.getInstance().newEditable(fullStreetName)

        binding.cityEditText.text =
            Editable.Factory.getInstance().newEditable(goverment)

        binding.saveAddressBtn.setOnClickListener {
            if (
                binding.addressTitle.text.toString().trim().isEmpty()
                || binding.apartmentNumberEditText.text.toString().trim().isEmpty()
                || binding.streetEditText.text.toString().trim().isEmpty()
                || binding.areaEditText.text.toString().trim().isEmpty()
                || binding.phoneNumberEditText.text.toString().trim().isEmpty()
                || binding.buildingNumberEditText.text.toString().trim().isEmpty()
                || binding.floorNoEditText.text.toString().trim().isEmpty()
                || binding.cityEditText.text.toString().trim().isEmpty()
                || binding.phoneNumberEditText.text.toString().length != 11
            ) {
                Toast.makeText(
                    context, getString(R.string.missing_info),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (binding.phoneEdt.text.toString().isEmpty()) {
                    binding.phoneEdt.setText("")
                }
                if (binding.nameEdt.text.toString().isEmpty()) {
                    binding.nameEdt.setText("")
                }
                var default = 0
                if (binding.checkBoxAddress.isChecked) {
                    default = 1
                }
                val postAddressModel = AddAddressPostModel(
                    name = binding.addressTitle.text.toString(),
                    address = binding.streetEditText.text.toString(),
                    city = binding.cityEditText.text.toString(),
                    phone = binding.phoneNumberEditText.text.toString(),
                    default = default,
                    area = binding.areaEditText.text.toString(),
                    lat = coordinates!!.split(",")[0].toDouble(),
                    lng = coordinates!!.split(",")[1].toDouble(),
                    apartment_number = binding.apartmentNumberEditText.text.toString().toInt(),
                    floor_number = binding.floorNoEditText.text.toString().toInt(),
                    building_number = binding.buildingNumberEditText.text.toString().toInt(),
                    landline = binding.landlineEditText.text.toString(),
                    receiver_phone = binding.phoneEdt.text.toString(),
                    receiver_name = binding.nameEdt.text.toString()
                )
                viewModel.addAddress(postAddressModel)
            }
        }
        binding.changeBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.backIc.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.cancelAddressBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setUpSuggestAdapter() {
        val arr: MutableList<StringsAddress> = ArrayList()
        arr.add(StringsAddress(getString(R.string.Home)))
        arr.add(StringsAddress(getString(R.string.Office)))
        arr.add(StringsAddress(getString(R.string.Other), true))
        suggestAddAdapter = SuggestAddAddressAdapter(this, arr)
        binding.rvSuggestAddAddress.adapter = suggestAddAdapter
    }

    override fun onClickSuggestLabelAddress(position: Int, txt: String) {
        when (position) {
            2 -> {
                binding.addressTitle.visibility = View.VISIBLE
            }

            1 -> {
                binding.addressTitle.setText(getString(R.string.Office))
                binding.addressTitle.visibility = View.GONE
            }
            0 -> {
                binding.addressTitle.setText(getString(R.string.Home))
                binding.addressTitle.visibility = View.GONE
            }
        }
    }
    private fun checkFieldsForEmptyValues(
        addressLabel: EditText,
        phoneNumber: EditText,
        buildingNumber:EditText,
        apartmentNum:EditText,
        floorNum:EditText,
        streetNum:EditText,
        area:EditText,
        city:EditText,
        submitButton: Button
    ) {
        val addressLabelEdt = addressLabel.text.toString().trim()
        val phoneNumberEdt = phoneNumber.text.toString().trim()
        val buildingNumberEdt = buildingNumber.text.toString().trim()
        val apartmentNumEdt = apartmentNum.text.toString().trim()
        val floorNumEdt = floorNum.text.toString().trim()
        val streetNumEdt = streetNum.text.toString().trim()
        val areaEdt = area.text.toString().trim()
        val cityEdt = city.text.toString().trim()

        if (addressLabelEdt.isNotEmpty() && phoneNumberEdt.isNotEmpty()
            && buildingNumberEdt.isNotEmpty() && apartmentNumEdt.isNotEmpty()
            && floorNumEdt.isNotEmpty() && streetNumEdt.isNotEmpty()
            && areaEdt.isNotEmpty() && cityEdt.isNotEmpty()) {
            submitButton.isEnabled = true
            submitButton.backgroundTintList = ColorStateList
                .valueOf(ContextCompat.getColor(requireContext(), R.color.dark_blue))
        } else {
            submitButton.isEnabled = false
            submitButton.backgroundTintList = ColorStateList
                .valueOf(ContextCompat.getColor(requireContext(), R.color.white_shadow))
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearObeservers()
    }
}

interface FragmentAddAddressNavigator {
    fun onClickSuggestLabelAddress(position: Int, txt: String)
}

data class StringsAddress(var name: String, var checked: Boolean? = false)