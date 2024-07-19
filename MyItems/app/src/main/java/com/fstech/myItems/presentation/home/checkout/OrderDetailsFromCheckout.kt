package store.msolapps.flamingo.presentation.home.checkout

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentOrderDetailsBinding
import store.msolapps.flamingo.presentation.home.profile.CancelOrderBottomSheet
import store.msolapps.flamingo.presentation.home.profile.MyOrderDetailsArgs
import store.msolapps.flamingo.presentation.home.profile.OrderDetailsViewModel
import store.msolapps.flamingo.util.LocalTime

class OrderDetailsFromCheckout(
    private var id: String? = null
) : BaseFragment() {
    private var _binding: FragmentOrderDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: OrderDetailsViewModel by activityViewModels()
    private val args: MyOrderDetailsArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        args.let {
            id = it.id
        }
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        viewModel.showOrderDetails(id!!)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setupListeners()
        setupObservers()
    }

    @SuppressLint("SetTextI18n")
    fun setupObservers() {
        viewModel.orderDetails.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.datetv.text = it.data.date
                if (it.data.flag == "slots" || it.data.flag == "Slots") {
                    if (it.data.slot != null) {
                        val apiTextDate =
                            LocalTime.convertDateString(it.data.date, "dd MMM yyyy") + ", " +
                                    getString(
                                        R.string.from_to,
                                        LocalTime.convertTo12HourFormat(it.data.slot!!.from),
                                        LocalTime.convertTo12HourFormat(it.data.slot!!.to)
                                    )
                        val formattedTextDate = apiTextDate.replace(",", "\n")
                        binding.datetv.text = formattedTextDate
                    } else {
                        binding.datetv.text = LocalTime
                            .convertDateString(it.data.date, "dd MMM yyyy, h:mm a")
                    }
                } else {
                    binding.datetv.text =
                        LocalTime.convertDateString(it.data.date, "dd MMM yyyy, h:mm a")
                }
                binding.sameDayVv.text = it.data.flag.replaceFirstChar { char -> char.uppercase() }
                binding.orderNumber.text =
                    getString(R.string.order_id_details, it.data.id.toString())
                binding.itemsId.text =
                    getString(R.string.items_count, it.data.order_products.size.toString())
                binding.priceItems.text =
                    getString(R.string.egps, (it.data.total_amount).toString())

                binding.deliveryFees.text =
                    getString(R.string.egps, "${it.data.cost}")

                binding.totalPrices.text =
                    getString(R.string.egps, it.data.total_net_amount.toString())

                binding.layoutDeliveryAddress.textView44.text = it.data.address.name
                binding.layoutDeliveryAddress.textView45.text = it.data.address.address
                binding.paymentMethods.textView43.text = it.data.payment_method
                if (it.data.payment_method == "cash") {
                    Glide.with(this)
                        .load(R.drawable.usd_circle).into(binding.paymentMethods.imageView8)
                } else {
                    Glide.with(this)
                        .load(R.drawable.credit_card).into(binding.paymentMethods.imageView8)
                }

                if (it.data.status == "pending") {
                    checkWaiting()
                } else if (it.data.status == "preparing") {
                    checkPreparing()
                } else if (it.data.status == "is_ready" || it.data.status == "assigned" || it.data.status == "pos_check") {
                    checkReady()
                } else if (it.data.status == "on_the_way" || it.data.status == "out for delivery" || it.data.status == "assigned") {
                    checkOnTheWay()
                } else if (it.data.status == "delivered" || it.data.status == "Delivered") {
                    checkDelivered()
                } else {
                    binding.marksLayout.visibility = View.INVISIBLE
                }

                binding.textView34.text = it.data.order_message
                val apiText = it.order_container
                val formattedText = apiText.replace(" ", "\n")
                binding.waitingtv.text = formattedText
                val apiTextDate = LocalTime.convertDateString(it.data.date, "dd MMM yyyy, h:mm a")
                val formattedTextDate = apiTextDate?.replace(",", "\n")
                binding.dateContainer.text =
                    formattedTextDate
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

    private fun checkWaiting() {
        Glide.with(this)
            .load(R.drawable.mark_first).into(binding.markFirst)
    }

    private fun checkPreparing() {
        binding.statusSecond.setTextColor(resources.getColor(R.color.black_blue))
        Glide.with(this)
            .load(R.drawable.mark_first).into(binding.markSecond)
    }

    private fun checkReady() {
        binding.statusSecond.setTextColor(resources.getColor(R.color.black_blue))
        binding.statusThird.setTextColor(resources.getColor(R.color.black_blue))
        Glide.with(this)
            .load(R.drawable.mark_first).into(binding.markSecond)
        Glide.with(this)
            .load(R.drawable.mark_first).into(binding.markThird)
    }

    private fun checkOnTheWay() {
        binding.statusSecond.setTextColor(resources.getColor(R.color.black_blue))
        binding.statusThird.setTextColor(resources.getColor(R.color.black_blue))
        binding.statusFourth.setTextColor(resources.getColor(R.color.black_blue))
        Glide.with(this)
            .load(R.drawable.mark_first).into(binding.markSecond)
        Glide.with(this)
            .load(R.drawable.mark_first).into(binding.markThird)
        Glide.with(this)
            .load(R.drawable.mark_first).into(binding.markFourth)
    }

    private fun checkDelivered() {
        binding.statusSecond.setTextColor(resources.getColor(R.color.black_blue))
        binding.statusThird.setTextColor(resources.getColor(R.color.black_blue))
        binding.statusFourth.setTextColor(resources.getColor(R.color.black_blue))
        binding.statusFifth.setTextColor(resources.getColor(R.color.black_blue))
        Glide.with(this)
            .load(R.drawable.mark_first).into(binding.markSecond)
        Glide.with(this)
            .load(R.drawable.mark_first).into(binding.markThird)
        Glide.with(this)
            .load(R.drawable.mark_first).into(binding.markFourth)
        Glide.with(this)
            .load(R.drawable.green_mark).into(binding.markFifth)
    }

    fun setupListeners() {
        binding.backIc.setOnClickListener {
            findNavController().navigate(OrderDetailsFromCheckoutDirections.actionOrderDetailsFromCheckoutToNavigationHome())
        }
        binding.showDetailsbtn.setOnClickListener {
            binding.showDetailsbtn.visibility = View.GONE
            binding.cancelBtn.visibility = View.VISIBLE
            binding.detailsView.visibility = View.VISIBLE
        }
        binding.cancelBtn.setOnClickListener {
            val cancelOrderBottomSheet = CancelOrderBottomSheet()
            cancelOrderBottomSheet.show(parentFragmentManager, cancelOrderBottomSheet.tag)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearObserver()
    }
}