package store.msolapps.flamingo.presentation.home.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.domain.models.response.CartResponseModel
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentCartBinding
import store.msolapps.flamingo.presentation.home.cart.actions.CartAdapterActions
import store.msolapps.flamingo.presentation.home.cart.adapter.CartAdapter
import store.msolapps.flamingo.presentation.home.home.CardViewModel

@AndroidEntryPoint
class CartFragment : BaseFragment(), CartAdapterActions {

    private var _binding: FragmentCartBinding? = null
    private val viewModel: CartViewModel by viewModels()
    private val cardViewModel: CardViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter
    private val deliveryCharge = 0.0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        setUpListeners()
        return binding.root
    }

    private fun setUpListeners() {
        binding.imageView14.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.bottomCardLayout.buttonShowSuggestionPage.setOnClickListener {
            findNavController().navigate(
                CartFragmentDirections.actionNavigationCartToShowSingleCategorySuggestionFragment(
                    viewModel.cart.value?.show_cat_id ?: 0
                )
            )
        }
    }

    private fun setUpAdapters() {
        cartAdapter = CartAdapter(this)
        val llm = LinearLayoutManager(requireContext())
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.cartRecycler.layoutManager = llm
        binding.cartRecycler.adapter = cartAdapter
    }

    private fun observeCartViewModel() {
        viewModel.cart.observe(viewLifecycleOwner) {
            if (it.data.isEmpty()) {
                showEmptyPageRoutine(true)
                binding.buttonCart.setOnClickListener {
                    findNavController().popBackStack()
                }
            } else {
                showEmptyPageRoutine(false)
                if (it.banner_num) {
                    //should set color and set listeners of the button if server says we can checkout
                    binding.buttonCart.setOnClickListener {
                        //check if user got default address
                        if (!viewModel.isDefaultAddressExist()) {
                            Toast.makeText(context,getString(R.string.please_add_address),Toast.LENGTH_LONG).show()
                        } else {
                            findNavController().navigate(CartFragmentDirections.actionNavigationCartToCheckoutFragment())
                        }
                    }
                    binding.buttonCart.setBackgroundResource(R.color.black_blue)
                } else {
                    //should show toast or color the button if server says we cannot checkout
                    binding.buttonCart.setBackgroundResource(R.color.inactive_button)
                    binding.buttonCart.setOnClickListener {
                        Toast.makeText(context,getString(R.string.please_add_more_products),Toast.LENGTH_LONG).show()
                    }
                }
                binding.buttonCart.isEnabled = it.banner_num
                //button Banner Data
                Glide.with(requireContext()).load(it.banner_icon).error(R.drawable.cart_megaphone)
                    .into(binding.bottomCardLayout.imageView28)
                binding.bottomCardLayout.textView70.text = it.banner_message
                val apiText = it.cat_name
                val formattedText = apiText.replace(" ", "\n")

                binding.bottomCardLayout.btn.text = formattedText
                if (resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                    binding.bottomCardLayout.arrowGolden.scaleX =
                        -1f // Flip the image horizontally for RTL
                } else {
                    binding.bottomCardLayout.arrowGolden.scaleX = 1f // Default orientation for LTR
                }

                showEmptyPageRoutine(false)
                binding.textView26.text = "(${it.data.size})"
                binding.cartRecycler.visibility = View.VISIBLE
                cartAdapter.setCartData(it.data)
                var total = 0.0
                for (item in it.data) {
                    if (item.discount_flag != 0) total += item.price
                    else total += item.discount_price
                }
                binding.bottomCardLayout.textView33.text = it.total_price.toString()
                binding.bottomCardLayout.deliverFees.text =
                    deliveryCharge.toString() //delievery chages
                binding.bottomCardLayout.textView35.text =
                    "${it.total_price + deliveryCharge}" //total
                binding.bottomCardLayout.textView30.text =
                    getString(R.string.items_count, it.data.size.toString())
            }
        }
        viewModel.error.observe(viewLifecycleOwner) {
            showEmptyPageRoutine(true)
        }
        viewModel.isLoad.observe(viewLifecycleOwner) {
            if (it) showLoading()
            else hideLoading()
        }

    }

    private fun showEmptyPageRoutine(flag: Boolean) {
        if (flag) {
            //it's empty
            binding.constraintLayout2.visibility = View.GONE
            binding.cartRecycler.visibility = View.GONE
            binding.textView26.visibility = View.GONE
            binding.textView25.visibility = View.GONE
            binding.imageView3.visibility = View.VISIBLE
            binding.textView22.visibility = View.VISIBLE
            binding.textView28.visibility = View.VISIBLE
            binding.buttonCart.text = getString(R.string.shop_now)
            binding.buttonCart.setBackgroundResource(R.color.black_blue)
        } else {
            //it's not empty
            binding.constraintLayout2.visibility = View.VISIBLE
            binding.cartRecycler.visibility = View.VISIBLE
            binding.textView26.visibility = View.VISIBLE
            binding.textView25.visibility = View.VISIBLE
            binding.imageView3.visibility = View.GONE
            binding.textView22.visibility = View.GONE
            binding.textView28.visibility = View.GONE
            binding.buttonCart.text = getString(R.string.checkout)
        }
    }

    private fun getData() {
        viewModel.getCartData()
    }

    override fun onResume() {
        super.onResume()
        getData()
        setUpAdapters()
        observeCartViewModel()
        observeCardData()
        observeCardLoading()
    }

    private fun observeCardLoading() {
        cardViewModel.addProductToCartLoading.observe(viewLifecycleOwner) {

        }
        cardViewModel.removeProductFromCartLoading.observe(viewLifecycleOwner) {

        }
        cardViewModel.addProductToFavouriteLoading.observe(viewLifecycleOwner) {

        }
        cardViewModel.removeProductFromFavouriteLoading.observe(viewLifecycleOwner) {

        }
    }

    private fun observeCardData() {
        cardViewModel.addProductToCartData.observe(viewLifecycleOwner) {
            viewModel.getCartData()
        }
        cardViewModel.removeProductFromCartData.observe(viewLifecycleOwner) {
            viewModel.getCartData()
        }
        cardViewModel.addProductToFavouriteData.observe(viewLifecycleOwner) {
            viewModel.getCartData()
        }
        cardViewModel.removeProductFromFavouriteData.observe(viewLifecycleOwner) {
            viewModel.getCartData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun addOneProductToCart(item: CartResponseModel.DataCartResponseModel, position: Int) {
        cardViewModel.addProductToCart(item.id.toString(), 1)
    }

    override fun removeOneProductFromCart(
        item: CartResponseModel.DataCartResponseModel, position: Int
    ) {
        cardViewModel.addProductToCart(item.id.toString(), -1)
    }

    override fun removeProductsFromCart(
        item: CartResponseModel.DataCartResponseModel, position: Int
    ) {
        cardViewModel.removeProductFromCart(item.id.toString())
    }
}