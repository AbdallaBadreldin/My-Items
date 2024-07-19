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
import store.msolapps.flamingo.presentation.home.category.adapter.ChildOfChildAdapter
import store.msolapps.flamingo.presentation.home.category.adapter.ChildOfChildAdapterActions
import store.msolapps.flamingo.presentation.home.category.adapter.ProductsActions
import store.msolapps.flamingo.presentation.home.category.adapter.ProductsAdapter
import store.msolapps.flamingo.presentation.home.category.adapter.SingleParentCategoryAdapter
import store.msolapps.flamingo.presentation.home.category.adapter.SingleParentCategoryAdapterActions
import store.msolapps.flamingo.presentation.home.home.CardViewModel

@AndroidEntryPoint
class ShowSingleCategory(
    private var idMain: Long? = null
) : BaseFragment(), SingleParentCategoryAdapterActions, ChildOfChildAdapterActions,
    FilterImplementation, ProductsActions {
    private var _binding: FragmentNewAllCatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShowSingleCategoryViewModel by activityViewModels()
    private val cartViewModel: CardViewModel by activityViewModels()
    private val filterViewModel: FilterViewModel by activityViewModels()

    /* we have four adapters */
    //three for categories
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var childAdapter: ChildOfChildAdapter
    val list = mutableListOf<CategoriesResponseModel.Data>()

    //one for products
    private lateinit var parentsAdapter: SingleParentCategoryAdapter

    //page setup
    private var currentPage = 0

    private var maxPages = 1
    val paginate = 9

    //filter Attributes
    var min: Int? = null
    var max: Int? = null
    private var filterName: String? = null
    private var categoriesIds: MutableList<Long>? = null
    private var minStandard: Double? = null
    private var maxStandard: Double? = null
    private var noOfProducts: Int? = null
    private var shownProducts: MutableSet<ProductsData> = mutableSetOf()
    private val args: ShowSingleCategoryArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewAllCatBinding.inflate(inflater, container, false)

        args.let {
            idMain = it.idMain
        }
        if (resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            binding.cartIcon.scaleX = -1f
        } else {
            binding.cartIcon.scaleX = 1f
        }
        setupObservers()
        setupAdapters()
        setupListeners()
        cartViewModel.getCartData()
//        startGettingData()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        startGettingData()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        binding.cartLayout.setOnClickListener {
            findNavController().navigate(ShowSingleCategoryDirections.actionShowSingleCategoryToNavigationCart())
        }
        binding.backIc.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.categoryDropdown.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.searchViewEdt.setOnClickListener {
//            findNavController().navigate(ShowSingleCategoryDirections.actionShowSingleCategoryToSearchFragment())
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
                    && maxPages > currentPage
                    && !recyclerView.canScrollVertically(
                        RecyclerView.FOCUS_DOWN
                    )
                ) {
                    if (!viewModel.getProductsDataLoading.value!!) {
                        loadNextPageData()
                        Log.v(
                            "TAG", "END OF RECYCLERVIEW IS REACHED $maxPages and $currentPage" +
                                    " and $noOfProducts"
                        )
                    }
                } else {
                    // END OF RECYCLERVIEW IS NOT REACHED
                    Log.v(
                        "TAG", "END OF RECYCLERVIEW IS NOT REACHED $maxPages and $currentPage" +
                                " and $noOfProducts"
                    )

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
                val parentId = posCatParent(it.data)
                list.add(it.data[parentId])
                it.data = list
                if (it.data[0].children.isNotEmpty() && it.data.isNotEmpty()) {
                    parentsAdapter.setData(it.data[0].children, 0)
                    if (it.data[0].children[0].children.isNotEmpty()) {
                        if (resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                            binding.arrowToRight.scaleX = -1f // Flip the image horizontally for RTL
                        } else {
                            binding.arrowToRight.scaleX = 1f // Default orientation for LTR
                        }
                        binding.arrowToRight.visibility = View.VISIBLE
                        childAdapter.setData(it.data[0].children[0].children, 0)
                    } else {
                        binding.arrowToRight.visibility = View.GONE
                    }
                }
                loadProductsDataByCategory()
                binding.categoryName.text = it.data[0].name
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
                noOfProducts = it.data.per_page
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
                    productsAdapter.clearData()
                    productsAdapter.updateData(shownProducts)
                    binding.productsRecycler.post {
                        binding.productsRecycler.scrollToPosition(0)
                    }
                }
            }
        }
        cartViewModel.cart.observe(viewLifecycleOwner)
        {
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

    private fun loadProductsDataByCategory() {
        if (viewModel.getCategoriesData.value == null) {
            //TODO("Show no data")
            return
        }
        val data = viewModel.getCategoriesData.value
        if (data!!.data[0].children.isEmpty()) {
            loadProducts(data.data[0].category_id)
            binding.subName.text = data.data[0].name
            binding.arrowToRight.visibility = View.GONE
        } else {
            if (data.data[0].children[parentsAdapter.getSelectedItemId()].children.isNotEmpty()) {
                binding.subName.text = data.data[0].children[parentsAdapter.getSelectedItemId()]
                    .children[childAdapter.getSelectedItemId()].name
                loadProducts(
                    data.data[0].children[parentsAdapter.getSelectedItemId()]
                        .children[childAdapter.getSelectedItemId()].category_id
                )
                if (resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                    binding.arrowToRight.scaleX = -1f // Flip the image horizontally for RTL
                } else {
                    binding.arrowToRight.scaleX = 1f // Default orientation for LTR
                }
                binding.arrowToRight.visibility = View.VISIBLE
            } else if (data.data[0].children.isNotEmpty()) {
                binding.subName.text =
                    data.data[0].children[parentsAdapter.getSelectedItemId()].name
                loadProducts(data.data[0].children[parentsAdapter.getSelectedItemId()].category_id)
            } else {
                binding.subName.text = data.data[0].name
                loadProducts(data.data[0].category_id)
                binding.arrowToRight.visibility = View.GONE
            }
        }
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

    private fun startGettingData() {
        viewModel.getCategories()
    }

    private fun setupAdapters() {
        parentsAdapter = SingleParentCategoryAdapter(this)
        productsAdapter = ProductsAdapter(cartViewModel, this)
        childAdapter = ChildOfChildAdapter(this, 0)
        binding.mainSubRecycler.adapter = parentsAdapter
        binding.productsRecycler.adapter = productsAdapter
        binding.subSubRecycler.adapter = childAdapter
//        binding.rec.adapter= childOfChildrenAdapter  //we will need it when the add third child level to the app
        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.productsRecycler.layoutManager = layoutManager
    }

    private fun loadNextPageData() {
        if (currentPage >= maxPages) {
            return
        }
        currentPage++
        loadProductsDataByCategory()
    }

    fun updateCategories() {
        childAdapter.updateData(
            viewModel.getCategoriesData.value!!.data[0].children[parentsAdapter.mSelectedItem].children,
            childAdapter.getSelectedItemId()
        )
    }

    private fun posCatParent(categories: MutableList<CategoriesResponseModel.Data>): Int {
        return categories.indexOfFirst {
            it.id == idMain
        }
    }

    override fun onClickMainCategoryAdapter(
        categoriesData: MutableList<CategoriesResponseModel.Data.ChildrenCategoriesResponseModel>,
        position: Int
    ) {
        resetFilter()
        parentsAdapter.setSelectedItem(position)
        if (categoriesData[position].children.isEmpty()) {
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

    private fun reloadData() {
        currentPage = 1
        productsAdapter.clearData()
        shownProducts.clear()
        loadProductsDataByCategory()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onFilterListener(minValue: Int, maxValue: Int, filterTxt: String) {
        min = minValue
        max = maxValue
        filterName = filterTxt
        reloadData()
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearObserver()
        cartViewModel.clearObservers()
        resetFilter()
        currentPage = 1
    }

    override fun onItemClicked(position: Int, item: ProductsData) {
        findNavController().navigate(
            MobileNavigationDirections.actionGoToShowNormalProduct(
                id = item
            )
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

    override fun onClickOnAnyAction() {
        cartViewModel.getCartData()
    }

}

