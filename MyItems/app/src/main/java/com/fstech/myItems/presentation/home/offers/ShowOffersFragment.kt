package store.msolapps.flamingo.presentation.home.offers

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import store.msolapps.domain.models.request.ProductsData
import store.msolapps.domain.models.response.CategoriesResponseModel
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.MobileNavigationDirections
import store.msolapps.flamingo.databinding.FragmentNewAllCatBinding
import store.msolapps.flamingo.presentation.filter.FilterBottomSheet
import store.msolapps.flamingo.presentation.filter.FilterImplementation
import store.msolapps.flamingo.presentation.home.category.adapter.ProductsActions
import store.msolapps.flamingo.presentation.home.category.adapter.ProductsAdapter
import store.msolapps.flamingo.presentation.home.home.CardViewModel
import store.msolapps.flamingo.presentation.home.offers.adapters.ChildAdapter
import store.msolapps.flamingo.presentation.home.offers.adapters.ChildAdapterActions
import store.msolapps.flamingo.presentation.home.offers.adapters.ParentAdapter
import store.msolapps.flamingo.presentation.home.offers.adapters.ParentAdapterActions

class ShowOffersFragment(
    private var type: String? = null,
    private var name: String? = null,
    private var offerType: String? = null,
    private var home: Int? = null
) : BaseFragment(), ParentAdapterActions, FilterImplementation, ChildAdapterActions,
    ProductsActions {
    private var _binding: FragmentNewAllCatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShowOffersViewModel by activityViewModels()
    private val cardViewModel: CardViewModel by activityViewModels()

    private var shownProducts: MutableSet<ProductsData> = mutableSetOf()

    private var currentPage = 0

    private var maxPages = 1
    val paginate = 10

    //filter Attributes
    var min: Int? = null
    var max: Int? = null
    private var filterName: String? = null
    private var categoriesIds: MutableList<Long>? = null
    private var minStandard: Double? = null
    private var maxStandard: Double? = null

    /* we have four adapters */
    //three for categories
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var childAdapter: ChildAdapter

    val list = mutableListOf<CategoriesResponseModel.Data>()

    //one for products
    private lateinit var parentsAdapter: ParentAdapter
    private val args: ShowOffersFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewAllCatBinding.inflate(inflater, container, false)
        args.let {
            type = it.type
            name = it.name
            offerType = it.offerType
        }
        viewModel.getOffersName(type = type!!)
        binding.categoryName.text = name.toString()
        cardViewModel.getCartData()
        if (resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            binding.cartIcon.scaleX = -1f
        } else {
            binding.cartIcon.scaleX = 1f
        }
        setupAdapters()
        setupObservers()
        setupListeners()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        binding.cartLayout.setOnClickListener {
            findNavController().navigate(ShowOffersFragmentDirections.actionShowOffersFragmentToNavigationCart())
        }
        binding.backIc.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.categoryDropdown.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.searchViewEdt.setOnClickListener {
//            findNavController().navigate(ShowOffersFragmentDirections.actionShowOffersFragmentToSearchFragment())
            findNavController().navigate(MobileNavigationDirections.navigateToSearchFragmentFromHome())
        }
        binding.filter.setOnClickListener {
            minStandard?.let { _ ->
                maxStandard?.let { _ ->
                    val filterDialog =
                        FilterBottomSheet(this, minStandard!!, maxStandard!!)
                    filterDialog.show(parentFragmentManager, filterDialog.tag)
                }
            }
        }


        binding.productsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val position =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                if (position + 1 == productsAdapter.itemCount
                    && maxPages > currentPage
                    && !recyclerView.canScrollVertically(
                        RecyclerView.FOCUS_DOWN
                    )
                ) {
                    if (!viewModel.getProductsDataLoading.value!!)
                        loadNextPageData()
                    Log.v("TAG", "END OF RECYCLERVIEW IS REACHED11")
                } else {
                    // END OF RECYCLERVIEW IS NOT REACHED
                    Log.v("TAG", "END OF RECYCLERVIEW IS NOT REACHED")

                }
            }

        })
    }

    private fun setupObservers() {
        viewModel.getCategoryOffersLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.getCategoryOffersData.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.data.isNullOrEmpty()) {
                    //there there's no data
                    return@observe
                }
                parentsAdapter.setData(it)
                childAdapter.setData(it.data[0].children, 0)
                binding.subName.text = it.data[0].name
                loadProductsDataByCategory(it, 0, 0)
            }
        }
        viewModel.getProductsDataLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.getProductsData.observe(viewLifecycleOwner) {
            if (it != null) {
                val response = it.data.data
                //when one product show up we add new swipe listener for the user to make it easier to swipe for him
                if ((maxStandard ?: 0.0) < it.max)
                    maxStandard = it.max
                if ((minStandard ?: 0.0) > it.min || minStandard == 0.0 || minStandard == null)
                    minStandard = it.min
                shownProducts.clear()
                for (i in 0 until response.size) {
                    shownProducts.add(
                        ProductsData(
                            id = response[i].id.toString(),
                            name = response[i].name,
                            desc = response[i].name,
                            image = response[i].standard!!.image,
                            price = response[i].standard!!.price,
                            stock = response[i].in_stock,
                            cart = response[i].cart,
                            liked = response[i].liked,
                            notified = response[i].notified,
                            barcode = response[i].standard!!.barcode,
                            discountFlag = response[i].standard!!.discount_flag,
                            discountPrice = response[i].standard!!.discount_price,
                            unit = response[i].unit,
                            lists = response[i].lists.size
                        )
                    )
                }
                maxPages = it.data.last_page
                currentPage = it.data.current_page
                if (currentPage != 1) {
                    productsAdapter.updateData(shownProducts)
                    binding.productsRecycler.post {
                        binding.productsRecycler.scrollToPosition(((productsAdapter.itemCount - response.size) - 1))
                    }
                } else {
                    productsAdapter.updateData(shownProducts)
                    binding.productsRecycler.post {
                        binding.productsRecycler.smoothScrollToPosition(0)
                    }
                }
            }
        }
        cardViewModel.cart.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.cart_count == 0) {
                    binding.cartBadge.visibility = View.GONE
                } else {
                    binding.cartBadge.visibility = View.VISIBLE
                    binding.cartBadge.text = it.cart_count.toString()
                }
            }
        }
    }

    private fun setupAdapters() {
        parentsAdapter = ParentAdapter(this)
        productsAdapter = ProductsAdapter(cardViewModel = cardViewModel, this)
        childAdapter = ChildAdapter(this, 0)
        binding.mainSubRecycler.adapter = parentsAdapter
        binding.productsRecycler.adapter = productsAdapter
        binding.subSubRecycler.adapter = childAdapter
        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.productsRecycler.layoutManager = layoutManager
    }

    fun loadProducts(categoryId: String) {
        viewModel.getProducts(
            categoryId,
            currentPage,
            paginate,
            offer = 1,
            min,
            max,
            filterName,
            mulCatsIds = categoriesIds,
            type = offerType!!
        )
    }

    private fun reloadData() {
        currentPage = 1
        productsAdapter.clearData()
        shownProducts.clear()
        loadProductsDataByCategory(
            viewModel.getCategoryOffersData.value!!,
            parentsAdapter.mSelectedItem,
            childAdapter.getSelectedItemId()
        )
    }

    private fun loadProductsDataByCategory(
        data: CategoriesResponseModel,
        parent: Int,
        child: Int,
    ) {
        if (data.data[parent].children.isNotEmpty()) {
            loadProducts(data.data[parent].children[child].category_id)
        } else {
            loadProducts(data.data[parent].category_id)
        }
    }

    private fun loadNextPageData() {
        if (currentPage >= maxPages) {
            return
        }
        currentPage++
        loadProductsDataByCategory(
            viewModel.getCategoryOffersData.value!!,
            parentsAdapter.mSelectedItem,
            childAdapter.getSelectedItemId(),
        )
    }

    override fun onClickMainCategoryAdapter(
        categoriesData: CategoriesResponseModel,
        position: Int
    ) {
        parentsAdapter.setSelectedItem(position)
    }

    override fun onChangeParentAdapterSelectedItem() {
        childAdapter.setSelectedItem(0)
        updateCategories()
        min = null
        max = null
        minStandard = null
        maxStandard = null
        filterName = ""
    }

    fun updateCategories() {
        childAdapter.setData(
            viewModel.getCategoryOffersData.value!!.data[parentsAdapter.mSelectedItem].children,
            childAdapter.getSelectedItemId()
        )
    }

    override fun onFilterListener(minValue: Int, maxValue: Int, filterTxt: String) {
        min = minValue
        max = maxValue
        filterName = filterTxt
        reloadData()
    }

    override fun onClickChildItemOnSubCat(categoryId: String, position: Int) {
        childAdapter.setSelectedItem(position)
    }

    override fun onChangeChildSelectedItem() {
        updateCategories()
        min = null
        max = null
        minStandard = null
        maxStandard = null
        filterName = ""
        reloadData()
    }

    override fun onItemClicked(position: Int, item: ProductsData) {
        findNavController().navigate(
            MobileNavigationDirections.actionGoToShowNormalProduct(
                id = item
            )
        )
    }

    override fun onClickOnAnyAction() {
        cardViewModel.getCartData()
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearObservers()
    }
}
