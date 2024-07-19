package store.msolapps.flamingo.presentation.home.home.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import store.msolapps.domain.models.response.AddressResponseModel
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.AddressBottomSheetItemBinding

private const val TAG1 = "AddressesBSH_TAG"
class AddressesBottomSheetAdapter(private val listener: AddressBottomSheetNavigator)
    : RecyclerView.Adapter<AddressesBottomSheetAdapter.AddressesBottomSheetViewHolder>() {

    private lateinit var context: Context
    private var data: MutableList<AddressResponseModel.Data> = ArrayList()
    private var pos = 0

    class AddressesBottomSheetViewHolder(itemView: View, val itemRowBinding: AddressBottomSheetItemBinding)
        : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressesBottomSheetViewHolder {
        context = parent.context
        val itemBinding: AddressBottomSheetItemBinding =
            DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.address_bottom_sheet_item, parent, false)
        return AddressesBottomSheetViewHolder(itemBinding.root, itemBinding)
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: AddressesBottomSheetViewHolder, position: Int) {
        Log.d(TAG1, "onBindViewHolder $position: ${data[position].id}")

        holder.itemRowBinding.titleAddressBottomSheet.text = data[position].name
        holder.itemRowBinding.addressAddressBottomSheet.text = data[position].address
        holder.itemRowBinding.userNameAddressBottomSheet.text = data[position].receiver_name
        holder.itemRowBinding.numberAddressBottomSheet.text = data[position].receiver_phone

        if (data[position].default==1){
            pos = position
            holder.itemRowBinding.radioButtonAddressBottomSheet.isChecked = true
            holder.itemRowBinding.parentAddressBottomSheet.background =
                context.getDrawable(R.drawable.border_dark_blue)
            holder.itemRowBinding.titleAddressBottomSheet
                .setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.itemRowBinding.numberAddressBottomSheet
                .setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.itemRowBinding.userNameAddressBottomSheet
                .setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.itemRowBinding.addressAddressBottomSheet
                .setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.itemRowBinding.radioButtonAddressBottomSheet.isChecked = false
            holder.itemRowBinding.parentAddressBottomSheet.background =
                context.getDrawable(R.drawable.border_whitish_shadow)
        }

        holder.itemRowBinding.parentAddressBottomSheet.setOnClickListener {
            data[pos].default = 0
            data[position].default = 1
            notifyDataSetChanged()
            listener.onSelectAddress(data[position])
        }


        holder.itemRowBinding.radioButtonAddressBottomSheet.setOnClickListener {
            data[pos].default = 0
            data[position].default = 1
            notifyDataSetChanged()
            listener.onSelectAddress(data[position])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(data: MutableList<AddressResponseModel.Data>){
        this.data = data
    }
}
interface AddressBottomSheetNavigator {
    fun onSelectAddress(address: AddressResponseModel.Data)
}