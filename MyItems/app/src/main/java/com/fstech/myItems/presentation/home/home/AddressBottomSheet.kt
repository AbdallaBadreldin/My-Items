package store.msolapps.flamingo.presentation.home.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import store.msolapps.domain.models.response.AddressResponseModel
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentAddressesBottomSheetBinding
import store.msolapps.flamingo.presentation.home.home.adapters.AddressBottomSheetNavigator
import store.msolapps.flamingo.presentation.home.home.adapters.AddressesBottomSheetAdapter
import store.msolapps.flamingo.presentation.home.profile.MyAddressesFragmentDirections


class AddressBottomSheet : BottomSheetDialogFragment(), AddressBottomSheetNavigator {

    private lateinit var binding: FragmentAddressesBottomSheetBinding
    private val viewModel: AddressBottomSheetViewModel by activityViewModels()
    private lateinit var adapter: AddressesBottomSheetAdapter
    private var addressObject: AddressResponseModel.Data? = null
    private var currentId: Int? = null

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.fragment_addresses_bottom_sheet,
                container,
                false
            )

        viewModel.getAddressAPI()
        setupObservers()
        binding.backIc.setOnClickListener { dismiss() }
        binding.applyBottomSheetBtn.setOnClickListener {
            if (addressObject != null)
                if (currentId != null && currentId == addressObject!!.id.toInt())
                    dismiss()
                else {
                    viewModel.updateAddress(addressObject!!.id.toInt())
                    dismiss()
                }
            else
                dismiss()
        }
        binding.addAddressBottomSheetBtn.setOnClickListener {
            val action = HomeFragmentDirections
                .actionNavigationHomeToMapFragment2(true)
            findNavController().navigate(action)
            dismiss()
        }
        return binding.root
    }

    fun setupObservers() {
        viewModel.getAddresses.observe(viewLifecycleOwner) {
            if(it != null){
                setUpRecyclerViewAddresses(it.data)
            }
        }
    }

    private fun setUpRecyclerViewAddresses(data: MutableList<AddressResponseModel.Data>) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.addressesRecyclerViewBottomSheet.layoutManager = layoutManager
        for (i in 0 until data.size) {
            if (data[i].default == 1)
                currentId = data[i].id.toInt()
        }
        adapter = AddressesBottomSheetAdapter(this)
        adapter.setData(data)
        binding.addressesRecyclerViewBottomSheet.adapter = adapter
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearObservers()
    }

    override fun onSelectAddress(address: AddressResponseModel.Data) {
        addressObject = address
    }
}