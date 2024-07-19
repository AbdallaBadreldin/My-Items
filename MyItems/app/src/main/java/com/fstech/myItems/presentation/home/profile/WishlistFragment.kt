package store.msolapps.flamingo.presentation.home.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import store.msolapps.domain.models.request.ProductsData
import store.msolapps.domain.models.response.CategoryProductsResponseModel
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.MobileNavigationDirections
import store.msolapps.flamingo.databinding.FragmentWishlistBinding
import store.msolapps.flamingo.presentation.home.category.adapter.ProductsActions
import store.msolapps.flamingo.presentation.home.category.adapter.ProductsAdapter
import store.msolapps.flamingo.presentation.home.home.CardViewModel

class WishlistFragment : BaseFragment(), ProductsActions {

    private var _binding: FragmentWishlistBinding? = null


    private val viewModel: WishlistViewModel by activityViewModels()
    private val productsData: MutableSet<ProductsData> = mutableSetOf()
    private lateinit var productsAdapter: ProductsAdapter
    private val cardViewModel: CardViewModel by activityViewModels()
    private var updateAllProducts: MutableList<CategoryProductsResponseModel.DataCategoryProductsResponseModel.Data1CategoryProductsResponseModel> =
        ArrayList()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        viewModel.getWishlist()
        viewModel.getCartData()
        cardViewModel.getCartData()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setupListeners()
        setupObservers()
        setUpAdapters()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupListeners() {
        binding.backIc.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.addAll.setOnClickListener {
            viewModel.addAllWishlist()
        }
        binding.getCartLayout.setOnClickListener {
            findNavController()
                .navigate(WishlistFragmentDirections.actionWishlistFragmentToNavigationCart())
        }
    }

    private fun setupObservers() {
        viewModel.getWishlist.observe(viewLifecycleOwner) {
            if (it != null) {
                val response = it.data
                productsData.clear()
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
                        )
                    )
                }
                productsAdapter.updateData(productsData)
            }
        }
        viewModel.addAllcart.observe(viewLifecycleOwner){
            if (it != null) {
                productsData.clear()
                productsAdapter.clearData()
            }
        }
        viewModel.cart.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.data.isNotEmpty()) {
                    binding.getCartLayout.visibility = View.VISIBLE
                    binding.price.text = it.total_price.toString()
                    binding.cartCount.text = it.cart_count.toString()
                } else {
                    binding.getCartLayout.visibility = View.GONE
                }
            }
        }
        cardViewModel.cart.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.data.isNotEmpty()) {
                    binding.getCartLayout.visibility = View.VISIBLE
                    binding.price.text = it.total_price.toString()
                    binding.cartCount.text = it.cart_count.toString()
                } else {
                    binding.getCartLayout.visibility = View.GONE
                }
            }
        }
        cardViewModel.isLoadCart.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.isLoadWishlist.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.isLoadAddAllCart.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        cardViewModel.addProductToFavouriteLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        cardViewModel.addProductToCartLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        cardViewModel.removeProductFromFavouriteLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
    }

    private fun setUpAdapters() {
        productsAdapter = ProductsAdapter(cardViewModel, this)
        binding.productsSearchRv.adapter = productsAdapter
        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.productsSearchRv.layoutManager = layoutManager
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearObserver()
    }

    override fun onItemClicked(position: Int, item: ProductsData) {
        findNavController().navigate(
            MobileNavigationDirections.actionGoToShowNormalProduct(
                id = item
            )
        )
    }

    override fun onClickOnAnyAction() {
//        TODO("Not yet implemented")
    }
}