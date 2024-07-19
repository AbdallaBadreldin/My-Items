package store.msolapps.flamingo.presentation.home.products

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.domain.models.response.ProductResponseModel
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentShowProductBinding
import store.msolapps.flamingo.presentation.home.home.CardViewModel

@AndroidEntryPoint
class ShowProductFromHome : BaseFragment() {
    private var _binding: FragmentShowProductBinding? = null
    private val binding get() = _binding!!
    private val args: ShowProductFromHomeArgs by navArgs()
    private val productFromHomeViewModel: ShowProductFromHomeViewModel by viewModels()
    private val cardViewModel: CardViewModel by viewModels()
    private var flag = 0

    private var id: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowProductBinding.inflate(inflater, container, false)
        args.let {
            id = it.id
        }
        productFromHomeViewModel.getProductData(id!!)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setupObservers()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setupObservers() {
        cardViewModel.isLoadCart.observe(viewLifecycleOwner) {
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
        cardViewModel.addProductToFavouriteLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        cardViewModel.isLoadUpdate.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        cardViewModel.removeProductFromCartLoading.observe(viewLifecycleOwner) {
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
        productFromHomeViewModel.getProductDataLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        productFromHomeViewModel.getProductData.observe(viewLifecycleOwner) {
            if (it != null) {
                setDataOnScreen(it)
                binding.productCount.text = it.data.cart.toString()
                if (it.data.cart > 0) {
                    binding.productCount.visibility = View.VISIBLE
                    binding.minsButton.visibility = View.VISIBLE
                } else {
                    binding.productCount.visibility = View.GONE
                    binding.minsButton.visibility = View.GONE
                    binding.plusButton.setImageDrawable(
                        binding.plusButton.context.getDrawable(
                            R.drawable.inner_special_add_bottom
                        )
                    )
                }
                if (it.data.liked) {
                    binding.imageViewHeart.setImageResource(R.drawable.bigger_active_heart)
                } else {
                    binding.imageViewHeart.setImageResource(R.drawable.bigger_inactive_heart)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val window: Window = requireActivity().window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.statusBarColor = Color.WHITE
                }
            }
            cardViewModel.addProductToCartData.observe(viewLifecycleOwner) {
                if (it != null) {
                    findNavController().navigateUp()
                    Toast.makeText(context, getString(R.string.product_added), Toast.LENGTH_LONG)
                        .show()
                }
            }
            cardViewModel.removeProductFromCartData.observe(viewLifecycleOwner) {
                if (it != null) {
                    findNavController().navigateUp()
                    Toast.makeText(context, getString(R.string.product_updated), Toast.LENGTH_LONG)
                        .show()
                }
            }
            cardViewModel.addProductToFavouriteData.observe(viewLifecycleOwner) {
                if (it != null) {
                    binding.imageViewHeart.setImageResource(R.drawable.bigger_active_heart)
                }
            }
            cardViewModel.removeProductFromFavouriteData.observe(viewLifecycleOwner) {
                if (it != null) {
                    binding.imageViewHeart.setImageResource(R.drawable.bigger_inactive_heart)
                }
            }
            setupListeners(it!!)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupListeners(product: ProductResponseModel) {

        binding.imageViewBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.minsButton.setOnClickListener {
            if (!cardViewModel.isOnline()) {
                //user is not online
            } else {
                flag = 2
                val currentCount = binding.productCount.text.toString().toIntOrNull() ?: 0
                val newCount = currentCount - 1
                if (newCount > 0) {
                    binding.productCount.visibility = View.VISIBLE
                    binding.minsButton.visibility = View.VISIBLE
                    binding.productCount.text = newCount.toString()
                    binding.plusButton.setImageDrawable(
                        binding.plusButton.context.getDrawable(
                            R.drawable.show_product_plus_button
                        )
                    )
                } else {
                    binding.productCount.visibility = View.GONE
                    binding.minsButton.visibility = View.GONE
                    binding.productCount.text = 0.toString()
                    binding.plusButton.setImageDrawable(
                        binding.plusButton.context.getDrawable(
                            R.drawable.inner_special_add_bottom
                        )
                    )
                }
            }
        }
        binding.plusButton.setOnClickListener {
            if (!cardViewModel.isOnline()) {
                //user is not online
            } else {
                flag = 1
                val currentCount = binding.productCount.text.toString().toIntOrNull() ?: 0
                val newCount = currentCount + 1
                if (newCount > 0) {
                    binding.productCount.visibility = View.VISIBLE
                    binding.minsButton.visibility = View.VISIBLE
                    binding.productCount.text = newCount.toString()
                    binding.plusButton.setImageDrawable(
                        binding.plusButton.context.getDrawable(
                            R.drawable.show_product_plus_button
                        )
                    )
                }
            }
        }
        binding.imageViewHeart.setOnClickListener {
            if (!cardViewModel.isOnline()) {
                //user is not online
            } else if (!(product.data.liked)) {
                cardViewModel.addProductToFavourite(product.data.id.toString())
                binding.imageViewHeart.setImageResource(R.drawable.bigger_inactive_heart)
                product.data.liked = true
            } else {
                cardViewModel.removeProductFromFavourite(product.data.id.toString())
                binding.imageViewHeart.setImageResource(R.drawable.bigger_active_heart)
                product.data.liked = false
            }
        }
        binding.button.setOnClickListener {
            if (flag == 1) {
                if (binding.productCount.text.toString().toInt() > 0) {
                    cardViewModel.addProductToCart(
                        product.data.id.toString(),
                        binding.productCount.text.toString().toInt() - product.data.cart
                    )
                }
            } else {
                if (binding.productCount.text.toString().toInt() > 0) {
                    cardViewModel.updateProductInCart(
                        product.data.id.toString(), binding.productCount.text.toString().toInt()
                    )
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.Please_Add_Quantity),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    @SuppressLint("PrivateResource", "ResourceType")
    private fun setDataOnScreen(product: ProductResponseModel) {
        binding.showProductName.text = product.data.name
        binding.showProductDescription.text = product.data.description
        Glide.with(this).load(product.data.standard.image).error(R.drawable.flamingo_placeholder)
            .placeholder(R.drawable.flamingo_placeholder).into(binding.showProductImage)

        if (product.data.liked) {
            binding.imageViewHeart.setImageResource(R.drawable.bigger_active_heart)
        } else {
            binding.imageViewHeart.setImageResource(R.drawable.bigger_inactive_heart)
        }
        // there's a discount on the product
        if (product.data.standard.discount_flag == 0) {
            binding.textView18.visibility = View.GONE
            binding.realPriceDiscount.visibility = View.GONE

//            binding.productDiscountPercent.visibility = View.GONE
            binding.priceDiscount.visibility = View.VISIBLE
            binding.priceDiscount.text =
                String.format("%.2f", product.data.standard.price.toString())
        }
        //if there's no discount on the product
        else {
            binding.textView18.visibility = View.VISIBLE
            binding.realPriceDiscount.visibility = View.VISIBLE
            binding.realPriceDiscount.paintFlags =
                binding.realPriceDiscount.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.priceDiscount.text =
                removeUnusedZeros(product.data.standard.discount_price.toString())
            binding.realPriceDiscount.text =
                removeUnusedZeros(product.data.standard.price.toString())
        }

    }
}

