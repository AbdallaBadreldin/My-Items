package store.msolapps.flamingo.presentation.home.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import store.msolapps.domain.models.response.OrderResponseModel1
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentOrderHistoryBinding
import store.msolapps.flamingo.presentation.home.profile.adapters.MyNewOrdersAdapter
import store.msolapps.flamingo.presentation.home.profile.adapters.MyNewOrdersNavigator

class MyOrderHistory : BaseFragment(), MyNewOrdersNavigator {
    private var _binding: FragmentOrderHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MyOrderHistoryViewModel by activityViewModels()
    private lateinit var adapter: MyNewOrdersAdapter
    private var cancelledOrders: MutableList<OrderResponseModel1.DataOrderResponseModel.Data1OrderResponseModel> =
        ArrayList()
    private var onTheWayOrders: MutableList<OrderResponseModel1.DataOrderResponseModel.Data1OrderResponseModel> =
        ArrayList()
    private var deliveredOrders: MutableList<OrderResponseModel1.DataOrderResponseModel.Data1OrderResponseModel> =
        ArrayList()
    private var waitingOrders: MutableList<OrderResponseModel1.DataOrderResponseModel.Data1OrderResponseModel> =
        ArrayList()
    private var preparingOrders: MutableSet<OrderResponseModel1.DataOrderResponseModel.Data1OrderResponseModel> =
        mutableSetOf()
    private var currentPage = 0

