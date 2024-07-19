package store.msolapps.flamingo.presentation.home.category.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import store.msolapps.domain.models.response.CategoriesResponseModel
import store.msolapps.flamingo.R

class CategoriesAdapter(private val listener: ClickItem) :
    RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {

    private lateinit var context: Context
    private var categories: MutableList<CategoriesResponseModel.Data> = mutableListOf()

    class CategoriesViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        context = parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_categories, parent, false)
        return CategoriesViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        holder.itemView.apply {
            Glide.with(this)
                .load(categories[position].image)
                .error(R.drawable.flamingo_placeholder)
                .placeholder(R.drawable.flamingo_placeholder)
                .into(holder.itemView.findViewById(R.id.item_categories_imageView))
        }
        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    fun setData(categories: MutableList<CategoriesResponseModel.Data>) {
        this.categories = categories
    }
}

interface ClickItem {
    fun onItemClick(position: Int)
}

data class CategoryData(
    var id: Long,
    var name: String,
    var image: String,
    var seasonal_category: Int,
    var seasonal_image: String? = null,
    var icon: Boolean? = false
)