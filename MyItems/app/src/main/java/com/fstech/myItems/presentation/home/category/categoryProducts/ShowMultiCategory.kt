package store.msolapps.flamingo.presentation.home.category.categoryProducts

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
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.domain.models.request.ProductsData
import store.msolapps.domain.models.response.CategoriesResponseModel
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.MobileNavigationDirections
import store.msolapps.flamingo.databinding.FragmentNewAllCatBinding
import store.msolapps.flamingo.presentation.filter.FilterBottomSheet
import store.msolapps.flamingo.presentation.filter.FilterImplementation
import store.msolapps.flamingo.presentation.home.FilterViewModel
import store.msolapps.flamingo.presentation.home.category.adapter.ProductsActions
import store.msolapps.flamingo.presentation.home.category.adapter.ProductsAdapter
import store.msolapps.flamingo.presentation.home.home.CardViewModel
import store.msolapps.flamingo.presentation.home.offers.adapters.ChildAdapter
import store.msolapps.flamingo.presentation.home.offers.adapters.ChildAdapterActions
import store.msolapps.flamingo.presentation.home.offers.adapters.ParentAdapter
import store.msolapps.flamingo.presentation.home.offers.adapters.ParentAdapterActions

@AndroidEntryPoint
class ShowMultiCategory : BaseFragment(), ParentAdapterActions, FilterImplementation,
    ChildAdapterActions, ProductsActions {
    private var _binding: FragmentNewAllCatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShowMultiCategoryViewModel by activityViewModels()
    private val cartViewModel: CardViewModel by activityViewModels()
    private val filterViewModel: FilterViewModel by activityViewModels()

    private var multiCats: MutableList<Long>? = null
    private var name: String? = null
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var childAdapter: ChildAdapter
    private lateinit var parentsAdapter: ParentAdapter
    val list = mutableListOf<CategoriesResponseModel.Data>()

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

    private var shownProducts: MutableSet<ProductsData> = mutableSetOf()
    private val args: ShowCategoryArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewAllCatBinding.inflate(inflater, container, false)
        arguments?.let {
            multiCats = it.getLongArray("numberList")?.toMutableList()!!
            name = it.getString("name")
        }
        binding.categoryName.text = name.toString()
        startGettingData()
        if (resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            binding.cartIcon.scaleX = -1f
        } else {
            binding.cartIcon.scaleX = 1f
        }
        cartViewModel.getCartData()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setupAdapters()
        setupObservers()
        setupListeners()
    }

    private fun startGettingData() {
        viewModel.getCategories(multiCats = multiCats!!)
    }

    private fun setupAdapters() {
        parentsAdapter = ParentAdapter(this)
        productsAdapter = ProductsAdapter(cardViewModel = cartViewModel, this)
        childAdapter = ChildAdapter(this, 0)
        binding.mainSubRecycler.adapter = parentsAdapter
        binding.productsRecycler.adapter = productsAdapter
        binding.subSubRecycler.adapter = childAdapter
        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.productsRecycler.layoutManager = layoutManager
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        binding.cartLayout.setOnClickListener {
            findNavController().navigate(ShowMultiCategoryDirections.actionShowMultiCategoryToNavigationCart())
        }
        binding.backIc.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.categoryDropdown.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.searchViewEdt.setOnClickListener {
//            findNavController().navigate(ShowMultiCategoryDirections.actionShowMultiCategoryToSearchFragment())
            findNavController().navigate(MobileNavigationDirections.navigateToSearchFragmentFromHome())
        }
        binding.filter.setOnClickListener {
            minStandard?.let { _ ->
                maxStandard?.let { _ ->
                    val filterDialog =
                        FilterBottomSheet(this, minStandard!! - 1, maxStandard!! + 1)
                    filterDialog.show(parentFragmentManager, filterDialog.tag)
                }
            }
        }

        binding.productsRecycler.post {
            binding.productsRecycler.scrollToPosition(0)
        }

        binding.productsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val position =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                if (position + 1 == productsAdapter.itemCount
                    && maxPages > currentPage && !recyclerView.canScrollVertically(
                        RecyclerView.FOCUS_DOWN
                    )
                ) {
                    if (!viewModel.getProductsDataLoading.value!!)
                        loadNextPageData()
                    Log.v("TAG", "END OF RECYCLERVIEW IS REACHED")
                } else {
                    // END OF RECYCLERVIEW IS NOT REACHED
                    Log.v("TAG", "END OF RECYCLERVIEW IS NOT REACHED")
                }
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupObservers() {
        viewModel.getCategoriesLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.getCategoriesData.observe(viewLifecycleOwner) {
            if (it != null) {
                parentsAdapter.setData(it)
                if (it.data[0].children.isEmpty()) {
                    binding.arrowToRight.visibility = View.GONE
                } else {
                    childAdapter.setData(it.data[0].children, 0)
                    if (resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                        binding.arrowToRight.scaleX = -1f // Flip the image horizontally for RTL
                    } else {
                        binding.arrowToRight.scaleX = 1f // Default orientation for LTR
                    }
                    binding.arrowToRight.visibility = View.VISIBLE
                }
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
        cartViewModel.cart.observe(viewLifecycleOwner) {
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

    private fun loadProductsDataByCategory(
        data: CategoriesResponseModel,
        parent: Int,
        child: Int,
    ) {
        if (data.data[parent].children.isNotEmpty()) {
            loadProducts(data.data[parent].children[child].category_id)
            binding.subName.text = data.data[parent].children[child].name
        }
        else {
            loadProducts(data.data[parent].category_id)
            binding.subName.text = data.data[0].name

        }
    }

    private fun reloadData() {
        currentPage = 1
        productsAdapter.clearData()
        shownProducts.clear()
        loadProductsDataByCategory(
            viewModel.getCategoriesData.value!!,
            parentsAdapter.mSelectedItem,
            childAdapter.getSelectedItemId()
        )
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
            mulCatsIds = categoriesIds
        )
    }

    private fun loadNextPageData() {
        if (currentPage >= maxPages) {
            return
        }
        currentPage++
        loadProductsDataByCategory(
            viewModel.getCategoriesData.value!!,
            parentsAdapter.mSelectedItem,
            childAdapter.getSelectedItemId(),
        )
    }

    override fun onFilterListener(minValue: Int, maxValue: Int, filterTxt: String) {
        min = minValue
        max = maxValue
        filterName = filterTxt
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
        cartViewModel.getCartData()
    }

    override fun onClickChildItemOnSubCat(categoryId: String, position: Int) {
        childAdapter.setSelectedItem(position)
        resetFilter()
    }

    override fun onChangeChildSelectedItem() {
        updateCategories()
        resetFilter()
        reloadData()
    }

    override fun onClickMainCategoryAdapter(
        categoriesData: CategoriesResponseModel,
        position: Int
    ) {
        parentsAdapter.setSelectedItem(position)
        resetFilter()
        if (categoriesData.data[position].children.isEmpty()) {
            binding.arrowToRight.visibility = View.GONE
        } else {
            if (resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                binding.arrowToRight.scaleX = -1f // Flip the image horizontally for RTL
            } else {
                binding.arrowToRight.scaleX = 1f // Default orientation for LTR
            }
            binding.arrowToRight.visibility = View.VISIBLE
        }
    }

    override fun onChangeParentAdapterSelectedItem() {
        childAdapter.setSelectedItem(0)
        updateCategories()
        resetFilter()
    }

    fun updateCategories() {
        childAdapter.setData(
            viewModel.getCategoriesData.value!!.data[parentsAdapter.mSelectedItem].children,
            childAdapter.getSelectedItemId()
        )
    }

    fun resetFilter() {
        min = null
        max = null
        minStandard = null
        maxStandard = null
        filterName = ""
        filterViewModel.resetFilter()
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearObserver()
        cartViewModel.clearObservers()
        resetFilter()
        currentPage = 1
    }
}