package store.msolapps.flamingo.presentation.home.offers.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import store.msolapps.domain.models.response.CategoriesResponseModel
import store.msolapps.domain.models.response.OfferName
import store.msolapps.domain.models.response.OffersTitle
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.ItemOfferBinding
import store.msolapps.flamingo.presentation.home.offers.ArrNamesOffers

class OffersAdapter(
    private val listener: OffersAdapterActions,
) :
    RecyclerView.Adapter<OffersAdapter.ViewHolder>() {

    private var offers: MutableList<OfferName> =
        ArrayList()
    var mRecyclerView: RecyclerView? = null
    var mSelectedItem = 0
    private lateinit var context: Context

    class ViewHolder(itemView: View, var itemRowBinding: ItemOfferBinding) :
        RecyclerView.ViewHolder(itemView) {
        val parent: TextView = itemView.findViewById(R.id.subCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val itemBinding: ItemOfferBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_offer,
            parent,
            false
        )
        return ViewHolder(itemBinding.root, itemBinding)
    }

    override fun getItemCount(): Int {
        return offers.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (offers != null) {
            val apiText = offers[position].offer_name
            val formattedText = apiText.replace(" ", "\n")


            holder.itemRowBinding.subCategory.text = formattedText
            if (context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                holder.itemRowBinding.arrowIc.scaleX = -1f // Flip the image horizontally for RTL
            } else {
                holder.itemRowBinding.arrowIc.scaleX = 1f // Default orientation for LTR
            }
            holder.itemRowBinding.linearCat.setBackgroundResource(R.drawable.filter_white)
            holder.itemRowBinding.subCategory.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.black
                )
            )

            holder.itemRowBinding.subCategory.setOnClickListener {
                if (position != mSelectedItem) {
                    mSelectedItem = position
                    listener.onClickOfferAdapter(offers, position = position)
                }
            }
            if (position == mSelectedItem) {
                holder.itemRowBinding.linearCat.setBackgroundResource(R.drawable.filter_blue)
                holder.itemRowBinding.subCategory.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }


    fun setData(
        offers: MutableList<OfferName>,
        selectedItem: Int
    ) {
        if (offers.isNullOrEmpty()) {
            mRecyclerView?.visibility = View.GONE
        } else {
            mRecyclerView?.visibility = View.VISIBLE
            mSelectedItem = selectedItem
            this.offers = offers
        }
        notifyDataSetChanged()
    }

    fun getSelectedItemId(): Int {
        return mSelectedItem
    }

    fun setSelectedItem(selectedItem: Int) {
        mSelectedItem = selectedItem
        notifyDataSetChanged()
    }
}

interface OffersAdapterActions {
    fun onClickOfferAdapter(
        offersData: MutableList<OfferName>
        , position: Int)
}