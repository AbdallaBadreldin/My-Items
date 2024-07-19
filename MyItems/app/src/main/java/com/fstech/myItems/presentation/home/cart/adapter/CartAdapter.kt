package store.msolapps.flamingo.presentation.home.cart.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import store.msolapps.domain.models.response.CartResponseModel
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.ItemMycartBinding
import store.msolapps.flamingo.presentation.home.cart.actions.CartAdapterActions

class CartAdapter(private val listener: CartAdapterActions) :
    RecyclerView.Adapter<CartAdapter.ViewPagerViewHolder>() {
    private var data: MutableList<CartResponseModel.DataCartResponseModel> = mutableListOf()

    inner class ViewPagerViewHolder(val binding: ItemMycartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(item: CartResponseModel.DataCartResponseModel, position: Int) {
            binding.tvQuantity.text = item.qty.toString()
            if (item.container_color.isNotEmpty()) {
                val colorInt = Color.parseColor(item.container_color)
                binding.cardOfCart.strokeColor = colorInt
            } else {
                binding.cardOfCart.strokeColor = binding.root.context.getColor(R.color.white)
            }

            if (item.container_image.isNotEmpty()) {
                binding.smallProductImage.visibility = View.VISIBLE
                Glide.with(binding.root.context)
                    .load(item.container_image).into(binding.smallProductImage)
            } else {
                binding.smallProductImage.visibility = View.GONE
                binding.smallProductImage.visibility = View.GONE
            }

            Glide.with(binding.root.context)
                .load(item.image)
                .error(com.google.android.material.R.drawable.mtrl_ic_error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imageView9)
            binding.textView11.text = item.name
            //there are a discount
            if (item.discount_flag == 1) {
                binding.textView24.visibility = View.VISIBLE
                binding.textView19.visibility = View.VISIBLE
                binding.textView21.text = item.discount_price.toString()
                binding.textView24.text = item.price.toString()
                binding.textView24.paintFlags =
                    binding.textView24.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                //there are no discount
            } else {
                binding.textView24.visibility = View.GONE
                binding.textView19.visibility = View.GONE
                binding.textView21.text = item.price.toString()
            }
            //if client asked to make item clickable in future
//            binding.root.setOnClickListener {
//                listener.onClickStickyBanner(position = adapterPosition)
//            }
            binding.imageViewDeleteProduct.setOnClickListener {
                listener.removeProductsFromCart(item, position = position)
            }
            binding.imageViewAddProduct.setOnClickListener {
                listener.addOneProductToCart(item, position = position)
            }
            binding.imageViewRemoveProduct.setOnClickListener {
                listener.removeOneProductFromCart(item, position = position)
            }
        }

    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {

        val binding = ItemMycartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.setData(data[position], position)
    }

    fun setCartData(data: MutableList<CartResponseModel.DataCartResponseModel>) {
        this.data = data
        notifyDataSetChanged()
    }
}