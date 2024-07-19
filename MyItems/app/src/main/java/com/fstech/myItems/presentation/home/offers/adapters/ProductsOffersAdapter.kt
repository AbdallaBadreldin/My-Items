package store.msolapps.flamingo.presentation.home.offers.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import store.msolapps.domain.models.request.ProductsData
import store.msolapps.flamingo.R
import store.msolapps.flamingo.presentation.home.category.adapter.ProductsActions
import store.msolapps.flamingo.presentation.home.home.CardViewModel
import java.util.Locale
import kotlin.math.abs

class ProductOffersAdapter(
    private val cardViewModel: CardViewModel,
    private val listener: ProductsActions,
) :
    RecyclerView.Adapter<ProductOffersAdapter.ProductsViewHolder>() {

    private lateinit var context: Context
    private var products: MutableList<ProductsData> = mutableListOf()

    class ProductsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        context = parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductsViewHolder(itemView)

    }

    @SuppressLint("SetTextI18n", "CutPasteId", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {

        holder.itemView.apply {
            val productList = products.toList()
            if (productList[position].cart >= 1) {
                holder.itemView.findViewById<ImageView>(R.id.productCard_removeToCart).visibility =
                    View.VISIBLE
                holder.itemView.findViewById<TextView>(R.id.product_count).visibility =
                    View.VISIBLE
                holder.itemView.findViewById<TextView>(R.id.product_count).text =
                    productList[position].cart.toString()
            } else {
                holder.itemView.findViewById<ImageView>(R.id.productCard_addToCart)
                    .setImageDrawable(
                        holder.itemView.findViewById<ImageView>(R.id.productCard_addToCart).context.getDrawable(
                            R.drawable.inner_special_add_bottom
                        )
                    )
            }
            if (position in productList.indices) {
                Glide.with(this)
                    .load(productList[position].image)
                    .placeholder(R.drawable.product_placeholder)
                    .into(holder.itemView.findViewById(R.id.item_image))

                holder.itemView.findViewById<TextView>(R.id.product_name).text =
                    productList[position].name
                if (productList[position].discountFlag == 0) {
                    holder.itemView.findViewById<TextView>(R.id.product_price).visibility =
                        View.VISIBLE
                    holder.itemView.findViewById<TextView>(R.id.product_discountPercent).visibility =
                        View.GONE
                    holder.itemView.findViewById<TextView>(R.id.product_discount).visibility =
                        View.GONE
                    holder.itemView.findViewById<TextView>(R.id.product_price).text =
                        productList[position].price.toString()
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
                        removeUnusedZeros(productList[position].discountPrice.toString())
                    holder.itemView.findViewById<TextView>(R.id.product_discount).text =
                        removeUnusedZeros(productList[position].price.toString())

                    holder.itemView.findViewById<TextView>(R.id.product_discountPercent).text =
                        "${
                            getDiscountPercent(
                                productList[position].price,
                                productList[position].discountPrice
                            )
                        }%"
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
                holder.itemView.findViewById<ImageView>(R.id.productCard_addToCart)
                    .setOnClickListener {
                        cardViewModel.addProductToCart(
                            productId = productList[position].id,
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
                        cardViewModel.getCartData()
                    }

                holder.itemView.findViewById<ImageView>(R.id.productCard_removeToCart)
                    .setOnClickListener {
                        cardViewModel.addProductToCart(
                            productId = productList[position].id,
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
                                View.GONE
                            holder.itemView.findViewById<ImageView>(R.id.productCard_removeToCart)
                                .visibility =
                                View.GONE
                        }
                        holder.itemView.findViewById<TextView>(R.id.product_count).text =
                            productList[position].cart.toString()
                        cardViewModel.getCartData()
                    }
                holder.itemView.findViewById<ImageView>(R.id.productCard_addToFacourite)
                    .setOnClickListener {
                        if (productList[position].liked) {
                            cardViewModel.removeProductFromFavourite(
                                productList[position].id
                            )
                            holder.itemView.findViewById<ImageView>(R.id.productCard_addToFacourite)
                                .setImageDrawable(
                                    holder.itemView.findViewById<ImageView>(R.id.productCard_addToFacourite)
                                        .context.getDrawable(
                                            R.drawable.inactive_heart
                                        )
                                )
                        } else {
                            cardViewModel.addProductToFavourite(
                                productList[position].id
                            )
                            holder.itemView.findViewById<ImageView>(R.id.productCard_addToFacourite)
                                .setImageDrawable(
                                    holder.itemView.findViewById<ImageView>(R.id.productCard_addToFacourite)
                                        .context.getDrawable(
                                            R.drawable.active_heart
                                        )
                                )
                        }
                    }
                holder.itemView.findViewById<ImageView>(R.id.item_image).setOnClickListener {
                    listener.onItemClicked(position = position, productList[position])
                }
            }
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
        ).toDouble().toInt().toString()
    }

    override fun getItemCount(): Int {
        return products.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        this.products.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(products: MutableList<ProductsData>) {
        this.products.addAll(products)
        notifyDataSetChanged()
    }
}