package store.msolapps.flamingo.presentation.home.search.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.msolapps.oscar.Model.ResponseModel.DataProductsOrdersResponse
import store.msolapps.domain.models.response.CategoryProductsResponseModel
import store.msolapps.domain.models.response.FilterSearchResponse
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.RecentSearchItemBinding

class RecentSearchAdapter(private val listener: ClickSearchFragment)
    : RecyclerView.Adapter<RecentSearchAdapter.RecentSearchViewHolder>() {

    private lateinit var context: Context
    private var data: MutableList<String> = ArrayList()

    class RecentSearchViewHolder(itemView: View, val itemBinding: RecentSearchItemBinding) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentSearchViewHolder {
        context = parent.context
        val itemBinding: RecentSearchItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recent_search_item, parent, false
        )
        return RecentSearchViewHolder(itemBinding.root, itemBinding)

    }

    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
        holder.itemBinding.txtRecentSearch.text = data[position]

        holder.itemBinding.txtRecentSearch.setOnClickListener {
            listener.clickOnRecentSearch(data[position])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(data: MutableList<String>) {
        this.data = data
    }
}
interface ClickSearchFragment {
    fun getSearchInputResult(search: String, data: MutableList<String>, saved: Boolean)
    fun clickOnRecentSearch(txt: String)
    fun clickOnSuggestSearch(txt: String)
    fun onSearchProductsResponse(products: CategoryProductsResponseModel.DataCategoryProductsResponseModel)
    fun onFilterSearchProductsResponse(products: FilterSearchResponse, currentP: Int, totalP: Int)
    fun getProductsForYouResponse(products: MutableList<DataProductsOrdersResponse>)
}
