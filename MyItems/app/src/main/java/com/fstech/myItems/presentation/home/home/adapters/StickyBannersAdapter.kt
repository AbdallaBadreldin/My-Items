package store.msolapps.flamingo.presentation.home.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import store.msolapps.domain.models.response.DataHomeBannersResponse
import store.msolapps.flamingo.databinding.ImageSliderItemBinding
import store.msolapps.flamingo.presentation.home.home.actions.StickyBannersActions

class StickyBannersAdapter(private val listener: StickyBannersActions) :
    RecyclerView.Adapter<StickyBannersAdapter.ViewPagerViewHolder>() {
    private var data: MutableList<DataHomeBannersResponse> = mutableListOf()

    inner class ViewPagerViewHolder(val binding: ImageSliderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(item: DataHomeBannersResponse,position: Int) {
            Glide.with(binding.root.context)
                .load(item.image)
                .error(com.google.android.material.R.drawable.mtrl_ic_error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.ivImage)

            binding.root.setOnClickListener {
                listener.onClickStickyBanner(position = position)
            }
        }

    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {

        val binding = ImageSliderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.setData(data[position],position)
    }

    fun setStickyBannerData(data: MutableList<DataHomeBannersResponse>) {
        this.data = data
        notifyDataSetChanged()
    }
}