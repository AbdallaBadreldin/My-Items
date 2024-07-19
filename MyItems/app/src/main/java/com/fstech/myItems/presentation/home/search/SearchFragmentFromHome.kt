package store.msolapps.flamingo.presentation.home.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.domain.models.request.ProductsData
import store.msolapps.domain.models.response.DataFilterSearchResponse1
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.MobileNavigationDirections
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentSearchBinding
import store.msolapps.flamingo.presentation.filter.FilterBottomSheet
import store.msolapps.flamingo.presentation.filter.FilterImplementation
import store.msolapps.flamingo.presentation.home.FilterViewModel
import store.msolapps.flamingo.presentation.home.category.adapter.ProductsActions
import store.msolapps.flamingo.presentation.home.category.adapter.ProductsAdapter
import store.msolapps.flamingo.presentation.home.home.CardViewModel

@AndroidEntryPoint
class SearchFragmentFromHome : BaseFragment(), FilterImplementation, ProductsActions {

    private var _binding: FragmentSearchBinding? = null
    private lateinit var newProductsAdapter: ProductsAdapter
    private lateinit var productsForYouAdapter: ProductsAdapter
    private val filterViewModel: FilterViewModel by activityViewModels()

    private var minStandard: Double? = null
    private var maxStandard: Double? = null
    private var min: Int? = null
    private var max: Int? = null
    private var filterName: String? = null
    private var offerApi = 1
    private var currentPage = 1
    private var totalPages = 1
    private var catsIdsFilter: MutableList<Long> = ArrayList()
    private var txtSearch = ""
    private var firstCall = true
    private var firstCall1 = true
    private var allProducts: MutableList<DataFilterSearchResponse1> = ArrayList()
    private val productsData: MutableSet<ProductsData> = mutableSetOf()
    private val productsDataForYou: MutableSet<ProductsData> = mutableSetOf()
    private val cardViewModel: CardViewModel by viewModels()

