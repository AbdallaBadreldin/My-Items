package store.msolapps.flamingo.presentation.auth.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import store.msolapps.flamingo.R
import store.msolapps.flamingo.presentation.auth.FragmentAddAddressNavigator
import store.msolapps.flamingo.presentation.auth.StringsAddress

class SuggestAddAddressAdapter(
    private val listener: FragmentAddAddressNavigator,
    private val arr: MutableList<StringsAddress>
) : RecyclerView.Adapter<SuggestAddAddressAdapter.SuggestAddAddressViewHolder>() {

    private lateinit var context: Context

    class SuggestAddAddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val suggestTv: TextView = itemView.findViewById(R.id.txtTopAddress)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SuggestAddAddressViewHolder {
        context = parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_top_address, parent, false)
        return SuggestAddAddressViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: SuggestAddAddressViewHolder,
        position: Int
    ) {

        holder.suggestTv.text = arr[position].name

        Log.d("SuggestAdapter", "onBindViewHolder $position: ${arr[position].checked}")

        if (arr[position].checked!!){
            holder.suggestTv.background = ContextCompat.getDrawable(context, R.drawable.radius_background_blue)
            holder.suggestTv.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.suggestTv.background = ContextCompat.getDrawable(context, R.drawable.radius_background_grey)
            holder.suggestTv.setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        holder.suggestTv.setOnClickListener {
            listener.onClickSuggestLabelAddress(position, arr[position].name)
            for (item in arr)
                item.checked = false
            arr[position].checked = true
            notifyDataSetChanged()
        }
    }
    override fun getItemCount(): Int {
        return arr.size
    }
}
