package store.msolapps.flamingo.presentation.home.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import store.msolapps.domain.models.request.AddAddressPostModel
import store.msolapps.domain.models.response.AddressResponseModel
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentEditAddressBinding
import store.msolapps.flamingo.presentation.auth.AddAddressViewModel

class EditAddressFragment() : BaseFragment() {
    private var _binding: FragmentEditAddressBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddAddressViewModel by activityViewModels()

    private var default = 0
    private var address: String? = null
    private var adminArea: String? = null
    private var subAdminArea: String? = null
    private var lat: Double? = null
    private var lng: Double? = null
    private var phone: String? = null
    private var name: String? = null
    private var building_number: String? = null
    private var floor_number: String? = null
    private var apartment_number: String? = null
    private var receiver_phone: String? = null
    private var receiver_name: String? = null
    private var landline: String? = null
    private var id: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditAddressBinding.inflate(inflater, container, false)
        arguments?.let {
            address = it.getString("address")
            adminArea = it.getString("area")
            subAdminArea = it.getString("city")
            lat = it.getDouble("lat")
            lng = it.getDouble("lng")
            name = it.getString("name")
            phone = it.getString("phone")
            building_number = it.getString("building_number")
            floor_number = it.getString("floor_number")
            apartment_number = it.getString("apartment_number")
            receiver_phone = it.getString("receiver_phone")
            receiver_name = it.getString("receiver_name")
            landline = it.getString("landline")
            id = it.getString("id")
            default = it.getInt("default")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setData()
        setUpListeners()
        setupObservers()

    }

    fun setupObservers() {
        viewModel.editAddress.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { _ ->
                Toast.makeText(requireContext(), "Updated Successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
        viewModel.errorEdit.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(requireContext(), getString(R.string.out_of_zone), Toast.LENGTH_LONG)
                    .show()
            }
        }
        viewModel.isLoad.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()

            } else {
                hideLoading()
            }
        }
        viewModel.isLoadEdit.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()

            } else {
                hideLoading()
            }
        }
    }

    private fun setUpListeners() {

        binding.saveAddressBtn.setOnClickListener {
            if (
                binding.addressTitle.text.toString().isEmpty()
                || binding.apartmentNumberEditText.text.toString().isEmpty()
                || binding.streetEditText.text.toString().isEmpty()
                || binding.areaEditText.text.toString().isEmpty()
                || binding.phoneNumberEditText.text.toString().isEmpty()
                || binding.buildingNumberEditText.text.toString().isEmpty()
                || binding.floorNoEditText.text.toString().isEmpty()
                || binding.cityEditText.text.toString().isEmpty()
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
                    lat = lat!!,
                    lng = lng!!,
                    apartment_number = binding.apartmentNumberEditText.text.toString().toInt(),
                    floor_number = binding.floorNoEditText.text.toString().toInt(),
                    building_number = binding.buildingNumberEditText.text.toString().toInt(),
                    landline = binding.landlineEditText.text.toString(),
                    receiver_phone = binding.phoneEdt.text.toString(),
                    receiver_name = binding.nameEdt.text.toString()
                )
                viewModel.editAddress(id!!, postAddressModel)
            }
        }

        binding.backIc.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.cancelAddressBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setData() {

        if (name!!.isNotEmpty()) {
            binding.addressTitle.setText(name)
        }

        if (phone!!.isNotEmpty()) {
            binding.phoneNumberEditText.setText(phone)
        }

        if (address!!.isNotEmpty()) {
            binding.streetEditText.setText(address)
        }


        if (building_number!!.isNotEmpty()) {
            binding.buildingNumberEditText.setText(building_number)
        }

        if (floor_number!!.isNotEmpty()) {
            binding.floorNoEditText.setText(floor_number)
        }

        if (landline != null) {
            binding.landlineEditText.setText(landline)
        }

        if (apartment_number!!.isNotEmpty()) {
            binding.apartmentNumberEditText.setText(apartment_number)
        }

        if (adminArea!!.isNotEmpty()) {
            binding.areaEditText.setText(adminArea)
        }

        if (subAdminArea!!.isNotEmpty()) {
            binding.cityEditText.setText(subAdminArea)
        }

        if (receiver_name != null) {
            if (receiver_name!!.isNotEmpty()) {
                binding.nameEdt.setText(receiver_name)
            }
        }

        if (receiver_phone != null) {
            if (receiver_phone!!.isNotEmpty()) {
                binding.phoneEdt.setText(receiver_phone)
            }
        }
        if (default == 1) {
            binding.checkBoxAddress.isChecked = true
        } else {
            binding.checkBoxAddress.isChecked = false
        }
    }
}