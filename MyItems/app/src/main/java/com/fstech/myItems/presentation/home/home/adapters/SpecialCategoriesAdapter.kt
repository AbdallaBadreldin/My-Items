package store.msolapps.flamingo.presentation.home.home.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import store.msolapps.domain.models.response.DataSpecialCategoriesResponse
import store.msolapps.flamingo.databinding.ItemOutterSpecialDealsBinding
import store.msolapps.flamingo.presentation.home.home.actions.StickyBannersActions

class SpecialCategoriesAdapter(private val listener: StickyBannersActions) :
    RecyclerView.Adapter<SpecialCategoriesAdapter.ViewPagerViewHolder>() {
    private var data: MutableList<DataSpecialCategoriesResponse> = mutableListOf()

    inner class ViewPagerViewHolder(val binding: ItemOutterSpecialDealsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(imageUrl: DataSpecialCategoriesResponse,position: Int) {
            Glide.with(binding.root.context)
                .load(imageUrl.image)
                .error(com.google.android.material.R.drawable.mtrl_ic_error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imageViewSpecialCategory)
//            binding.ivImage.setImageResource()
            binding.root.setOnClickListener {
                listener.onClickStickyBanner(position = position)
            }
        }

    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {

        val binding = ItemOutterSpecialDealsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.setData(data[position],position)
    }

    fun setSpecialCategoryData(data: MutableList<DataSpecialCategoriesResponse>) {
        this.data = data
        notifyDataSetChanged()
    }
}