package store.msolapps.flamingo.presentation.home.checkout.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import store.msolapps.domain.models.response.DataGetSlotsResponse
import store.msolapps.domain.models.response.GetSlotsResponse
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.ItemBottomSlotsBottomSheetBinding
import store.msolapps.flamingo.presentation.home.checkout.actions.TimeAdapterActions

class TimeAdapter(private val listener: TimeAdapterActions) :
    RecyclerView.Adapter<TimeAdapter.ViewPagerViewHolder>() {
    private var data: GetSlotsResponse? = null
    var mSelectedItem = 0
    private lateinit var context: Context

    inner class ViewPagerViewHolder(val binding: ItemBottomSlotsBottomSheetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(item: DataGetSlotsResponse, position: Int) {
            if (item.max_orders <= item.orders.size) {
                //should hide it because No More available orders here
                binding.rootCard.strokeColor =
                    binding.root.context.getColor(R.color.white_transparent)
                binding.rootCard.setCardBackgroundColor(binding.root.context.getColor(R.color.white))
                binding.constraintBottomCheckoutFragment.setBackgroundResource(R.drawable.unavailable_slot)

                binding.textView34.text = "${item.from} - ${item.to}"
//                binding.textViewFeesInEgp.text = "EGP ${item.fees}"
                Log.d("timeAda","${context.getString(R.string.egps, item.fees)}")
                binding.textViewFeesInEgp.text = context.getString(R.string.egps, item.fees)
                binding.textView34.setTextColor(binding.root.context.getColor(R.color.grey))
                binding.textViewFeesInEgp.setTextColor(binding.root.context.getColor(R.color.grey))
                binding.deliveryFeesTv.setTextColor(binding.root.context.getColor(R.color.grey))

                binding.deliveryFeesTv.visibility = View.INVISIBLE
                binding.textViewFeesInEgp.visibility = View.INVISIBLE

                binding.tvFullBooked.visibility = View.VISIBLE

            } else if (mSelectedItem == position && item.max_orders <= item.orders.size) {
                // selected item and it's not available to be chosen
                if (mSelectedItem + 1 >= (data?.data?.size ?: 0))
                    ++mSelectedItem
                else
                    mSelectedItem = -1

                binding.rootCard.strokeColor =
                    binding.root.context.getColor(R.color.white_transparent)
                binding.rootCard.setCardBackgroundColor(binding.root.context.getColor(R.color.bacground_checout_carts))
                binding.constraintBottomCheckoutFragment.setBackgroundResource(R.drawable.unavailable_slot)

                binding.textView34.text = "${item.from} - ${item.to}"
                binding.textViewFeesInEgp.text = context.getString(R.string.egps, item.fees)
                binding.textView34.setTextColor(binding.root.context.getColor(R.color.grey))
                binding.textViewFeesInEgp.setTextColor(binding.root.context.getColor(R.color.grey))
                binding.deliveryFeesTv.setTextColor(binding.root.context.getColor(R.color.grey))

                binding.deliveryFeesTv.visibility = View.INVISIBLE
                binding.textViewFeesInEgp.visibility = View.INVISIBLE

                binding.tvFullBooked.visibility = View.VISIBLE
            } else if (mSelectedItem == position && item.max_orders > item.orders.size) {
                listener.setDataToViewModel(position, item)
                // selected item and it's available to be chosen
                binding.rootCard.strokeColor = binding.root.context.getColor(R.color.blue)
                binding.rootCard.setCardBackgroundColor(binding.root.context.getColor(R.color.black_blue))
                binding.constraintBottomCheckoutFragment.setBackgroundResource(R.color.black_blue)

                binding.textView34.text = "${item.from} - ${item.to}"
                binding.textViewFeesInEgp.text = context.getString(R.string.egps, item.fees)
                binding.textView34.setTextColor(binding.root.context.getColor(R.color.white))
                binding.textViewFeesInEgp.setTextColor(binding.root.context.getColor(R.color.white))
                binding.deliveryFeesTv.setTextColor(binding.root.context.getColor(R.color.white))

                binding.deliveryFeesTv.visibility = View.VISIBLE
                binding.textViewFeesInEgp.visibility = View.VISIBLE

                binding.tvFullBooked.visibility = View.GONE

            } else {
                //not selected item but available to be choosen
                binding.rootCard.strokeColor = binding.root.context.getColor(R.color.border_of_time_slots_item)
                binding.rootCard.setCardBackgroundColor(binding.root.context.getColor(R.color.bacground_checout_carts))
                binding.constraintBottomCheckoutFragment.setBackgroundResource(0)

                binding.textView34.text = "${item.from} - ${item.to}"
                binding.textViewFeesInEgp.text = context.getString(R.string.egps, item.fees)
                binding.textView34.setTextColor(binding.root.context.getColor(R.color.black))
                binding.textViewFeesInEgp.setTextColor(binding.root.context.getColor(R.color.black))
                binding.deliveryFeesTv.setTextColor(binding.root.context.getColor(R.color.black))

                binding.deliveryFeesTv.visibility = View.VISIBLE
                binding.textViewFeesInEgp.visibility = View.VISIBLE
                binding.tvFullBooked.visibility = View.GONE
            }
            binding.root.setOnClickListener { listener.onClickTimeAdapterItem(position, item) }
        }
    }

    override fun getItemCount(): Int = data?.data?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        context = parent.context

        val binding = ItemBottomSlotsBottomSheetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        if (!data?.data.isNullOrEmpty()) {
            holder.setData(data!!.data[position], position)
        }
    }

    fun setData(data: GetSlotsResponse) {
        this.data = data
        notifyDataSetChanged()
    }

    fun setSelectedItem(position: Int) {
        mSelectedItem = position
        notifyDataSetChanged()
    }

    fun getSelectedItem(): DataGetSlotsResponse? {
        if (data == null)
            return null
        else
            return data?.data?.get(mSelectedItem)
    }
}