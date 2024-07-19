package store.msolapps.flamingo.presentation.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import store.msolapps.domain.models.response.AddressResponseModel
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentMyAddressesBinding
import store.msolapps.flamingo.presentation.home.profile.adapters.MyAddressNavigator
import store.msolapps.flamingo.presentation.home.profile.adapters.MyAddressesAdapter
import store.msolapps.flamingo.util.DeleteConfirmationDialog
import store.msolapps.flamingo.util.DeleteConfirmationDialogActions


class MyAddressesFragment : BaseFragment(), MyAddressNavigator {

    private var _binding: FragmentMyAddressesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MyAddressesAdapter
    private val viewModel: MyAddressesFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAddressesBinding.inflate(inflater, container, false)
        viewModel.getAddressAPI()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setupObservers()
        setupListeners()
    }

    fun setupListeners() {
        binding.backIc.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.addAddressBtn.setOnClickListener {
            val action = MyAddressesFragmentDirections
                .actionMyAddressesFragmentToMapFragment2(true)
            findNavController().navigate(action)
        }
    }

    fun setupObservers() {
        viewModel.getAddresses.observe(viewLifecycleOwner) {
            var isThereDefaultAddress = false
            val response = it.data
            if (it.data.isNotEmpty()) {
                //we need to check if there's default addresses or not
                for (address in response) {
                    if (address.default == 1) {
                        //there's address with default value
                        isThereDefaultAddress = true
                    }
                }
                if (!isThereDefaultAddress) {
                    //choose the first address to be the default
                    response[0].default = 1
                    it.data[0].default = 1
                }
                setUpRecyclerViewAddresses(response)
                binding.addressRecyclerView.visibility = View.VISIBLE
                binding.placeHolder.visibility = View.GONE
            } else {
                binding.addressRecyclerView.visibility = View.GONE
                binding.placeHolder.visibility = View.VISIBLE
            }
        }
        viewModel.deleteAddresses.observe(viewLifecycleOwner) {
            //we need to delay because API need time to respond with the right data
            viewModel.getAddressAPI()
        }
        viewModel.updateDefaultAddresses.observe(viewLifecycleOwner) {
            viewModel.getAddressAPI()
        }
        viewModel.isLoadDelete.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.isLoad.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
    }

    private fun setUpRecyclerViewAddresses(data: MutableList<AddressResponseModel.Data>) {
        adapter = MyAddressesAdapter(this)
        adapter.setData(data)
        binding.addressRecyclerView.adapter = adapter
    }

    override fun onClickToEdit(address: AddressResponseModel.Data) {
        val bundle = Bundle().apply {
            putString("name", address.name)
            putString("phone", address.phone)
            putString("address", address.address)
            putString("building_number", address.building_number)
            putString("floor_number", address.floor_number)
            putString("apartment_number", address.apartment_number)
            putString("area", address.area)
            putString("city", address.city)
            putString("receiver_phone", address.receiver_phone)
            putDouble("lng", address.lng)
            putDouble("lat", address.lat)
            putString("receiver_name", address.receiver_name)
            putInt("default", address.default)
            putString("landline", address.landline)
            putString("id", address.id)
        }
        findNavController().navigate(R.id.action_myAddressesFragment_to_editAddressFragment, bundle)
    }

    override fun onClickToRemove(id: String) {
        //when click remove address in adapter we need to show delete confirmation dialog
        DeleteConfirmationDialog(requireContext(), object : DeleteConfirmationDialogActions {
            //on click button confirm delete address in dialog
            override fun onClickButtonDelete() {
                //finally MyAddressesFragment call the api using SingleSourceOfTruth Design Pattern
                viewModel.deleteAddressAPI(id)
            }
        }).show()
    }

    override fun onClickToSelect(id: String) {
        viewModel.updateDefaultAddress(id.toInt())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}