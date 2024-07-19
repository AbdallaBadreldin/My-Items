package store.msolapps.flamingo.presentation.home.offers

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import store.msolapps.domain.models.request.ProductsData
import store.msolapps.domain.models.response.OfferName
import store.msolapps.domain.models.response.OffersModel
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.MobileNavigationDirections
import store.msolapps.flamingo.databinding.FragmentOffersBinding
import store.msolapps.flamingo.presentation.home.category.adapter.ProductsActions
import store.msolapps.flamingo.presentation.home.home.AddressBottomSheet
import store.msolapps.flamingo.presentation.home.home.CardViewModel
import store.msolapps.flamingo.presentation.home.offers.adapters.AllOffersDataAdapter
import store.msolapps.flamingo.presentation.home.offers.adapters.NewOffersNav
import store.msolapps.flamingo.presentation.home.offers.adapters.OffersAdapter
import store.msolapps.flamingo.presentation.home.offers.adapters.OffersAdapterActions

class OffersFragment : BaseFragment(), OffersAdapterActions, ProductsActions, NewOffersNav {

    private var _binding: FragmentOffersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: OffersViewModel by activityViewModels()
    private val cardViewModel: CardViewModel by activityViewModels()
    private lateinit var offersAdapter: OffersAdapter
    private lateinit var offersProductsAdapter: AllOffersDataAdapter

    private val arrNames: MutableList<ArrNamesOffers> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOffersBinding.inflate(inflater, container, false)
        if (resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            binding.cartIcon.scaleX = -1f
        } else {
            binding.cartIcon.scaleX = 1f
        }
        setupAdapters()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardViewModel.getCartData()
        viewModel.getOffersName()
        viewModel.getOffersData()
        viewModel.getDefaultAddress()
    }

    override fun onResume() {
        super.onResume()
        setupListeners()
        setupObservers()
    }

    fun setupListeners() {
        binding.searchViewEdt.setOnClickListener {
//            findNavController().navigate(OffersFragmentDirections.actionNavigationOffersToSearchFragment())
            findNavController().navigate(MobileNavigationDirections.navigateToSearchFragmentFromHome())
        }
        binding.textViewDefaultAddress.setOnClickListener {
            val addressBottomSheet = AddressBottomSheet()
            addressBottomSheet.show(parentFragmentManager, addressBottomSheet.tag)
        }
        binding.cartLayout.setOnClickListener {
            findNavController().navigate(OffersFragmentDirections.actionNavigationOffersToNavigationCart())
        }
    }

    fun setupObservers() {
        viewModel.getOfferNames.observe(viewLifecycleOwner) {
            if (it != null) {
                offersAdapter.setData(it.data, 0)
            }
        }
        viewModel.getOfferDataLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.getOfferData.observe(viewLifecycleOwner) {
            if (it != null) {

                arrNames.clear()
                val offersData: MutableSet<OffersModel> = mutableSetOf()

                val arrProductsDeals: MutableList<ProductsData> = ArrayList()
                val arrProductsNewArrivals: MutableList<ProductsData> = ArrayList()

                val deals = it.data.find {
                    if (viewModel.getLanguage() == "en") {
                        it.offer_name.contains("DEALS")

                    } else {
                        it.offer_name.contains("صفقات")
                    }
                }
                if (deals!!.products.isNotEmpty()) {
                    for (i in 0 until deals.products.size) {
                        arrProductsDeals.add(
                            ProductsData(
                                id = deals.products[i].id.toString(),
                                name = deals.products[i].name,
                                desc = deals.products[i].name,
                                image = deals.products[i].standard.image,
                                price = deals.products[i].standard.price,
                                stock = deals.products[i].in_stock,
                                cart = deals.products[i].cart,
                                liked = deals.products[i].liked,
                                notified = deals.products[i].notified,
                                barcode = deals.products[i].standard.barcode,
                                discountFlag = deals.products[i].standard.discount_flag,
                                discountPrice = deals.products[i].standard.discount_price,
                                unit = deals.products[i].unit
                            )
                        )
                    }
                    arrNames.add(ArrNamesOffers(deals.offer_name))
                }

                val newArrivals = it.data.find {
                    if (viewModel.getLanguage() == "en") {
                        it.offer_name.contains("New Arrivals")

                    } else {
                        it.offer_name.contains("القادمون")
                    }
                }
                if (newArrivals!!.products.isNotEmpty()) {
                    for (i in 0 until newArrivals.products.size) {
                        arrProductsNewArrivals.add(
                            ProductsData(
                                id = newArrivals.products[i].id.toString(),
                                name = newArrivals.products[i].name,
                                desc = newArrivals.products[i].name,
                                image = newArrivals.products[i].standard.image,
                                price = newArrivals.products[i].standard.price,
                                stock = newArrivals.products[i].in_stock,
                                cart = newArrivals.products[i].cart,
                                liked = newArrivals.products[i].liked,
                                notified = newArrivals.products[i].notified,
                                barcode = newArrivals.products[i].standard.barcode,
                                discountFlag = newArrivals.products[i].standard.discount_flag,
                                discountPrice = newArrivals.products[i].standard.discount_price,
                                unit = newArrivals.products[i].unit
                            )
                        )
                    }
                    arrNames.add(ArrNamesOffers(newArrivals.offer_name))
                }
                offersData.add(
                    OffersModel(
                        newArrivals.offer_name,
                        arrProductsNewArrivals,
                        newArrivals.offer_id
                    )
                )
                offersData.add(
                    OffersModel(
                        deals.offer_name,
                        arrProductsDeals,
                        deals.offer_id
                    )
                )
                setUpRecyclerOffers(offersData)
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
        viewModel.getDefaultAddressData.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.textViewDefaultAddress.text = it.data?.name
            }
        }
    }

    private fun setupAdapters() {
        if (arrNames.isNotEmpty())
            arrNames[0].click = true
        offersAdapter = OffersAdapter(this)
        binding.offerNamesRecyclerView.adapter = offersAdapter
    }

    private fun setUpRecyclerOffers(offersData: MutableSet<OffersModel>) {
        offersProductsAdapter = AllOffersDataAdapter(this, this, cardViewModel, offersData)
        binding.offersRv.adapter = offersProductsAdapter
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearObservers()
        cardViewModel.clearObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickOfferAdapter(offersData: MutableList<OfferName>, position: Int) {
        offersAdapter.setSelectedItem(position)
        if (position in 0 until offersAdapter.itemCount) {
            val view = binding.offersRv.getChildAt(position)
            if (view != null) {
                val target = binding.offersRv.top + view.top
                binding.scrollview.scrollTo(0, target)
            }
        }
    }

    override fun onItemClicked(position: Int, item: ProductsData) {
        findNavController().navigate(
            MobileNavigationDirections.actionGoToShowNormalProduct(
                id = item
            )
        )
    }

    override fun onClickOnAnyAction() {
        TODO("Not yet implemented")
    }

    override fun onClickOnSeeAll(offerId: String, name: String, offerType: String) {
        findNavController().navigate(
            OffersFragmentDirections.actionNavigationOffersToShowOffersFragment(
                type = splitOfferName(offerId),
                name = name,
                offerType = offerType
            )
        )
    }

    private fun splitOfferName(input: String): String {
        val parts = input.split("/")
        return parts.last() // Get the last part after splitting by "/"
    }
}

data class ArrNamesOffers(var name: String, var click: Boolean? = false)