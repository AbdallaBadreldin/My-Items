package store.msolapps.flamingo.presentation.home.offers.adapters

import android.annotation.SuppressLint
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

class NewAllSubCatAdapter(private val listener: CategoriesActions) :
    RecyclerView.Adapter<NewAllSubCatAdapter.NewAllSubCatViewHolder>() {

    private lateinit var context: Context
    private var children: MutableList<CategoriesResponseModel.Data.ChildrenCategoriesResponseModel.Children1CategoriesResponseModel> =
        ArrayList()
    private var selectDefault = true
    var mainSelectedItemPosition=0
    class NewAllSubCatViewHolder(itemView: View, var itemRowBinding: ItemMainCatBinding) :
        RecyclerView.ViewHolder(itemView){
        val parent: TextView = itemView.findViewById(R.id.subCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewAllSubCatViewHolder {
        context = parent.context
        val itemBinding: ItemMainCatBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.item_main_cat,
            parent,
            false
        )
        return NewAllSubCatViewHolder(itemBinding.root, itemBinding)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: NewAllSubCatViewHolder, position: Int) {

        holder.itemRowBinding.subCategory.text = children[position].name
        holder.itemRowBinding.subCategory.setBackgroundResource(R.drawable.filter_white)
        holder.itemRowBinding.subCategory.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.blue
            )
        )

        if (children[position].color) {
            holder.itemRowBinding.subCategory.setBackgroundResource(R.drawable.filter_blue)
            holder.itemRowBinding.subCategory.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        }
        holder.itemRowBinding.subCategory.setOnClickListener {
            listener.onClickSubCat(position, children[position].id, children[position].name)
            for (i in 0 until children.size) {
                children[i].color = false
            }
            children[position].color = true
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return children.size
    }

    fun setData(children: MutableList<CategoriesResponseModel.Data.ChildrenCategoriesResponseModel.Children1CategoriesResponseModel>) {
        this.children = children
    }
    fun getSelectedItemId(): Int {
        return mainSelectedItemPosition
    }

}

interface CategoriesActions {
    fun onClickMainMultiCat(position: Int, mainId: MutableList<Long>)
    fun onClickMainCatOffer(position: Int, mainId: Long)
    fun onClickMainCat(position: Int, mainId: Long)
    fun onClickSubCat(position: Int, subId: Long, name: String)
    fun updateChildAdapter(
        position: Int
    )
}