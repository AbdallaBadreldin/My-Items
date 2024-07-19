package store.msolapps.flamingo.presentation.home.category.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import store.msolapps.domain.models.response.CategoriesResponseModel
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.ItemMainCatBinding

class ChildOfChildAdapter(
    private val listener: ChildOfChildAdapterActions,
    private var mSelectedItem: Int
) : RecyclerView.Adapter<ChildOfChildAdapter.ChildOfChildAdapterViewHolder>() {
    var mRecyclerView: RecyclerView? = null

    private lateinit var context: Context
    private var children: MutableList<CategoriesResponseModel.Data
    .ChildrenCategoriesResponseModel.Children1CategoriesResponseModel> =
        ArrayList()
    private var checkedPosition = 0

    class ChildOfChildAdapterViewHolder(itemView: View, var itemRowBinding: ItemMainCatBinding) :
        RecyclerView.ViewHolder(itemView) {
        val parent: TextView = itemView.findViewById(R.id.subCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildOfChildAdapterViewHolder {
        context = parent.context
        val itemBinding: ItemMainCatBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.item_main_cat,
            parent,
            false
        )
        return ChildOfChildAdapterViewHolder(itemBinding.root, itemBinding)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onBindViewHolder(holder: ChildOfChildAdapterViewHolder, position: Int) {
        holder.itemRowBinding.subCategory.text = children[position].name
        holder.itemRowBinding.subCategory.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.black
            )
        )

        holder.itemRowBinding.subCategory.setOnClickListener {
            if (position != mSelectedItem) {
                mSelectedItem = position
                listener.onClickChildItemOnSubCat(children[position].category_id, position = position)
            }
        }
        if (position == mSelectedItem) {
            holder.itemRowBinding.subCategoryWave.visibility = View.VISIBLE
            holder.itemRowBinding.subCategory.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.blue
                )
            )
        } else {
            holder.itemRowBinding.subCategoryWave.visibility = View.GONE
        }
        /*if (mSelectedItem == position) {
            //we select it
            holder.itemRowBinding.subCategory.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.blue
                )
            )
            holder.itemRowBinding.subCategoryWave.visibility = View.VISIBLE

            //scroll to it
            val layoutManager = mRecyclerView?.layoutManager as LinearLayoutManager
            val firstPosition = layoutManager.findFirstVisibleItemPosition()
            val lastPosition = layoutManager.findLastVisibleItemPosition()
            val visibleItems = lastPosition - firstPosition + 1
            if (firstPosition < position) {
                mRecyclerView?.smoothScrollToPosition(position + (visibleItems / 2))
            } else {
                mRecyclerView?.smoothScrollToPosition(position - (visibleItems / 2))
            }
        }*/
        /*holder.itemRowBinding.subCategory.setOnClickListener {
            mSelectedItem = position

            holder.itemRowBinding.subCategory.setBackgroundResource(R.drawable.filter_blue)
            holder.itemRowBinding.subCategory.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
            listener.onClickChildItemOnSubCat(children[position].category_id, position = position)
            notifyDataSetChanged()
            if (checkedPosition != holder.layoutPosition) {
                notifyItemChanged(checkedPosition)
                checkedPosition = holder.layoutPosition
            }
        }*/

    }

    override fun getItemCount(): Int {
        return children.size
    }

    fun setData(
        children: MutableList<CategoriesResponseModel.Data.ChildrenCategoriesResponseModel.Children1CategoriesResponseModel>,
        selectedItem: Int
    ) {
        if (children.isNullOrEmpty()) {
            mRecyclerView?.visibility = View.GONE
        } else {
            mRecyclerView?.visibility = View.VISIBLE
            mSelectedItem = selectedItem
            this.children.addAll(children)
        }
        notifyDataSetChanged()
    }

    fun updateData(
        children: MutableList<CategoriesResponseModel.Data.ChildrenCategoriesResponseModel.Children1CategoriesResponseModel>,
        selectedItem: Int
    ) {
        if (children.isNullOrEmpty()) {
            mRecyclerView?.visibility = View.GONE
        } else {
            mRecyclerView?.visibility = View.VISIBLE
            mSelectedItem = selectedItem
        }
        this.children.clear()
        this.children.addAll(children)
        notifyDataSetChanged()
    }

    fun getSelectedItemId(): Int {
        return mSelectedItem
    }

    fun setSelectedItem(selectedItem: Int) {
        mSelectedItem = selectedItem
        listener.onChangeChildSelectedItem()
        notifyDataSetChanged()
    }
}

interface ChildOfChildAdapterActions {
    fun onClickChildItemOnSubCat(categoryId: String, position: Int)
    fun onChangeChildSelectedItem()
}