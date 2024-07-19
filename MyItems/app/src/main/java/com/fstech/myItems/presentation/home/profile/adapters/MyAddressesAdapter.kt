package store.msolapps.flamingo.presentation.home.profile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import store.msolapps.domain.models.response.AddressResponseModel
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.NewAddressItemBinding

class MyAddressesAdapter(private val listener: MyAddressNavigator) :
    RecyclerView.Adapter<MyAddressesAdapter.MyAddressesViewHolder>() {

    private var data: MutableList<AddressResponseModel.Data> = ArrayList()
    private lateinit var context: Context
    private var pos = 0

    class MyAddressesViewHolder(itemView: View, val itemBinding: NewAddressItemBinding) :
        RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAddressesViewHolder {
        context = parent.context
        val itemBinding: NewAddressItemBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.new_address_item,
                parent,
                false
            )
        return MyAddressesViewHolder(itemBinding.root, itemBinding)
    }

    override fun onBindViewHolder(holder: MyAddressesViewHolder, position: Int) {
        if (data[position].default == 1) {
            holder.itemBinding.parentAddress.setBackgroundResource(R.drawable.rounded_view)
            holder.itemBinding.titleAddress.setTextColor(context.getColor(R.color.white))
            holder.itemBinding.addressAddress.setTextColor(context.getColor(R.color.white))
            holder.itemBinding.userNameAddress.setTextColor(context.getColor(R.color.white))
            holder.itemBinding.numberAddress.setTextColor(context.getColor(R.color.white))
        } else {
            holder.itemBinding.parentAddress.setBackgroundResource(R.color.inactive_address)
            holder.itemBinding.titleAddress.setTextColor(context.getColor(R.color.black))
            holder.itemBinding.addressAddress.setTextColor(context.getColor(R.color.black))
            holder.itemBinding.userNameAddress.setTextColor(context.getColor(R.color.black))
            holder.itemBinding.numberAddress.setTextColor(context.getColor(R.color.black))
        }

        holder.itemBinding.titleAddress.text = data[position].name
        holder.itemBinding.addressAddress.text = data[position].address
        holder.itemBinding.userNameAddress.text = data[position].receiver_name
        holder.itemBinding.numberAddress.text = data[position].receiver_phone

        holder.itemBinding.editIc.setOnClickListener {
            listener.onClickToEdit(data[position])
        }

        holder.itemBinding.removeIc.setOnClickListener {
            listener.onClickToRemove(data[position].id)
        }

        holder.itemBinding.root.setOnClickListener {
            if (data[position].default != 1) {
                listener.onClickToSelect(data[position].id)
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(data: MutableList<AddressResponseModel.Data>) {
        this.data = data
    }
}

interface MyAddressNavigator {
    fun onClickToEdit(address: AddressResponseModel.Data)
    fun onClickToRemove(id: String)
    fun onClickToSelect(id: String)
//    fun onUpdateSuccess(data: MutableList<AddressResponseModel.Data>, position: Int, pos: Int)
}
