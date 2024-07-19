package store.msolapps.flamingo.presentation.home.products

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
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
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentShowProductBinding
import store.msolapps.flamingo.presentation.home.home.CardViewModel

@AndroidEntryPoint
class ShowNormalProductFragment : BaseFragment() {
    private var _binding: FragmentShowProductBinding? = null
    private val binding get() = _binding!!
    private val args: ShowNormalProductFragmentArgs by navArgs()

    private val cardViewModel: CardViewModel by viewModels()
    private var flag = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowProductBinding.inflate(inflater, container, false)
        setupUi()
        setDataOnScreen()
        SetupListeners()
        setupObservers()
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupUi() {
        binding.productCount.text = args.id.cart.toString()
        if (args.id.cart > 0) {
            binding.productCount.visibility = View.VISIBLE
            binding.minsButton.visibility = View.VISIBLE
        } else {
            binding.productCount.visibility = View.GONE
            binding.minsButton.visibility = View.GONE
            binding.plusButton
                .setImageDrawable(
                    binding.plusButton.context.getDrawable(
                        R.drawable.inner_special_add_bottom
                    )
                )
        }
        if (args.id.liked) {
            binding.imageViewHeart.setImageResource(R.drawable.bigger_active_heart)
        } else {
            binding.imageViewHeart.setImageResource(R.drawable.bigger_inactive_heart)
        }
        val window: Window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.WHITE
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun SetupListeners() {
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
                    binding.plusButton
                        .setImageDrawable(
                            binding.plusButton.context.getDrawable(
                                R.drawable.show_product_plus_button
                            )
                        )
                } else {
                    binding.productCount.visibility = View.GONE
                    binding.minsButton.visibility = View.GONE
                    binding.productCount.text = 0.toString()
                    binding.plusButton
                        .setImageDrawable(
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
                    binding.plusButton
                        .setImageDrawable(
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
            } else if (!(args.id.liked)) {
                cardViewModel.addProductToFavourite(args.id.id.toString())
                binding.imageViewHeart.setImageResource(R.drawable.bigger_inactive_heart)
                args.id.liked = true
            } else {
                cardViewModel.removeProductFromFavourite(args.id.id.toString())
                binding.imageViewHeart.setImageResource(R.drawable.bigger_active_heart)
                args.id.liked = false
            }
        }
        binding.button.setOnClickListener {
            if (flag == 1) {
                if (binding.productCount.text.toString().toInt() > 0) {
                    cardViewModel
                        .addProductToCart(
                            args.id.id,
                            binding.productCount.text.toString().toInt() - args.id.cart
                        )
                }
            } else {
                if (binding.productCount.text.toString().toInt() > 0) {
                    cardViewModel.updateProductInCart(
                        args.id.id,
                        binding.productCount.text.toString().toInt()
                    )
                } else {
                  Toast.makeText(context,  getString(R.string.Please_Add_Quantity),Toast.LENGTH_LONG).show()
                }
            }
        }
    }

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
        cardViewModel.addProductToCartData.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigateUp()
                Toast.makeText(context,getString(R.string.product_added),Toast.LENGTH_LONG).show()
            }
        }
        cardViewModel.removeProductFromCartData.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigateUp()
                Toast.makeText(context,getString(R.string.product_updated),Toast.LENGTH_LONG).show()
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

    }

    private fun setDataOnScreen() {
        val item = args.id
        binding.showProductName.text = item.name
        binding.showProductDescription.text = item.desc
        Glide.with(this)
            .load(args.id.image)
            .error(R.drawable.flamingo_placeholder)
            .placeholder(R.drawable.flamingo_placeholder)
            .into(binding.showProductImage)


        // there's a discount on the product
        if (item.discountFlag == 0) {
            binding.textView18.visibility = View.GONE
            binding.realPriceDiscount.visibility = View.GONE

//            binding.productDiscountPercent.visibility = View.GONE
            binding.priceDiscount.visibility = View.VISIBLE
            binding.priceDiscount.text = String.format("%.2f", args.id.price.toString())
        }
        //if there's no discount on the product
        else {
            binding.textView18.visibility = View.VISIBLE
            binding.realPriceDiscount.visibility = View.VISIBLE
//            binding.productDiscountPercent.visibility = View.VISIBLE
            // binding.priceDiscount.visibility = View.VISIBLE
            binding.realPriceDiscount.paintFlags =
                binding.realPriceDiscount.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.priceDiscount.text =
                removeUnusedZeros(item.discountPrice.toString())
            binding.realPriceDiscount.text =
                removeUnusedZeros(item.price.toString())
        }
    }

    override fun onStop() {
        super.onStop()
        cardViewModel.clearObservers()
    }
}