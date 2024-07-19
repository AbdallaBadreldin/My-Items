package store.msolapps.flamingo.presentation.home.offers.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import store.msolapps.domain.models.request.ProductsData
import store.msolapps.domain.models.response.GetOffersData
import store.msolapps.domain.models.response.OffersModel
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.OffersParentBinding
import store.msolapps.flamingo.presentation.home.category.adapter.ProductsActions
import store.msolapps.flamingo.presentation.home.category.adapter.ProductsAdapter
import store.msolapps.flamingo.presentation.home.home.CardViewModel

class AllOffersDataAdapter(
    private val listener: ProductsActions,
    private val showAllListener: NewOffersNav,
    private val cardViewModel: CardViewModel,
    private val offersData: MutableSet<OffersModel>,
) : RecyclerView.Adapter<AllOffersDataAdapter.AllOffersDataViewHolder>() {

    private lateinit var context: Context
    private lateinit var productsAdapter: ProductOffersAdapter


    class AllOffersDataViewHolder(itemView: View, val itemBinding: OffersParentBinding) :
        RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOffersDataViewHolder {
        context = parent.context
        val itemBinding: OffersParentBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.offers_parent,
                parent,
                false
            )
        return AllOffersDataViewHolder(
            itemBinding.root,
            itemBinding
        )
    }

    override fun getItemCount(): Int {
        return offersData.size
    }

    override fun onBindViewHolder(holder: AllOffersDataViewHolder, position: Int) {
        val productList = offersData.toList()

        holder.itemBinding.tx2.text = productList[position].name

        if (productList[position].data.isNotEmpty()) {
            holder.itemBinding.tx2.visibility = View.VISIBLE
            holder.itemBinding.seeAllOffersAll.visibility = View.VISIBLE
            setUpProductsAdapter(holder.itemBinding.offersItems, productList[position].data)
        } else {
            holder.itemBinding.tx2.visibility = View.GONE
            holder.itemBinding.seeAllOffersAll.visibility = View.GONE
        }

        holder.itemBinding.seeAllOffersAll.setOnClickListener {
//            listener.onClickOnSeeAll(offersData[position].offer_id, offersData[position].name)
            showAllListener.onClickOnSeeAll(
                productList[position].offer_id,
                productList[position].name,
                productList[position].offer_id
            )
        }

    }

    private fun setUpProductsAdapter(
        offersProductsRecyclerView: RecyclerView,
        data: MutableList<ProductsData>
    ) {
        productsAdapter = ProductOffersAdapter(cardViewModel, listener)
        productsAdapter.updateData(data)
        offersProductsRecyclerView.adapter = productsAdapter
    }
}

interface NewOffersNav {
    fun onClickOnSeeAll(offerId: String, name: String, offerType: String)
}
