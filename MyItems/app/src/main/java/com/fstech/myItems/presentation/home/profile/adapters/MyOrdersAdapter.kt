package store.msolapps.flamingo.presentation.home.profile.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import store.msolapps.domain.models.response.OrderResponseModel1
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.NewOrderItemBinding
import store.msolapps.flamingo.util.LocalTime


class MyNewOrdersAdapter(private val listener: MyNewOrdersNavigator) :
    RecyclerView.Adapter<MyNewOrdersAdapter.MyNewOrdersViewHolder1>() {
    private lateinit var context: Context

    class MyNewOrdersViewHolder1(itemView: View, var itemRowBinding: NewOrderItemBinding) :
        RecyclerView.ViewHolder(itemView)


    private val differCallback = object :
        DiffUtil.ItemCallback<OrderResponseModel1.DataOrderResponseModel.Data1OrderResponseModel>() {
        override fun areItemsTheSame(
            oldItem: OrderResponseModel1.DataOrderResponseModel.Data1OrderResponseModel,
            newItem: OrderResponseModel1.DataOrderResponseModel.Data1OrderResponseModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: OrderResponseModel1.DataOrderResponseModel.Data1OrderResponseModel,
            newItem: OrderResponseModel1.DataOrderResponseModel.Data1OrderResponseModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyNewOrdersViewHolder1 {
        context = parent.context
        val itemBinding: NewOrderItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.new_order_item, parent, false
        )
        return MyNewOrdersViewHolder1(itemBinding.root, itemBinding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyNewOrdersViewHolder1, position: Int) {
        val item = differ.currentList[position]
        if (!item.order_products.isNullOrEmpty()) {
            if (item.order_products[0].image != "")
                Glide.with(context)
                    .load(item.order_products[0].image)
                    .placeholder(R.drawable.product_placeholder)
                    .into(holder.itemRowBinding.photo)

            if (context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                holder.itemRowBinding.nameNewOrderItem.text = "${item.id} #"
            } else {
                holder.itemRowBinding.nameNewOrderItem.text = "# ${item.id}"
            }
        }

        holder.itemRowBinding.price.text =
            context.getString(R.string.egps, item.final_total_amount.toString())
        if (context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            holder.itemRowBinding.orderIdTvNewOrderItem.text = "${item.id} #"
        } else {
            holder.itemRowBinding.orderIdTvNewOrderItem.text = "# ${item.id}"
        }
        holder.itemRowBinding.totalItemsTvNewOrderItem.text = item.order_products.size.toString()
        holder.itemRowBinding.deliverToTv.text = context.getString(R.string.delivering_to)
        holder.itemRowBinding.addressTvNewOrderItem.text = item.address.name
        when (item.status) {
            "pending" -> holder.itemRowBinding.statusTvNewOrderItem.text = context.getString(R.string.waiting)
            "preparing" -> holder.itemRowBinding.statusTvNewOrderItem.text = context.getString(R.string.preparing)
            "is_ready" -> holder.itemRowBinding.statusTvNewOrderItem.text = context.getString(R.string.is_ready)
            "on_the_way" -> holder.itemRowBinding.statusTvNewOrderItem.text = context.getString(R.string.on_the_way)
            "assign" -> holder.itemRowBinding.statusTvNewOrderItem.text = context.getString(R.string.on_the_way)
            "pos_check" -> holder.itemRowBinding.statusTvNewOrderItem.text = context.getString(R.string.on_the_way)
            "delivered" -> {
                holder.itemRowBinding.statusTvNewOrderItem.text = context.getString(R.string.delivered)
                holder.itemRowBinding.statusTvNewOrderItem.setTextColor(context.resources.getColor(R.color.green))
            }
        }

        holder.itemRowBinding.locatTime.text = LocalTime.convertToFullDate2(item.date)

        holder.itemRowBinding.reOrder.setOnClickListener {
            listener.clickOnReOrder(item.id.toString())
        }

        holder.itemRowBinding.orderDetails.setOnClickListener {
            listener.clickOnItem(item.id.toString())
        }

    }


}

interface MyNewOrdersNavigator {
    fun clickOnItem(id: String)
    fun clickOnReOrder(id: String)
}