package store.msolapps.flamingo.presentation.home.offers.adapters

import android.content.Context
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import store.msolapps.domain.models.response.CategoriesResponseModel
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.ItemMainCatBinding

class ChildAdapter(
    private val listener: ChildAdapterActions,
    private var mSelectedItem: Int
) : RecyclerView.Adapter<ChildAdapter.ChildAdapterViewHolder>() {
    var mRecyclerView: RecyclerView? = null

    private lateinit var context: Context
    private var children: MutableList<CategoriesResponseModel.Data.ChildrenCategoriesResponseModel> =
        ArrayList()
    private var checkedPosition = 0

    class ChildAdapterViewHolder(itemView: View, var itemRowBinding: ItemMainCatBinding) :
        RecyclerView.ViewHolder(itemView) {
        val parent: TextView = itemView.findViewById(R.id.subCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildAdapterViewHolder {
        context = parent.context
        val itemBinding: ItemMainCatBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.item_main_cat,
            parent,
            false
        )
        return ChildAdapterViewHolder(itemBinding.root, itemBinding)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onBindViewHolder(holder: ChildAdapterViewHolder, position: Int) {
        /*holder.itemRowBinding.subCategory.text = children[position].name
        holder.itemRowBinding.subCategory.setBackgroundResource(R.drawable.filter_white)
        holder.itemRowBinding.subCategory.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.blue
            )
        )

        if (mSelectedItem == position) {
            //we select it
            holder.itemRowBinding.subCategoryWave.visibility = View.VISIBLE
            holder.itemRowBinding.subCategory.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.blue
                )
            )

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
        } else {
            holder.itemRowBinding.subCategoryWave.visibility = View.GONE
        }
        holder.itemRowBinding.subCategory.setOnClickListener {
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
    }

    override fun getItemCount(): Int {
        return children.size
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
        listener.onChangeChildSelectedItem()
        notifyDataSetChanged()
    }
    fun getChildrenSize(): Int {
        return children.size
    }
}

interface ChildAdapterActions {
    fun onClickChildItemOnSubCat(categoryId: String, position: Int)
    fun onChangeChildSelectedItem()
}