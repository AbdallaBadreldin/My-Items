package store.msolapps.flamingo.presentation.home.checkout.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import store.msolapps.domain.models.response.CartResponseModel
import store.msolapps.flamingo.databinding.ItemOrderSummuryBinding
import store.msolapps.flamingo.presentation.home.checkout.actions.OrderSummaryActions

class OrderSummaryAdapter(private val listener: OrderSummaryActions) :
    RecyclerView.Adapter<OrderSummaryAdapter.ViewPagerViewHolder>() {
    private var data: MutableList<CartResponseModel.DataCartResponseModel> = mutableListOf()

    inner class ViewPagerViewHolder(val binding: ItemOrderSummuryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(item: CartResponseModel.DataCartResponseModel) {
            binding.textView61.text = item.name.toString().trim()
            binding.textView15.text = "${item.qty.toString().trim()}X"
            if (item.discount_flag == 1){
              binding.textView59.text=  item.discount_price.toString().trim()
            }
            else{
                binding.textView59.text=  item.price.toString().trim()
            }

        }

    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {

        val binding = ItemOrderSummuryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.setData(data[position])
    }

    fun setData(data: MutableList<CartResponseModel.DataCartResponseModel>) {
        this.data = data
        notifyDataSetChanged()
    }
}