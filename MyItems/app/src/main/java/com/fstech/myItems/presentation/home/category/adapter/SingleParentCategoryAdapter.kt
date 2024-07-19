package store.msolapps.flamingo.presentation.home.category.adapter

import android.content.Context
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import store.msolapps.domain.models.response.CategoriesResponseModel
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.ItemMainCatBinding

class SingleParentCategoryAdapter(private val listener: SingleParentCategoryAdapterActions) :
    RecyclerView.Adapter<SingleParentCategoryAdapter.ViewHolder>() {

    private var children: MutableList<CategoriesResponseModel.Data.ChildrenCategoriesResponseModel> =
        ArrayList()
    var mRecyclerView: RecyclerView? = null
    var mSelectedItem = 0
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

    override fun getItemCount(): Int {
        return children.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (children != null) {
            val currentItem = children[position]
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
                    listener.onClickMainCategoryAdapter(children, position = position)
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


    fun setData(
        children: MutableList<CategoriesResponseModel.Data.ChildrenCategoriesResponseModel>,
        selectedItem: Int
    ) {
        if (children.isNullOrEmpty()) {
            mRecyclerView?.visibility = View.GONE
        } else {
            mRecyclerView?.visibility = View.VISIBLE
            mSelectedItem = selectedItem
            this.children = children
        }
        notifyDataSetChanged()
    }

    fun getSelectedItemId(): Int {
        return mSelectedItem
    }

    fun setSelectedItem(selectedItem: Int) {
        mSelectedItem = selectedItem
        listener.onChangeParentAdapterSelectedItem()
        notifyDataSetChanged()
    }
}

interface SingleParentCategoryAdapterActions {
    fun onClickMainCategoryAdapter(
        categoriesData: MutableList<CategoriesResponseModel.Data.ChildrenCategoriesResponseModel>
        , position: Int)
    fun onChangeParentAdapterSelectedItem()
}