package store.msolapps.flamingo.presentation.home.category.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import store.msolapps.domain.models.request.ProductsData
import store.msolapps.flamingo.R
import store.msolapps.flamingo.presentation.home.home.CardViewModel
import java.util.Locale
import kotlin.math.abs

class ProductsAdapter(
    private val cardViewModel: CardViewModel,
    private val listener: ProductsActions,
) : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

    private lateinit var context: Context
    private var products: MutableSet<ProductsData> = mutableSetOf()

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

        holder.itemView.findViewById<ConstraintLayout>(R.id.cardViewOfProduct).isClickable = true
        holder.itemView.findViewById<ConstraintLayout>(R.id.cardViewOfProduct).isEnabled = true
        holder.itemView.findViewById<ImageView>(R.id.productCard_addToCart).isClickable = true
        holder.itemView.findViewById<ImageView>(R.id.productCard_addToCart).isEnabled = true
        holder.itemView.findViewById<ImageView>(R.id.productCard_removeToCart).isClickable = true
        holder.itemView.findViewById<ImageView>(R.id.productCard_removeToCart).isEnabled = true
        holder.itemView.findViewById<ImageView>(R.id.item_image).isEnabled = true
        holder.itemView.findViewById<ImageView>(R.id.item_image).isClickable = true
        holder.itemView.findViewById<ImageView>(R.id.productCard_addToFacourite).isEnabled = true
        holder.itemView.findViewById<ImageView>(R.id.productCard_addToFacourite).isClickable = true

        holder.itemView.apply {
            val productList = products.toList()
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
                    .error(R.drawable.flamingo_placeholder)
                    .placeholder(R.drawable.flamingo_placeholder)
                    .into(holder.itemView.findViewById(R.id.item_image))

                holder.itemView.findViewById<TextView>(R.id.product_name).text =
                    productList[position].name
                if (productList[position].discountFlag == 0) {
                    holder.itemView.findViewById<TextView>(R.id.product_price).visibility =
                        View.VISIBLE
                    holder.itemView.findViewById<TextView>(R.id.product_discountPercent).visibility =
                        View.INVISIBLE
                    holder.itemView.findViewById<TextView>(R.id.product_discount).visibility =
                        View.INVISIBLE
                    holder.itemView.findViewById<TextView>(R.id.product_price).text =
                        String.format("%.2f", productList[position].price)
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
                        String.format(
                            "%.2f",
                            productList[position].discountPrice.toString().toDouble()
                        )
                    holder.itemView.findViewById<TextView>(R.id.product_discount).text =
                        String.format(
                            "%.2f",
                            productList[position].price.toString().toDouble()
                        )

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
                        listener.onClickOnAnyAction()
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
                                View.INVISIBLE
                            holder.itemView.findViewById<ImageView>(R.id.productCard_removeToCart)
                                .visibility =
                                View.INVISIBLE
                        }
                        holder.itemView.findViewById<TextView>(R.id.product_count).text =
                            productList[position].cart.toString()
                        cardViewModel.getCartData()
                        listener.onClickOnAnyAction()
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
                            productList[position].liked = false
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
                            productList[position].liked = true
                        }
                        listener.onClickOnAnyAction()
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
    fun updateData(products: MutableSet<ProductsData>) {
        this.products.addAll(products)
        notifyDataSetChanged()
    }
}

interface ProductsActions {
    fun onItemClicked(position: Int, item: ProductsData)
    fun onClickOnAnyAction()
}