    private var maxPages = 1
    val paginate = 10
    var defaultVal = 0
    var whiteColor: Int = 0
    var blueColor: Int = 0
    var listener = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
        whiteColor = ContextCompat.getColor(requireContext(), R.color.white)
        blueColor = ContextCompat.getColor(requireContext(), R.color.white_blue)
        listener = 0
        setUpRecyclerViewForMyOrders(viewModel.currentSelection)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setupListeners()
        setupObservers()
    }

    fun setupObservers() {
        viewModel.myOrdersOnTheWay.observe(viewLifecycleOwner) {
            if (it != null) {
                maxPages = it.data.last_page
                currentPage = it.data.current_page
                onTheWayOrders.addAll(it.data.orders)
                adapter.differ.submitList(onTheWayOrders.toList())
            }
        }
        viewModel.myOrdersCancelled.observe(viewLifecycleOwner) {
            if (it != null) {
                maxPages = it.data.last_page
                currentPage = it.data.current_page
                cancelledOrders.addAll(it.data.orders)
                adapter.differ.submitList(cancelledOrders.toList())
            }
        }
        viewModel.myOrdersDelivered.observe(viewLifecycleOwner) {
            if (it != null) {
                maxPages = it.data.last_page
                currentPage = it.data.current_page
                deliveredOrders.addAll(it.data.orders)
                adapter.differ.submitList(deliveredOrders.toList())
            }
        }
        viewModel.myOrdersWaiting.observe(viewLifecycleOwner) {
            if (it != null) {
                maxPages = it.data.last_page
                currentPage = it.data.current_page
                waitingOrders.addAll(it.data.orders)
                adapter.differ.submitList(waitingOrders.toList())
            }
        }
        viewModel.myOrdersPreparing.observe(viewLifecycleOwner) {
            if (it != null) {
                maxPages = it.data.last_page
                currentPage = it.data.current_page
                preparingOrders.addAll(it.data.orders)
                adapter.differ.submitList(preparingOrders.toList())
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

    fun setupListeners() {

        binding.onTheWay.setOnClickListener {
            selectTap1()
        }
        binding.cancelled.setOnClickListener {
            selectTap2()
        }
        binding.delivered.setOnClickListener {
            selectTap3()
        }
        binding.Waiting.setOnClickListener {
            selectTap4()
        }
        binding.preparing.setOnClickListener {
            selectTap5()
        }

        binding.ordersRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val position =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                if (position + 1 == adapter.itemCount
                    && maxPages > currentPage
                    && !recyclerView.canScrollVertically(
                        RecyclerView.FOCUS_DOWN
                    )
                ) {
                    if (!viewModel.isLoad.value!!)
                        loadNextPageData()
                    Log.v("TAG", "END OF RECYCLERVIEW IS REACHED11")
                } else {
                    // END OF RECYCLERVIEW IS NOT REACHED
                    Log.v("TAG", "END OF RECYCLERVIEW IS NOT REACHED $maxPages and $currentPage")

                }
            }

        })
        binding.backIc.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun selectTap2() {
        defaultVal = 2
        viewModel.currentSelection = 2
        resetPage()
        viewModel.getMyOrdersCancelled(currentPage, paginate)
        binding.cancelled.setBackgroundResource(R.drawable.rounded_colored_btn)
        binding.cancelled.setTextColor(whiteColor)

        binding.delivered.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.delivered.setTextColor(blueColor)

        binding.onTheWay.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.onTheWay.setTextColor(blueColor)

        binding.preparing.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.preparing.setTextColor(blueColor)

        binding.Waiting.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.Waiting.setTextColor(blueColor)
    }

    private fun selectTap3() {
        defaultVal = 3
        viewModel.currentSelection = 3
        resetPage()

        viewModel.getMyOrdersDelivered(currentPage, paginate)
        binding.delivered.setBackgroundResource(R.drawable.rounded_colored_btn)
        binding.delivered.setTextColor(whiteColor)

        binding.cancelled.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.cancelled.setTextColor(blueColor)

        binding.onTheWay.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.onTheWay.setTextColor(blueColor)

        binding.preparing.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.preparing.setTextColor(blueColor)

        binding.Waiting.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.Waiting.setTextColor(blueColor)
    }

    private fun selectTap4() {
        defaultVal = 4
        viewModel.currentSelection = 4
        resetPage()

        viewModel.getMyOrdersWaiting(currentPage, paginate)
        binding.Waiting.setBackgroundResource(R.drawable.rounded_colored_btn)
        binding.Waiting.setTextColor(whiteColor)

        binding.cancelled.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.cancelled.setTextColor(blueColor)

        binding.onTheWay.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.onTheWay.setTextColor(blueColor)

        binding.delivered.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.delivered.setTextColor(blueColor)

        binding.preparing.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.preparing.setTextColor(blueColor)
    }

    private fun selectTap5() {
        defaultVal = 5
        viewModel.currentSelection = 5
        resetPage()

        viewModel.getMyOrdersPreparing(currentPage, paginate)
        binding.preparing.setBackgroundResource(R.drawable.rounded_colored_btn)
        binding.preparing.setTextColor(whiteColor)

        binding.cancelled.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.cancelled.setTextColor(blueColor)

        binding.onTheWay.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.onTheWay.setTextColor(blueColor)

        binding.delivered.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.delivered.setTextColor(blueColor)

        binding.Waiting.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.Waiting.setTextColor(blueColor)
    }

    private fun selectTap1() {
        defaultVal = 1
        viewModel.currentSelection = 1
        resetPage()
        viewModel.getMyOrdersOnTheWay(currentPage, paginate)
        binding.onTheWay.setBackgroundResource(R.drawable.rounded_colored_btn)
        binding.onTheWay.setTextColor(whiteColor)

        binding.cancelled.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.cancelled.setTextColor(blueColor)

        binding.delivered.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.delivered.setTextColor(blueColor)

        binding.preparing.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.preparing.setTextColor(blueColor)

        binding.Waiting.setBackgroundResource(R.drawable.button_blue_raduis)
        binding.Waiting.setTextColor(blueColor)
    }

    private fun setUpRecyclerViewForMyOrders(value: Int) {
        adapter = MyNewOrdersAdapter(this)
        binding.ordersRecyclerView.adapter = adapter
        when (value) {
            1 -> selectTap1()
            2 -> selectTap2()
            3 -> selectTap3()
            4 -> selectTap4()
            5 -> selectTap5()
        }
    }

    private fun loadNextPageData() {
        if (currentPage >= maxPages) {
            return
        }
        currentPage++

        if (defaultVal == 1) {
            loadOnTheWayOrders()
        } else if (defaultVal == 2) {
            loadCancelledOrders()
        } else if (defaultVal == 3) {
            loadDeliveredOrders()
        } else if (defaultVal == 4) {
            loadWaitingOrders()
        } else {
            loadPreparingOrders()
        }
    }

    fun loadCancelledOrders() {
        viewModel.getMyOrdersCancelled(
            currentPage,
            paginate,
        )
    }

    fun loadOnTheWayOrders() {
        viewModel.getMyOrdersOnTheWay(
            currentPage,
            paginate,
        )
    }

    fun loadDeliveredOrders() {
        viewModel.getMyOrdersDelivered(
            currentPage,
            paginate,
        )
    }

    fun loadPreparingOrders() {
        viewModel.getMyOrdersPreparing(
            currentPage,
            paginate,
        )
    }

    fun loadWaitingOrders() {
        viewModel.getMyOrdersWaiting(
            currentPage,
            paginate,
        )
    }

    private fun resetPage() {
        currentPage = 1
        maxPages = 1
        onTheWayOrders.clear()
        preparingOrders.clear()
        waitingOrders.clear()
        cancelledOrders.clear()
        deliveredOrders.clear()
    }

    override fun onStop() {
        super.onStop()
        if(listener != 1){
            viewModel.currentSelection = 4
        }
        viewModel.clearObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun clickOnItem(id: String) {
        val action = MyOrderHistoryDirections
            .actionMyOrderHistoryToMyOrderDetails(id)
        Log.d("AAA", "id is $id")
        listener = 1
        findNavController().navigate(action)
    }

    override fun clickOnReOrder(id: String) {
        viewModel.reorder(id.toInt())
    }
}