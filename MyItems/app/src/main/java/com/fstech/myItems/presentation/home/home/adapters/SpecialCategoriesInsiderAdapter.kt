package store.msolapps.flamingo.presentation.home.home.adapters

import android.annotation.SuppressLint
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import store.msolapps.domain.models.response.ProductsSpecialCategoriesResponse
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.ItemInnerSpecialDealsBinding
import store.msolapps.flamingo.presentation.home.home.CardViewModel
import store.msolapps.flamingo.presentation.home.home.actions.SpecialCategoriesInsiderActions
import java.util.Locale
import kotlin.math.abs

class SpecialCategoriesInsiderAdapter(
    private val listener: SpecialCategoriesInsiderActions,
    private val cardViewModel: CardViewModel
) :
    RecyclerView.Adapter<SpecialCategoriesInsiderAdapter.ViewPagerViewHolder>() {
    private var data: MutableList<ProductsSpecialCategoriesResponse> = mutableListOf()

    inner class ViewPagerViewHolder(val binding: ItemInnerSpecialDealsBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {

        val binding = ItemInnerSpecialDealsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewPagerViewHolder(binding)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {

        holder.itemView.apply {
            val productList = data.toList()
            if (productList[position].cart >= 1) {
                holder.itemView.findViewById<ImageView>(R.id.productCard_removeToCart).visibility =
                    View.VISIBLE
                holder.itemView.findViewById<TextView>(R.id.product_count).visibility =
                    View.VISIBLE
                holder.itemView.findViewById<TextView>(R.id.product_count).text =
                    productList[position].cart.toString()
                holder.itemView.findViewById<ImageView>(R.id.productCard_addToCart)
                    .setImageDrawable(
                        holder.itemView.findViewById<ImageView>(R.id.productCard_addToCart).context.getDrawable(
                            R.drawable.add_icon
                        )
                    )
            } else {
                holder.itemView.findViewById<ImageView>(R.id.productCard_removeToCart).visibility =
                    View.INVISIBLE
                holder.itemView.findViewById<TextView>(R.id.product_count).visibility =
                    View.INVISIBLE
                holder.itemView.findViewById<ImageView>(R.id.productCard_addToCart)
                    .setImageDrawable(
                        holder.itemView.findViewById<ImageView>(R.id.productCard_addToCart).context.getDrawable(
                            R.drawable.inner_special_add_bottom
                        )
                    )
            }
            if (productList[position].liked) {
                holder.itemView.findViewById<ImageView>(R.id.productCard_addToFacourite)
                    .setImageDrawable(
                        holder.itemView.findViewById<ImageView>(R.id.productCard_addToFacourite).context.getDrawable(
                            R.drawable.active_heart
                        )
                    )
            } else {
                holder.itemView.findViewById<ImageView>(R.id.productCard_addToFacourite)
                    .setImageDrawable(
                        holder.itemView.findViewById<ImageView>(R.id.productCard_addToFacourite).context.getDrawable(
                            R.drawable.inactive_heart
                        )
                    )
            }
            if (position in productList.indices) {
                Glide.with(this)
                    .load(productList[position].standard.image)
                    .error(R.drawable.flamingo_placeholder)
                    .placeholder(R.drawable.flamingo_placeholder)
                    .into(holder.itemView.findViewById(R.id.item_image))

                holder.itemView.findViewById<TextView>(R.id.product_name).text =
                    productList[position].name
                if (productList[position].standard.discount_flag == 0) {
                    holder.itemView.findViewById<TextView>(R.id.product_price).visibility =
                        View.VISIBLE
                    holder.itemView.findViewById<TextView>(R.id.product_discountPercent).visibility =
                        View.GONE
                    holder.itemView.findViewById<TextView>(R.id.product_discount).visibility =
                        View.GONE
                    holder.itemView.findViewById<TextView>(R.id.product_price).text =
                    String.format("%.2f",  productList[position].standard.price.toString())
                } else {
                    holder.itemView.findViewById<TextView>(R.id.product_price).visibility =
                        View.VISIBLE
                    holder.itemView.findViewById<TextView>(R.id.product_discountPercent).visibility =
                        View.VISIBLE
                    holder.itemView.findViewById<TextView>(R.id.product_discount).visibility =
                        View.VISIBLE

                    holder.itemView.findViewById<TextView>(R.id.product_discount).paintFlags =
                        holder.itemView.findViewById<TextView>(R.id.product_discount).paintFlags or
                                Paint.STRIKE_THRU_TEXT_FLAG
                    holder.itemView.findViewById<TextView>(R.id.product_price).text =
                        removeUnusedZeros(String.format("%.2f",
                            productList[position].standard.discount_price.toString().toDouble()))
                    holder.itemView.findViewById<TextView>(R.id.product_discount).text =
                        removeUnusedZeros(String.format("%.2f",
                            productList[position].standard.price.toString().toDouble()))

                    holder.itemView.findViewById<TextView>(R.id.product_discountPercent).text =
                        "${
                            getDiscountPercent(
                                productList[position].standard.price,
                                productList[position].standard.discount_price
                            )
                        }%"
                }
                holder.itemView.findViewById<ImageView>(R.id.productCard_addToFacourite)
                    .setOnClickListener {
                        if (productList[position].liked) {
                            cardViewModel.removeProductFromFavourite(
                                productList[position].id.toString()
                            )
                            holder.itemView.findViewById<ImageView>(R.id.productCard_addToFacourite)
                                .setImageDrawable(
                                    holder.itemView.findViewById<ImageView>(R.id.productCard_addToFacourite)
                                        .context.getDrawable(
                                            R.drawable.inactive_heart
                                        )
                                )
                            productList[position].liked = false
                        } else {
                            cardViewModel.addProductToFavourite(
                                productList[position].id.toString()
                            )
                            holder.itemView.findViewById<ImageView>(R.id.productCard_addToFacourite)
                                .setImageDrawable(
                                    holder.itemView.findViewById<ImageView>(R.id.productCard_addToFacourite)
                                        .context.getDrawable(
                                            R.drawable.active_heart
                                        )
                                )
                            productList[position].liked = true
                        }
                    }
                holder.itemView.findViewById<ImageView>(R.id.productCard_addToCart)
                    .setOnClickListener {
                        cardViewModel.addProductToCart(
                            productId = productList[position].id.toString(),
                            1
                        )
                        productList[position].cart += 1
                        holder.itemView.findViewById<ImageView>(R.id.productCard_removeToCart).visibility =
                            View.VISIBLE
                        holder.itemView.findViewById<TextView>(R.id.product_count).visibility =
                            View.VISIBLE
                        holder.itemView.findViewById<TextView>(R.id.product_count).text =
                            productList[position].cart.toString()
                        holder.itemView.findViewById<ImageView>(R.id.productCard_addToCart)
                            .setImageDrawable(
                                holder.itemView.findViewById<ImageView>(R.id.productCard_addToCart)
                                    .context.getDrawable(
                                        R.drawable.add_icon
                                    )
                            )
//                        cardViewModel.getCartData()
                    }

                holder.itemView.findViewById<ImageView>(R.id.productCard_removeToCart)
                    .setOnClickListener {
                        cardViewModel.addProductToCart(
                            productId = productList[position].id.toString(),
                            -1
                        )
                        productList[position].cart -= 1
                        if (productList[position].cart > 0) {
                            holder.itemView.findViewById<ImageView>(R.id.productCard_removeToCart)
                                .visibility =
                                View.VISIBLE
                            holder.itemView.findViewById<TextView>(R.id.product_count).visibility =
                                View.VISIBLE
                        } else {
                            holder.itemView.findViewById<ImageView>(R.id.productCard_addToCart)
                                .setImageDrawable(
                                    holder.itemView.findViewById<ImageView>(R.id.productCard_addToCart)
                                        .context.getDrawable(
                                            R.drawable.inner_special_add_bottom
                                        )
                                )
                            holder.itemView.findViewById<TextView>(R.id.product_count)
                                .visibility =
                                View.INVISIBLE
                            holder.itemView.findViewById<ImageView>(R.id.productCard_removeToCart)
                                .visibility =
                                View.INVISIBLE
                        }
                        holder.itemView.findViewById<TextView>(R.id.product_count).text =
                            productList[position].cart.toString()
//                        cardViewModel.getCartData()
                    }
                holder.itemView.findViewById<ImageView>(R.id.item_image).setOnClickListener {
                    listener.onInnerSpecialCategoriesClicked(
                        position = position,
                        productList[position]
                    )
                }
            }
        }
    }

    fun clearData() {
        this.data.clear()
    }

    fun setSpecialCategoryInsiderData(data: MutableList<ProductsSpecialCategoriesResponse>) {
        this.data = data
        if (data.isNotEmpty()) {
            notifyDataSetChanged()
        }
    }

    private fun removeUnusedZeros(price: String): String {
        return if (price.contains(".")) {
            price.replace("0*$".toRegex(), "").replace("\\.$".toRegex(), "")
        } else price
    }

    private fun getDiscountPercent(
        priceBeforeDiscount: Double,
        priceAfterDiscount: Double,
    ): String {
        return String.format(
            Locale.US,
            "%.1f",
            abs(((priceAfterDiscount * 100) / priceBeforeDiscount) - 100),
            Locale.ENGLISH
        ).toDouble().toString()
    }
}