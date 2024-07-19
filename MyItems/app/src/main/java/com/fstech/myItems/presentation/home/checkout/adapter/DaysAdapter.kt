package store.msolapps.flamingo.presentation.home.checkout.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import store.msolapps.domain.models.DayModel
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.ItemTopSlotsBottomSheetBinding
import store.msolapps.flamingo.presentation.home.checkout.actions.DaysAdapterActions

class DaysAdapter(private val listener: DaysAdapterActions) :
    RecyclerView.Adapter<DaysAdapter.ViewPagerViewHolder>() {
    private var data: MutableList<DayModel> = mutableListOf()
    var mSelectedItem = 0

    inner class ViewPagerViewHolder(val binding: ItemTopSlotsBottomSheetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(item: DayModel, position: Int) {
            binding.month.text = "${item.day} ${item.month}"
            binding.day.text = "${item.dayString}"
            if (position == mSelectedItem) {
                //style selected Item
                binding.month.setTextColor(binding.root.context.getColor(R.color.white))
                binding.day.setTextColor(binding.root.context.getColor(R.color.white))
                binding.root.setCardBackgroundColor(
                    getColor(
                        binding.root.context,
                        R.color.white_blue
                    )
                )
                listener.updateBottomSlotRecycler(item.date)
            } else {
                //style unselected item
                binding.month.setTextColor(binding.root.context.getColor(R.color.black))
                binding.day.setTextColor(binding.root.context.getColor(R.color.whitish))
                binding.root.setCardBackgroundColor(getColor(binding.root.context, R.color.bacground_checout_carts))
            }
            binding.root.setOnClickListener {
                listener.onClickDaysAdapter(item, position)
            }
        }

    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {

        val binding = ItemTopSlotsBottomSheetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.setData(data[position], position)
    }

    fun setData(data: MutableList<DayModel>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun getSelectedItem(): DayModel? {
        if (data == null)
            return null
        else
            if (mSelectedItem != -1)
                return data[mSelectedItem]
            else return data[0]
    }

    fun setSelectedItem(position: Int) {
        mSelectedItem = position
        notifyDataSetChanged()
    }
}