    private val viewModel: SearchViewModel by viewModels()
    private var maxPages = 1

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        viewModel.getProductsForYou()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setUpAdapters()
        setupObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupListeners() {
        binding.backIc.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.filter.setOnClickListener {
            minStandard?.let { _ ->
                maxStandard?.let { _ ->
                    if (minStandard == 0.0 && maxStandard == 0.0) {
                        return@setOnClickListener
                    } else {
                        val filterDialog =
                            FilterBottomSheet(this, minStandard!!, maxStandard!! + 1)
                        filterDialog.show(parentFragmentManager, filterDialog.tag)
                    }
                }
            }
        }
        binding.searchViewEdt.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                min = null
                max = null
                filterName = null
                catsIdsFilter.clear()
                resetFilter()
                validateAndApi(binding.searchViewEdt.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
        binding.productsSearchRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val position =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                currentPage = viewModel.page
                totalPages = viewModel.totalPages
                if (position + 1 == newProductsAdapter.itemCount
                    && totalPages > currentPage
                    && !recyclerView.canScrollVertically(
                        RecyclerView.FOCUS_DOWN
                    )
                ) {
                    if (!viewModel.getSearchProductsLoading.value!!) {
                        if (min == 0 && max == 0)
                            viewModel.filterSearchProduct(
                                requireContext(),
                                1,
                                searchQuery = txtSearch,
                                categoriesIds = catsIdsFilter,
                                offer = offerApi,
                                min = null,
                                max = null,
                                sort = filterName
                            )
                        else
                            viewModel.filterSearchProduct(
                                requireContext(),
                                2,
                                searchQuery = txtSearch,
                                categoriesIds = catsIdsFilter,
                                offer = offerApi,
                                min = min,
                                max = max,
                                sort = filterName
                            )
                    } else {
                        Log.v("TAG", "END OF RECYCLERVIEW IS NOT REACHED")
                    }
                }
            }
        })
    }

    private fun setupObservers() {

        viewModel.getSearchProductsLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
                binding.productsCount.visibility = View.GONE
                binding.productsName.visibility = View.GONE

            } else {
                hideLoading()
            }
        }
        viewModel.getProductsForYouLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.getSearchProductsData.observe(viewLifecycleOwner) {
            if (it != null) {
                val response = it.data.data
                binding.productsCount.visibility = View.VISIBLE
                binding.productsName.visibility = View.VISIBLE
                binding.productsCount.text = getString(
                    R.string.products,
                    "( ${it.data.total} ${getString(R.string.results)} )"
                )
                binding.productsName.text = binding.searchViewEdt.text.toString()

                //when one product show up we add new swipe listener for the user to make it easier to swipe for him
                if ((maxStandard ?: 0.0) < it.max)
                    maxStandard = it.max
                if ((minStandard ?: 0.0) > it.min || minStandard == 0.0 || minStandard == null)
                    minStandard = it.min
                binding.productsSearchRv.visibility = View.VISIBLE
                binding.parentRecentSearch.visibility = View.GONE
                binding.recyclerForYouProducts.visibility = View.GONE
                productsData.clear()
                if (firstCall1) {
                    minStandard = it.min
                    maxStandard = it.max
                    firstCall1 = false
                }
                for (i in 0 until response.size) {
                    productsData.add(
                        ProductsData(
                            id = response[i].id.toString(),
                            name = response[i].name,
                            desc = response[i].name,
                            image = response[i].standard.image,
                            price = response[i].standard.price,
                            stock = response[i].in_stock,
                            cart = response[i].cart,
                            liked = response[i].liked,
                            notified = response[i].notified,
                            barcode = response[i].standard.barcode,
                            discountFlag = response[i].standard.discount_flag,
                            discountPrice = response[i].standard.discount_price,
                            unit = response[i].unit,
                            lists = response[i].lists.size
                        )
                    )
                }
                // currentPage = it.data.current_page
                if (currentPage != 1) {
                    newProductsAdapter.updateData(productsData)
                    binding.productsSearchRv.post {
                        binding.productsSearchRv.scrollToPosition(((newProductsAdapter.itemCount - response.size) - 1))
                    }
                } else {
                    newProductsAdapter.clearData()
                    newProductsAdapter.updateData(productsData)
                    binding.productsSearchRv.post {
                        binding.productsSearchRv.smoothScrollToPosition(0)
                    }
                }
            }
        }
        viewModel.getProductsForYou.observe(viewLifecycleOwner) {
            productsDataForYou.clear()
            for (i in 0 until it.data.size) {
                productsDataForYou.add(
                    ProductsData(
                        id = it.data[i].id.toString(),
                        name = it.data[i].name,
                        desc = it.data[i].name,
                        image = it.data[i].standard.image,
                        price = it.data[i].standard.price,
                        stock = 1,
                        cart = it.data[i].cart,
                        liked = it.data[i].liked,
                        notified = it.data[i].notified,
                        barcode = it.data[i].standard.barcode,
                        discountFlag = it.data[i].standard.discount_flag,
                        discountPrice = it.data[i].standard.discount_price,
                        unit = it.data[i].unit
                    )
                )
            }

            if (productsDataForYou.isNotEmpty()) {
                binding.forYouTv.visibility = View.VISIBLE
                setUpProductsForYou(productsDataForYou)
            }
        }
    }

    private fun setUpProductsForYou(productsDataForYou: MutableSet<ProductsData>) {
        productsForYouAdapter = ProductsAdapter(cardViewModel, this)
        productsForYouAdapter.clearData()
        productsForYouAdapter.updateData(productsDataForYou)
        binding.recyclerForYouProducts.adapter = productsForYouAdapter
        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerForYouProducts.layoutManager = layoutManager
    }

    private fun setUpAdapters() {
        newProductsAdapter = ProductsAdapter(cardViewModel, this)
        binding.productsSearchRv.adapter = newProductsAdapter
        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.productsSearchRv.layoutManager = layoutManager

    }

    private fun hideKeyboardFrom() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun validateAndApi(text: String?) {
        if (text!!.isNotEmpty()) {
            if (text.toString() != txtSearch) {
                txtSearch = text.toString()
                allProducts.clear()
                productsData.clear()
                newProductsAdapter.clearData()
            }
            if (firstCall1) {
                allProducts.clear()
                productsData.clear()
                newProductsAdapter.clearData()
            }
            if (text.length >= 2) {
                hideKeyboardFrom()
                offerApi = 1
                resetFilter()
                catsIdsFilter.clear()
                firstCall = true

                viewModel.filterSearchProduct(
                    requireContext(),
                    3,
                    searchQuery = txtSearch,
                    categoriesIds = catsIdsFilter,
                    offer = offerApi,
                    min = null,
                    max = null,
                    sort = filterName,
                    true
                )

                hideKeyboardFrom()
            }
        }
    }

    override fun onFilterListener(
        minValue: Int,
        maxValue: Int,
        filterTxt: String,
    ) {
        min = minValue
        max = maxValue
        if (filterTxt != "") {
            filterName = filterTxt
            offerApi = 0
        } else {
            filterName = null
            offerApi = 1
        }
        productsData.clear()
        allProducts.clear()
        newProductsAdapter.clearData()
        viewModel.filterSearchProduct(
            requireContext(),
            6,
            searchQuery = txtSearch,
            categoriesIds = catsIdsFilter,
            offer = offerApi,
            min = min,
            max = max,
            sort = filterName,
            true
        )
        firstCall = true
    }

    override fun onPause() {
        super.onPause()
        firstCall1 = true
        firstCall = true
    }

    fun resetFilter() {
        firstCall1 = true
        min = null
        max = null
        minStandard = null
        maxStandard = null
        filterName = ""
        filterViewModel.resetFilter()
    }

    override fun onItemClicked(position: Int, item: ProductsData) {
        findNavController().navigate(
            MobileNavigationDirections.actionGoToShowNormalProduct(
                id = item
            )
        )
    }

    override fun onResume() {
        super.onResume()
        if (!binding.searchViewEdt.text.toString().isNullOrEmpty()) {
            /*minStandard = min!!.toDouble()
            maxStandard = max!!.toDouble()*/
            catsIdsFilter.clear()
            productsData.clear()
            allProducts.clear()
            newProductsAdapter.clearData()
            firstCall1 = false
            viewModel.filterSearchProduct(
                requireContext(),
                3,
                searchQuery = txtSearch,
                categoriesIds = catsIdsFilter,
                offer = offerApi,
                min = null,
                max = null,
                sort = filterName,
                true
            )
        }
    }

    override fun onClickOnAnyAction() {
//        TODO("Not yet implemented")
    }
}