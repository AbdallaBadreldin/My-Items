package store.msolapps.flamingo.presentation.home.offers.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import store.msolapps.domain.models.response.CategoriesResponseModel
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.ItemMainCatBinding

class ParentAdapter(private val listener: ParentAdapterActions) :
    RecyclerView.Adapter<ParentAdapter.ViewHolder>() {

    private var parents: MutableList<CategoriesResponseModel.Data> =
        ArrayList()
    var mRecyclerView: RecyclerView? = null
    var mSelectedItem = 0
    var multiCats: CategoriesResponseModel? = null
    private lateinit var context: Context

    class ViewHolder(itemView: View, var itemRowBinding: ItemMainCatBinding) :
        RecyclerView.ViewHolder(itemView) {
        val parent: TextView = itemView.findViewById(R.id.subCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val itemBinding: ItemMainCatBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_main_cat,
            parent,
            false
        )
        return ViewHolder(itemBinding.root, itemBinding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (multiCats != null) {
            val currentItem = multiCats!!.data[position]
            holder.itemRowBinding.subCategory.text = currentItem.name
            holder.itemRowBinding.subCategory.setBackgroundResource(R.drawable.filter_white)
            holder.itemRowBinding.subCategory.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.black
                )
            )

            holder.itemRowBinding.subCategory.setOnClickListener {
                if (position != mSelectedItem) {
                    mSelectedItem = position
                    listener.onClickMainCategoryAdapter(multiCats!!, position = position)
                }
            }
            if (position == mSelectedItem) {
                holder.itemRowBinding.subCategory.setBackgroundResource(R.drawable.filter_blue)
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


    override fun getItemCount(): Int {
        return multiCats?.data?.size ?: 0
    }

    fun setData(multiCats: CategoriesResponseModel) {
        this.multiCats = multiCats
        notifyDataSetChanged()
    }

    fun setSelectedItem(selectedItem: Int) {
        this.mSelectedItem = selectedItem
        listener.onChangeParentAdapterSelectedItem()
        notifyDataSetChanged()
    }
}

interface ParentAdapterActions {
    fun onClickMainCategoryAdapter(categoriesData: CategoriesResponseModel, position: Int)
    fun onChangeParentAdapterSelectedItem()
}