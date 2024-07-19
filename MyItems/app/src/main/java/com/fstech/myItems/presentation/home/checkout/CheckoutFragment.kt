package store.msolapps.flamingo.presentation.home.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.Keep
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.domain.models.request.CheckoutPostModel
import store.msolapps.domain.models.response.GetPaid.GetPaidResponseModel
import store.msolapps.domain.models.response.PaymentMethod
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentCheckoutBinding
import store.msolapps.flamingo.presentation.home.checkout.actions.OrderSummaryActions
import store.msolapps.flamingo.presentation.home.checkout.adapter.OrderSummaryAdapter
import store.msolapps.flamingo.presentation.home.checkout.viewmodels.CheckoutViewModel
import store.msolapps.flamingo.presentation.home.checkout.viewmodels.SlotsViewModel
import store.msolapps.flamingo.presentation.home.home.AddressBottomSheetViewModel
import store.msolapps.flamingo.util.Dialogs

@Keep
@AndroidEntryPoint
class CheckoutFragment : BaseFragment(), OrderSummaryActions {

    private val viewModel: CheckoutViewModel by activityViewModels()
    private val slotsViewModel: SlotsViewModel by activityViewModels()
    private val addressViewModel: AddressBottomSheetViewModel by activityViewModels()
    private var _binding: FragmentCheckoutBinding? = null
    private var orderSummaryAdapter: OrderSummaryAdapter? = null
    lateinit var checkoutPostModel: CheckoutPostModel
    lateinit var paymobPostModel: CheckoutPostModel
    var paymobMethod: String = ""
    var paymobFlag: String = ""
    var paymobDelDate: String? = null
    var paymobSlotId: Int = 0
    var paymobSlotDate: String = ""
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        setupAdapters()
        setupUi()
        setupObservers()
        getData()
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.imageView7.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.radioButtonPaymentMethod1.setOnClickListener {
            binding.radioButtonPaymentMethod2.isChecked = false
            binding.radioButtonPaymentMethod3.isChecked = false
        }
        binding.radioButtonPaymentMethod2.setOnClickListener {
            binding.radioButtonPaymentMethod1.isChecked = false
            binding.radioButtonPaymentMethod3.isChecked = false
        }
        binding.radioButtonPaymentMethod3.setOnClickListener {
            binding.radioButtonPaymentMethod2.isChecked = false
            binding.radioButtonPaymentMethod1.isChecked = false
        }
        binding.radioButtonScheduleDelivery.setOnClickListener {
            binding.radioButtonDeliveryNow.isChecked = false
            setDeliveryFees(
                viewModel.getPaidTypesData.value?.data?.get(1)?.times?.get(
                    slotsViewModel.getSelectedSlot()
                )?.fees
            )
        }
        binding.radioButtonDeliveryNow.setOnClickListener {
            binding.radioButtonScheduleDelivery.isChecked = false
            setDeliveryFees(
                viewModel.getPaidTypesData.value?.data?.get(0)?.times?.get(
                    0
                )?.fees
            )
        }
        binding.radioButtonScheduleDelivery.setOnClickListener {
            binding.radioButtonDeliveryNow.isChecked = false
        }
        binding.bottomLayoutOfCheckout.deliverFees.doOnTextChanged { _, _, _, _ ->
            showTotalPrice()
        }
        binding.bottomLayoutOfCheckout.textView33.doOnTextChanged { _, _, _, _ ->
            showTotalPrice()
        }
        binding.bottomLayoutOfCheckout.buttonOrderNow.setOnClickListener {
            createOrderRoutine()
        }
        binding.layoutDeliveryAddress.imageView11.setOnClickListener {
            openAddress()
        }
    }

    private fun openAddress() {
        findNavController().navigate(CheckoutFragmentDirections.actionCheckoutFragmentToAddressBottomSheet())
    }

    private fun createOrderRoutine() {
        //first check if there delivery address
        if (binding.radioButtonDeliveryNow.visibility == View.GONE && binding.radioButtonScheduleDelivery.visibility == View.GONE) {
            showOutOfZoneDialog()
            return
        }
        //if there's no deliverynow and only slots and slots is not selected
        if (viewModel.getPaidTypesData.value?.data?.get(0).toString()
                .contains("slots") && slotsViewModel.selectedTimeSlotPositionLiveData.value == -1 && binding.radioButtonDeliveryNow.isChecked
        ) {
            Toast.makeText(
                requireContext(), getString(R.string.please_select_time), Toast.LENGTH_LONG
            ).show()
            return
        }

        //second we check what payment method did the user picked up
        else if (binding.radioButtonPaymentMethod1.isChecked) {
            createOrderRoutine2(viewModel.getPaymentMethodData.value?.data?.get(0)?.payment_key.toString())
        } else if (binding.radioButtonPaymentMethod2.isChecked) {
            createOrderRoutine2(viewModel.getPaymentMethodData.value?.data?.get(1)?.payment_key.toString())
        } else if (binding.radioButtonPaymentMethod3.isChecked) {
            createOrderRoutine2(viewModel.getPaymentMethodData.value?.data?.get(2)?.payment_key.toString())
        }
    }

    private fun createOrderRoutine2(paymentMethod: String) {
        Log.e("payment method", slotsViewModel.botFrag_slotDate)
        Log.e("payment method", slotsViewModel.botFrag_slotsId.toString())
        Log.e("payment method", slotsViewModel.slot_date)
        var flag = if (binding.radioButtonScheduleDelivery.isChecked) {
            viewModel.getPaidTypesData.value?.data?.get(1)?.flag.toString()
//            slotsViewModel.getCurrentDate() + viewModel.getPaidTypesData.value?.data?.get(0)?.times?.get(
//                slotsViewModel.getSelectedSlot()
//            )?.from
        } else {
            viewModel.getPaidTypesData.value?.data?.get(0)?.flag.toString()
//            slotsViewModel.getCurrentDate() + viewModel.getPaidTypesData.value?.data?.get(0)?.times?.get(
//                0
//            )?.from
        }
        var deliveryDate: String? = null // cancelled from backend
        //surprise we need to add it again while we are in our first lunch
        if (binding.radioButtonDeliveryNow.isChecked) {
            if (viewModel.getPaidTypesData.value?.data?.get(0).toString().contains("slots")) {
                val from = slotsViewModel.selectedTimeSlotLiveData.value?.from
                val to = slotsViewModel.selectedTimeSlotLiveData.value?.to
                deliveryDate = binding.textView48.text.toString().removePrefix("\n")
            } else {
                deliveryDate = binding.textView48.text.toString().removePrefix("\n")
            }
        } else if (binding.radioButtonScheduleDelivery.isChecked) {
            if (viewModel.getPaidTypesData.value?.data?.get(1).toString().contains("slots")) {
                val from = slotsViewModel.selectedTimeSlotLiveData.value?.from
                val to = slotsViewModel.selectedTimeSlotLiveData.value?.to
                deliveryDate = binding.textViewSlotsTime.text.toString().removePrefix("\n")
            } else {
                deliveryDate = binding.textViewSlotsTime.text.toString().removePrefix("\n")
            }
        } else {
        }
        deliveryDate = null // cancelled from backend

        var slot_id: Int? = if (binding.radioButtonScheduleDelivery.isChecked) {
            if (viewModel.getPaidTypesData.value?.data?.get(1).toString()
                    .contains("slots")
            ) slotsViewModel.botFrag_slotsId
            else null
        } else if (binding.radioButtonDeliveryNow.isChecked) {
            if (viewModel.getPaidTypesData.value?.data?.get(0).toString()
                    .contains("slots")
            ) slotsViewModel.botFrag_slotsId
            else null
        } else null


        var slotDate: String? = if (binding.radioButtonDeliveryNow.isChecked) {
            if (viewModel.getPaidTypesData.value?.data?.get(0).toString()
                    .contains("slots")
            ) slotsViewModel.botFrag_slotDate
            else null
        } else if (binding.radioButtonScheduleDelivery.isChecked) {
            if (viewModel.getPaidTypesData.value?.data?.get(1).toString()
                    .contains("slots")
            ) slotsViewModel.botFrag_slotDate
            else null
        } else {
            null
        }

        checkoutPostModel = CheckoutPostModel(
            payment_method = paymentMethod,
            flag = flag,
            cost = binding.bottomLayoutOfCheckout.deliverFees.text.toString().toDouble(),
//            cost = binding.bottomLayoutOfCheckout.tvTotalPrice.text.toString().toDouble(),
            pickup = null,
            address_id = viewModel.getDefaultAddressData.value?.data?.id ?: 0,
            store_id = viewModel.getStoreId(),
            lang = viewModel.getLang(),
            note = binding.editText.text.toString().trim(),
            delivery_date = deliveryDate,
            Transaction_id = null, // slot_id.toString(), //trying to fix anything
            slot_id = slot_id,
            slot_date = slotDate
        )
        paymobMethod = paymentMethod
        paymobFlag = flag
        paymobDelDate = deliveryDate
        paymobSlotId = slot_id ?: 0
        paymobSlotDate = slotDate ?: ""

        when (paymentMethod) {
            "Credit or debit card" -> {
                openPaymob()
            }

            "Card on delivery" -> {
                viewModel.createOrder(checkoutPostModel)
            }

            "Cash on delivery" -> {
                viewModel.createOrder(checkoutPostModel)
            }

            else -> {
                viewModel.createOrder(checkoutPostModel)
            }
        }
    }

    val publicKey = "egy_pk_test_eyr0XZHwPyyuDZSbCR1EWd5e4IckQNMn"
    val secretKey = "egy_sk_test_e95435f7ded321831405b81ad1ac650f04a72792c3e81cf989855418b5d8c3f6"
    private fun openPaymob() {
        Log.e("paymob", "openPaymob")
        val itemsPrice = binding.bottomLayoutOfCheckout.deliverFees.text.toString().toDouble()
        val deliveryServicePrice =
            binding.bottomLayoutOfCheckout.textView33.text.toString().toDouble()
        val amount: Double = (itemsPrice + deliveryServicePrice)
        viewModel.getPaymobIframe(
            addressId = viewModel.getDefaultAddressData.value?.data?.id ?: 0, total = amount
        )
    }

    private fun showTotalPrice() {
        val itemsPrice = binding.bottomLayoutOfCheckout.deliverFees.text.toString().toDouble()
        val deliveryServicePrice =
            binding.bottomLayoutOfCheckout.textView33.text.toString().toDouble()
        binding.bottomLayoutOfCheckout.textView35.text =
            (itemsPrice + deliveryServicePrice).toString()
        binding.bottomLayoutOfCheckout.tvTotalPrice.text =
            (itemsPrice + deliveryServicePrice).toString()
    }

    private fun setDeliveryFees(fees: String?) {
        binding.bottomLayoutOfCheckout.deliverFees.text = fees.toString()
    }

    private fun setupAdapters() {
        orderSummaryAdapter = OrderSummaryAdapter(this)
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        binding.orderSummaryLinear.layoutManager = LinearLayoutManager(context)
        binding.orderSummaryLinear.adapter = orderSummaryAdapter
    }

    private fun setupUi() {}

    private fun setupObservers() {
        observeLoading()
        observeError()
        observeData()
        observeBottomSlotChanges()
    }

    private fun observeBottomSlotChanges() {
        slotsViewModel.selectedTimeSlotLiveData.observe(viewLifecycleOwner) {
            //we need to check if there's item is chosen or not from BottomFragment
            if (it == null || slotsViewModel.selectedTimeSlotPositionLiveData.value == null) {
                binding.radioButtonScheduleDelivery.isChecked = false
                binding.radioButtonDeliveryNow.isChecked = true
                binding.textViewSlotsTime.visibility = View.INVISIBLE
                return@observe
            }
            //we need to set data and check if slots in first or second item
            if (viewModel.getPaidTypesData.value?.data?.get(0).toString().contains("slots")) {
                binding.textView48.visibility = View.VISIBLE
                val dayString = slotsViewModel.selectedDaySlotLiveData.value?.dayString
                val day = slotsViewModel.selectedDaySlotLiveData.value?.day
                val month = slotsViewModel.selectedDaySlotLiveData.value?.month
                val dateOfSelectedSlot = "$dayString, $day $month \n(${it.from} - ${it.to})"
                binding.textView48.text = dateOfSelectedSlot
                binding.textView49.text = getString(R.string.egps, it.fees)
                binding.bottomLayoutOfCheckout.deliverFees.text = it.fees
            } else {
                binding.textViewSlotsTime.visibility = View.VISIBLE
                val dayString = slotsViewModel.selectedDaySlotLiveData.value?.dayString
                val day = slotsViewModel.selectedDaySlotLiveData.value?.day
                val month = slotsViewModel.selectedDaySlotLiveData.value?.month
                val dateOfSelectedSlot = "$dayString, $day $month \n(${it.from} - ${it.to})"
                binding.textViewSlotsTime.text = dateOfSelectedSlot
                binding.textView52.text = getString(R.string.egps, it.fees)
                binding.bottomLayoutOfCheckout.deliverFees.text = it.fees
            }


        }
        slotsViewModel.selectedTimeSlotPositionLiveData.observe(viewLifecycleOwner) {
            //on cancelBottomSheet
            if (it == null) {
                binding.radioButtonScheduleDelivery.isChecked = false
                binding.radioButtonDeliveryNow.isChecked = true
                binding.textViewSlotsTime.visibility = View.INVISIBLE
                return@observe
            }
        }
    }

    private fun observeError() {
        viewModel.getDefaultAddressError.observe(viewLifecycleOwner) {}
        viewModel.cartError.observe(viewLifecycleOwner) {}
        viewModel.getPaymentMethodError.observe(viewLifecycleOwner) {}
        viewModel.getPaidTypesError.observe(viewLifecycleOwner) {}
        viewModel.createOrderError.observe(viewLifecycleOwner) {}
        slotsViewModel.getSlotsError.observe(viewLifecycleOwner) {}
    }

    private fun observeLoading() {
        viewModel.getDefaultAddressLoad.observe(viewLifecycleOwner) {
            if (it) showLoading() else hideLoading()
        }
        viewModel.cartLoad.observe(viewLifecycleOwner) {

        }
        viewModel.getPaymentMethodLoad.observe(viewLifecycleOwner) {
            if (it) showLoading() else hideLoading()
        }
        viewModel.getPaidTypesLoading.observe(viewLifecycleOwner) {
            if (!it) hideLoading()
        }
        viewModel.createOrderLoad.observe(viewLifecycleOwner) {
            if (it) showLoading() else hideLoading()
        }
        viewModel.getPaymobClientSecretLoad.observe(viewLifecycleOwner) {
            if (it) showLoading() else hideLoading()
        }
        viewModel.getIframeLoad.observe(viewLifecycleOwner) {
            if (it) showLoading() else hideLoading()
        }
    }

    private fun observeData() {
        viewModel.getDefaultAddressData.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
            binding.layoutDeliveryAddress.textView44.text = it.data?.name.toString()
            binding.layoutDeliveryAddress.textView45.text = it.data?.address.toString()
            viewModel.getPaidTypes()
        }
        viewModel.cartData.observe(viewLifecycleOwner) {
            orderSummaryAdapter?.setData(it.data)
            binding.bottomLayoutOfCheckout.textView33.text = it.total_price.toString()
            binding.bottomLayoutOfCheckout.textView30.text =
                getString(R.string.items_count, it.data.size.toString())
        }
        viewModel.getPaymentMethodData.observe(viewLifecycleOwner) {
            showFirstPaymentMethod(false)
            showSecondPaymentMethod(false)
            showThirdPaymentMethod(false)
            binding.secondLine.visibility = View.GONE
            binding.thirdLine.visibility = View.GONE
            if (it.data.isEmpty()) {
                //no payment method

                return@observe
            }

            setPaymentMethodData(it.data)

            if (binding.radioButtonPaymentMethod1.visibility == View.VISIBLE) {
                binding.radioButtonPaymentMethod1.isChecked = true
            }
        }

        viewModel.getPaidTypesData.observe(viewLifecycleOwner) {
            showDeliveryNow(false)
            showScheduleDelivery(false)
            binding.firstLine.visibility = View.GONE
            if (it.error == true) {
                showOutOfZoneDialog()
                return@observe
            }
            //the middle blue line show it or hide it passed on the data size
            if (it.data.size >= 2) binding.firstLine.visibility = View.VISIBLE
            else binding.firstLine.visibility = View.GONE

            //workaround to make slots choice number two always
            if (it.data.get(0).toString().contains("slots")) {
                val reversedData = it.data.reversed()
                it.data = reversedData
                viewModel.getPaidTypesData.value?.data = reversedData
            }

            setDeliveryTypesData(it.data)

            if (binding.radioButtonDeliveryNow.visibility == View.VISIBLE) {
                binding.radioButtonDeliveryNow.isChecked = true
            } else if (binding.radioButtonScheduleDelivery.visibility == View.VISIBLE) {
                binding.radioButtonScheduleDelivery.isChecked = true
            }
        }
        viewModel.createOrderData.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            } else {
//                if (findNavController().currentDestination?.id == R.id.checkoutFragment)
                if (findNavController().currentDestination?.id != R.id.checkoutSuccessFragment) findNavController().navigate(
                    CheckoutFragmentDirections.actionCheckoutFragmentToCheckoutSuccessFragment(
                        it.order_id
                    )
                )
                viewModel.clearObserver()
            }
        }
        slotsViewModel.getSlotsData.observe(viewLifecycleOwner) {

        }
        viewModel.getPaymobClientSecretData.observe(viewLifecycleOwner) {
//            PaymobSdk.Builder(
//                context = requireActivity(),
//                clientSecret = it.data?.clientSecret.toString(),
//                publicKey = publicKey, // Place Public Key here
//                paymobSdkListener = this@CheckoutFragment,
//            savedCard = "{_MASKED_PAN_SAVED_CARD_TOKEN\CARD_TYPE}"//Optional Field if you have a saved card
//            )
//                .setButtonBackgroundColor(requireContext().getColor(R.color.black_blue))
//                .setButtonBackgroundColor(requireContext().getColor(R.color.white))
//                .setButtonBackgroundColor(requireContext().getColor(R.color.white_blue))
//                .setAppName(getString(R.string.flamingo))
//                .setAppLogo(R.drawable.ic_launcher_foreground)
//                .build().start()
        }
        addressViewModel.updateAddresses.observe(viewLifecycleOwner) {
            viewModel.getDefaultAddress()
        }
        viewModel.getIframeData.observe(viewLifecycleOwner) {
            if (it != null) {
                paymobPostModel = CheckoutPostModel(
                    payment_method = paymobMethod,
                    flag = paymobFlag,
                    cost = binding.bottomLayoutOfCheckout.deliverFees.text.toString().toDouble(),
                    pickup = null,
                    address_id = viewModel.getDefaultAddressData.value?.data?.id ?: 0,
                    store_id = viewModel.getStoreId(),
                    lang = viewModel.getLang(),
                    note = binding.editText.text.toString().trim(),
                    delivery_date = paymobDelDate,
                    Transaction_id = null,
                    slot_id = paymobSlotId,
                    slot_date = paymobSlotDate
                )
                val bundle = Bundle().apply {
                    putSerializable("item", paymobPostModel)
                    putString("link", it.link)
                }
                findNavController().navigate(R.id.action_checkoutFragment_to_IFrameWebView, bundle)
            }
        }
    }

    private fun showOutOfZoneDialog() {
        Dialogs(context = requireContext()).showOkDialog(
            getString(R.string.out_of_zone), getString(R.string.please_add_address)
        )
    }

    private fun setPaymentMethodData(data: List<PaymentMethod>) {
        if (data.size > 0) {
            showFirstPaymentMethod(true)
            if (data.get(0).payment_key.contains("debit")) {
                binding.imageView18.setImageResource(R.drawable.credit_card)
            } else if (data.get(0).payment_key.contains("card")) {
                binding.imageView18.setImageResource(R.drawable.pos_on_delivery)
            } else binding.imageView18.setImageResource(R.drawable.usd_circle)
            binding.textView55.text = data.get(0).name
        }
        if (data.size > 1) {
            showSecondPaymentMethod(true)
            if (data.get(1).payment_key.contains("debit")) {
                binding.imageView19.setImageResource(R.drawable.credit_card)
            } else if (data.get(1).payment_key.contains("Card")) {
                binding.imageView19.setImageResource(R.drawable.pos_on_delivery)
            } else binding.imageView19.setImageResource(R.drawable.usd_circle)
            binding.textView56.text = data.get(1).name
        }
        if (data.size > 2) {
            showThirdPaymentMethod(true)
            if (data.get(2).payment_key.contains("debit")) {
                binding.imageView21.setImageResource(R.drawable.credit_card)
            } else if (data.get(2).payment_key.contains("Card")) {
                binding.imageView21.setImageResource(R.drawable.pos_on_delivery)
            } else binding.imageView21.setImageResource(R.drawable.usd_circle)
            binding.textView63.text = data.get(2).name
        }
    }

    private fun setDeliveryTypesData(data: List<GetPaidResponseModel.Data>) {
        if (data.size > 0) {
            setDeliveryFees(data.get(0).times.get(0).fees)
            showDeliveryNow(true)
            if (data.get(0).toString().contains("Same Day")) {
                binding.textView47.text = getString(R.string.deliver_now)
                binding.imageView17.setImageResource(R.drawable.shipping_fast)
            } else if (data.get(0).toString().contains("slots")) {
                binding.textView47.text = getString(R.string.schedule_delivery)
                binding.imageView17.setImageResource(R.drawable.shipping_timed)
                setListenersFirstDelivery()
                slotsViewModel.flag_id = data.get(0).id
            }
            if (viewModel.getLang() == "ar") {
                binding.textView48.text =
                    viewModel.getPaidTypesData.value?.data?.get(0)?.name_ar.toString()
                binding.textView49.text = "${getString(R.string.egp)} ${
                    viewModel.getPaidTypesData.value?.data?.get(0)?.times?.get(0)?.fees.toString()
                }"
            } else {
                binding.textView48.text =
                    viewModel.getPaidTypesData.value?.data?.get(0)?.name_en.toString()
                binding.textView49.text = "${getString(R.string.egp)} ${
                    viewModel.getPaidTypesData.value?.data?.get(0)?.times?.get(0)?.fees.toString()
                }"
            }
        }
        if (data.size > 1) {
            showScheduleDelivery(true)
            if (data.get(1).toString().contains("Same Day")) {
                binding.textView50.text = getString(R.string.deliver_now)
                binding.imageView16.setImageResource(R.drawable.shipping_fast)
            } else if (data.get(1).toString().contains("slots")) {
                binding.textView50.text = getString(R.string.schedule_delivery)
                binding.imageView16.setImageResource(R.drawable.shipping_timed)
                setListenersSecondDelivery()
                slotsViewModel.flag_id = data.get(0).id
            }/*      if (viewModel.getLang() == "ar") {
                      val from =
                          viewModel.getPaidTypesData.value?.data?.get(1)?.times?.get(slotsViewModel.botFrag_slotsId)?.from.toString()
                      val to =
                          viewModel.getPaidTypesData.value?.data?.get(1)?.times?.get(slotsViewModel.botFrag_slotsId)?.to.toString()
                      binding.textViewSlotsTime.text = "${from} ${to}"
                      binding.textView52.text = "${getString(R.string.egp)} ${
                          viewModel.getPaidTypesData.value?.data?.get(1)?.times?.get(0)?.fees.toString()
                      }"
                  } else {
                      val from =
                          viewModel.getPaidTypesData.value?.data?.get(1)?.times?.get(slotsViewModel.botFrag_slotsId)?.from.toString()
                      val to =
                          viewModel.getPaidTypesData.value?.data?.get(1)?.times?.get(slotsViewModel.botFrag_slotsId)?.to.toString()
                      binding.textViewSlotsTime.text = "${from} ${to}"
                      binding.textView52.text = "${getString(R.string.egp)} ${
                          viewModel.getPaidTypesData.value?.data?.get(1)?.times?.get(0)?.fees.toString()
                      }"
                  }*/
        }
    }

    private fun setListenersFirstDelivery() {
        val openBottomView = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                binding.radioButtonDeliveryNow.isChecked = true
                binding.radioButtonScheduleDelivery.isChecked = false
                slotsViewModel.flag_id = viewModel.getPaidTypesData.value!!.data.get(0).id
                openSlotsBottomFragment()
            }
        }
        binding.imageView17.setOnClickListener(openBottomView)
        binding.textView47.setOnClickListener(openBottomView)
        binding.radioButtonDeliveryNow.setOnClickListener(openBottomView)
        binding.textView48.setOnClickListener(openBottomView)
        binding.textView49.setOnClickListener(openBottomView)
    }

    private fun setListenersSecondDelivery() {
        val openBottomView = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                binding.radioButtonDeliveryNow.isChecked = false
                binding.radioButtonScheduleDelivery.isChecked = false
                slotsViewModel.flag_id = viewModel.getPaidTypesData.value!!.data.get(1).id
                openSlotsBottomFragment()
            }
        }
        binding.imageView16.setOnClickListener(openBottomView)
        binding.textView50.setOnClickListener(openBottomView)
        binding.textViewSlotsTime.setOnClickListener(openBottomView)
        binding.textView52.setOnClickListener(openBottomView)
        binding.radioButtonScheduleDelivery.setOnClickListener(openBottomView)
    }

    private fun openSlotsBottomFragment() {
        findNavController().navigate(CheckoutFragmentDirections.actionCheckoutFragmentToFragmentSlotsBottomSheet())
    }

    private fun showSecondPaymentMethod(b: Boolean) {
        if (b) {
            binding.imageView19.visibility = View.VISIBLE
            binding.textView56.visibility = View.VISIBLE
            binding.radioButtonPaymentMethod2.visibility = View.VISIBLE
            binding.secondLine.visibility = View.VISIBLE
        } else {
            binding.imageView19.visibility = View.GONE
            binding.textView56.visibility = View.GONE
            binding.radioButtonPaymentMethod2.visibility = View.GONE
            binding.secondLine.visibility = View.GONE
        }
    }

    private fun showFirstPaymentMethod(b: Boolean) {
        if (b) {
            binding.imageView18.visibility = View.VISIBLE
            binding.textView55.visibility = View.VISIBLE
            binding.radioButtonPaymentMethod1.visibility = View.VISIBLE
        } else {
            binding.imageView18.visibility = View.GONE
            binding.textView55.visibility = View.GONE
            binding.radioButtonPaymentMethod1.visibility = View.GONE
        }
    }

    private fun showThirdPaymentMethod(b: Boolean) {
        if (b) {
            binding.imageView21.visibility = View.VISIBLE
            binding.textView63.visibility = View.VISIBLE
            binding.radioButtonPaymentMethod3.visibility = View.VISIBLE
            binding.thirdLine.visibility = View.VISIBLE
        } else {
            binding.imageView21.visibility = View.GONE
            binding.textView63.visibility = View.GONE
            binding.radioButtonPaymentMethod3.visibility = View.GONE
            binding.thirdLine.visibility = View.GONE
        }
    }

    private fun showDeliveryNow(b: Boolean) {
        if (b) {
            binding.imageView17.visibility = View.VISIBLE
            binding.textView47.visibility = View.VISIBLE
            binding.radioButtonDeliveryNow.visibility = View.VISIBLE
            binding.textView48.visibility = View.VISIBLE
            binding.textView49.visibility = View.VISIBLE
        } else {
            binding.imageView17.visibility = View.GONE
            binding.textView47.visibility = View.GONE
            binding.radioButtonDeliveryNow.visibility = View.GONE
            binding.textView48.visibility = View.GONE
            binding.textView49.visibility = View.GONE
        }
    }

    private fun showScheduleDelivery(b: Boolean) {
        if (b) {
            binding.imageView16.visibility = View.VISIBLE
            binding.textView50.visibility = View.VISIBLE
            binding.textViewSlotsTime.visibility = View.VISIBLE
            binding.textView52.visibility = View.VISIBLE
            binding.radioButtonScheduleDelivery.visibility = View.VISIBLE
        } else {
            binding.imageView16.visibility = View.GONE
            binding.textView50.visibility = View.GONE
            binding.textViewSlotsTime.visibility = View.GONE
            binding.textView52.visibility = View.GONE
            binding.radioButtonScheduleDelivery.visibility = View.GONE
        }
    }

    private fun getData() {
        showLoading()
        viewModel.getDefaultAddress()
        viewModel.getCartData()
        viewModel.getPaymentMethod()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        Log.e("onResume ", binding.radioButtonScheduleDelivery.isChecked.toString())
        //I need to show slots an the fees of slot if it being chosen
        if (binding.radioButtonScheduleDelivery.isChecked) {

        } else {

        }
    }

    /* override fun onFailure() {
         Toast.makeText(requireContext(), getString(R.string.payment_failed), Toast.LENGTH_LONG)
             .show()
     }

     override fun onPending() {
         Toast.makeText(requireContext(), getString(R.string.payment_pending), Toast.LENGTH_LONG)
             .show()
     }

     override fun onSuccess() {
         viewModel.createOrder(checkoutPostModel)
     }*/

    override fun onStop() {
        super.onStop()
        viewModel.clearObserver()
        slotsViewModel.clearObserver()
    }
}
