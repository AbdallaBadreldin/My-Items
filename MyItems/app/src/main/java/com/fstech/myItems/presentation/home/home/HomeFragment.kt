package store.msolapps.flamingo.presentation.home.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.domain.models.request.TokenRequest
import store.msolapps.domain.models.response.ProductsSpecialCategoriesResponse
import store.msolapps.domain.models.response.SpecialCategoriesResponse
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.MobileNavigationDirections
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentHomeBinding
import store.msolapps.flamingo.presentation.home.home.actions.SpecialCategoriesInsiderActions
import store.msolapps.flamingo.presentation.home.home.actions.SpecialCategoryActions
import store.msolapps.flamingo.presentation.home.home.actions.StickyBannersActions
import store.msolapps.flamingo.presentation.home.home.adapters.SpecialCategoriesInsiderAdapter
import store.msolapps.flamingo.presentation.home.home.adapters.StickyBannersAdapter
import java.util.Calendar

@AndroidEntryPoint
class HomeFragment : BaseFragment(), StickyBannersActions, SpecialCategoryActions,
    SpecialCategoriesInsiderActions {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val addressViewModel: AddressBottomSheetViewModel by activityViewModels()
    private val cardViewModel: CardViewModel by viewModels()
    private lateinit var stickyBannersAdapter: StickyBannersAdapter
    private lateinit var adapter: SpecialCategoriesInsiderAdapter

    //preparing shimmer for the app
    private lateinit var shimmer: Shimmer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        Log.e("onCreateView: ", viewModel.getToken())
        sendToken()
        setupAdapters()
        setupShimmer()
        prepareUi()
        setupListeners()
        setupObservers()
        return binding.root
    }

    private fun setupAdapters() {
        stickyBannersAdapter = StickyBannersAdapter(this)
        binding.viewPager2.adapter = stickyBannersAdapter
    }

    private fun prepareUi() {
        if (resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            binding.cartIcon.scaleX = -1f
        } else {
            binding.cartIcon.scaleX = 1f
        }
        sayHiToUser()
        putTheNameOfTheUser()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.blue)
        }
    }

    private fun putTheNameOfTheUser() {
        if (viewModel.isUserLoggedIn()) {
            binding.tvUserName.text = viewModel.getUserName()
        }
    }

    private fun sayHiToUser() {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        binding.TextViewHiUser.text = when (hourOfDay) {
            in 5..11 -> getString(R.string.good_morning)
            in 12..16 -> getString(R.string.good_afternoon)
            else -> getString(R.string.good_evening)
        }
    }

    private fun setupListeners() {
        binding.searchView.setOnClickListener {
            findNavController().navigate(MobileNavigationDirections.navigateToSearchFragmentFromHome())
        }
        binding.cartIcon.setOnClickListener {
            if (viewModel.isUserLoggedIn())
                findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToNavigationCart())
            else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_login_first),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.textViewDefaultAddress.setOnClickListener {
            val addressBottomSheet = AddressBottomSheet()
            addressBottomSheet.show(parentFragmentManager, addressBottomSheet.tag)
        }
        binding.imageViewCategory1.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToShowSingleCategory(
                    getIdFromDeepLink(viewModel.getCategoriesHomeData.value!!.data[0].category_id)
                )
            )
        }
        binding.imageViewCategory2.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToShowSingleCategory(
                    getIdFromDeepLink(viewModel.getCategoriesHomeData.value!!.data[1].category_id)
                )
            )
        }
        binding.imageViewCategory3.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToShowSingleCategory(
                    getIdFromDeepLink(viewModel.getCategoriesHomeData.value!!.data[3].category_id)
                )
            )
        }
        binding.imageViewCategory4.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToCategoryFromHomeFragment()
            )
        }
    }

    private fun loadHomePageData() {
        if (!viewModel.connection.isOnline()) {
            // no InterNet Connection
            //return
        }
        viewModel.getCategoriesHome()
        viewModel.getCategories()
        viewModel.getSpecialCategories()
        viewModel.getHomeBanners()
        viewModel.getStickyBanners()
        cardViewModel.getCartData()
        if (viewModel.isUserLoggedIn())
            viewModel.getDefaultAddress()
    }

    private fun setupObservers() {
        observeData()
        observeErrors()
        observeLoading()
    }

    private fun observeLoading() {
        viewModel.getCategoriesLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.getSpecialCategoryLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.getHomeBannersLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.getStickyBannersLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.getDefaultAddressLoad.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
    }

    private fun observeErrors() {
        viewModel.getCategoriesError.observe(viewLifecycleOwner) {
//            Log.d("Categories", it.toString())
        }
        viewModel.getSpecialCategoryError.observe(viewLifecycleOwner) {
//            Log.d("SpecialDeals", it.toString())
        }
        viewModel.getHomeBannersError.observe(viewLifecycleOwner) {
//            Log.d("HomeBanners", it.toString())
        }
        viewModel.getStickyBannersError.observe(viewLifecycleOwner) {
//            Log.d("stickyBanners", it.toString())
        }
        viewModel.getDefaultAddressError.observe(viewLifecycleOwner) {
            viewModel.setIsDefaultAddressExist(false)
            try {
            } catch (e: IllegalArgumentException) {
                viewModel.setIsDefaultAddressExist(false)
                //please fix this
            }
        }

    }

    private fun observeData() {
        viewModel.getSpecialCategoryData.observe(viewLifecycleOwner) {
            if (it != null) {
                setUpSpecialCategoryView(it)
            }
        }
        viewModel.getHomeBannersData.observe(viewLifecycleOwner) {
            if (it != null) {
                stickyBannersAdapter.setStickyBannerData(it.data)
                addIndicatorDots(it.data.size)

            }
        }
        viewModel.getStickyBannersData.observe(viewLifecycleOwner) {
            if (it != null) {
                loadCategoryImage(it.data.home_banners[0].image, binding.imageViewStickyBanner1)
                loadCategoryImage(it.data.home_banners[1].image, binding.imageViewStickyBanner2)

                val link1 = it.data.home_banners[0].deep_linking_type
                val link2 = it.data.home_banners[1].deep_linking_type

                binding.imageViewStickyBanner1.setOnClickListener { _ ->
                    openDeepLink(link1, it.data.home_banners[0].name_en)
                }
                binding.imageViewStickyBanner2.setOnClickListener { _ ->
                    openDeepLink(
                        link2,
                        it.data.home_banners[1].name_en,
                        it.data.home_banners[1].id.toString()
                    )
                }
            }
        }
        viewModel.getDefaultAddressData.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.textViewDefaultAddress.text = it.data?.name
                viewModel.setIsDefaultAddressExist(true)
            }
        }
        viewModel.getCategoriesHomeData.observe(viewLifecycleOwner) {
            if (it != null) {
                loadCategoryImage(it.data[0].image, binding.imageViewCategory1)
                loadCategoryImage(it.data[1].image, binding.imageViewCategory2)
                loadCategoryImage(it.data[2].image, binding.imageViewCategory3)

                if (viewModel.getLang() == "en") {
                    binding.imageViewCategory4.setImageResource(R.drawable.home_view_all_category)

                } else {
                    binding.imageViewCategory4.setImageResource(R.drawable.home_view_all_category_arabic)
                }
            }
        }
        cardViewModel.cart.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.cart_count == 0) {
                    binding.cartBadge.visibility = View.GONE
                } else {
                    binding.cartBadge.visibility = View.VISIBLE
                    binding.cartBadge.text = it.cart_count.toString()
                }
            }
        }
        addressViewModel.updateAddresses.observe(viewLifecycleOwner) {
            //to update the default address when user change it from AddAddressViewModel
            viewModel.getDefaultAddress()
        }
    }

    private fun addIndicatorDots(count: Int) {
        val indicatorContainer = binding.tabDots
        indicatorContainer.removeAllViews()

        for (i in 0 until count) {
            val dot = View(requireContext())
            dot.background = ContextCompat.getDrawable(requireContext(), R.drawable.tab_selector)
            val params = LinearLayout.LayoutParams(16, 16)
            params.setMargins(8, 8, 8, 0)
            dot.layoutParams = params
            indicatorContainer.addView(dot)
        }

        val viewPager = binding.viewPager2
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicatorDots(position)
            }
        })
    }

    private fun updateIndicatorDots(position: Int) {
        val indicatorContainer = binding.tabDots

        for (i in 0 until indicatorContainer.childCount) {
            val dot = indicatorContainer.getChildAt(i)
            dot.isSelected = i == position
        }
    }

    private fun setUpSpecialCategoryView(it: SpecialCategoriesResponse?) {
        if (it == null) {
            return
        } else if (it.data.isNullOrEmpty()) {
            return
        }
        if (it.data.get(0).products.isNullOrEmpty()) {
            return
        }
        for (item in it.data) {
            adapter = SpecialCategoriesInsiderAdapter(this, cardViewModel)
            adapter.setSpecialCategoryInsiderData(item.products)
            val view =
                LayoutInflater.from(context).inflate(R.layout.item_outter_special_deals, null)
            val imageView =
                view.findViewById<ImageView>(R.id.imageView_specialCategory)
            val button =
                view.findViewById<Button>(R.id.show_more)
            val recyclerView =
                view.findViewById<RecyclerView>(R.id.recyclerview_specialCategory)
            val specialCategoryNameTextView =
                view.findViewById<TextView>(R.id.textView_specialCategoryName)
            specialCategoryNameTextView.text = item.name
            val llm = LinearLayoutManager(context)
            Glide.with(imageView.context).load(item.image)
                .placeholder(requireContext().getDrawable(R.raw.loading))
                .into(imageView)
            llm.orientation = LinearLayoutManager.HORIZONTAL
            recyclerView.layoutManager = llm
            recyclerView.adapter = adapter
            binding.recyclerOuterSpecialDeals.addView(view)
            button.setOnClickListener { openDeepLink(item.deep_linking_type.toString(), item.name) }
            view.setOnClickListener {
                openDeepLink(
                    item.deep_linking_type.toString(),
                    item.name
                )
            }
        }
    }

    private fun loadCategoryImage(imageUrl: String?, imageView: ImageView) {
        if (imageUrl.isNullOrEmpty())
            imageView.visibility = View.GONE
        // This is the placeholder for the imageView
        val shimmerDrawable = ShimmerDrawable().apply {
            setShimmer(shimmer)
        }
        Glide.with(imageView.context).load(imageUrl)
            .placeholder(shimmerDrawable)
            .placeholder(R.drawable.flamingo_placeholder)
            .error(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.user__2_
                )
            )
            .into(imageView)
    }

    private fun setupShimmer() {
        shimmer = Shimmer.ColorHighlightBuilder()
            // The attributes for a ShimmerDrawable is set by this builder
            .setDuration(1800) // how long the shimmering animation takes to do one full sweep
            .setBaseAlpha(0.7f) //the alpha of the underlying children
            .setHighlightAlpha(0.6f) // the shimmer alpha amount
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .setHeightRatio(binding.imageViewCategory1.height.toFloat())
            .setWidthRatio(binding.imageViewCategory2.width.toFloat())
            .build()
    }

    private fun openDeepLink(link: String, name: String?, offerType: String? = null) {
        when {
            link.contains("show_category") -> {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavigationHomeToShowCategory(
                        getCategoriesId(link).toLong(),
                        name!!,
                        1
                    )
                )
            }

            link.contains("show_product") -> {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavigationHomeToShowProductFromHome(
                        id = getProductId(link)
                    )
                )
            }

            link.contains("multi_category") -> {
                val longArray = getIdsFromMultiCats(link)
                val bundle = Bundle()
                bundle.putLongArray("numberList", longArray.toLongArray())
                bundle.putString("name", name!!)
                findNavController().navigate(
                    R.id.action_navigation_home_to_showMultiCategory,
                    bundle
                )

            }

            link.contains("multi_product") -> {
                val longArray = getIdsFromMultiCats(link)
                val bundle = Bundle()
                bundle.putLongArray("numberList", longArray.toLongArray())
                bundle.putString("name", name!!)
                findNavController().navigate(
                    R.id.action_navigation_home_to_showMultiProductsCategory,
                    bundle
                )
            }

            link.contains("show_offers") -> {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavigationHomeToShowOffersFragment(
                        type = getCategoriesId(link),
                        name = name!!,
                        offerType = offerType ?: "1"
                    )
                )
            }

            else -> {
                if (link.isNotEmpty()) {
                    openDeepLink(link, name)
                }
            }
        }
    }


    override fun onClickStickyBanner(position: Int) {
        val item = viewModel.getHomeBannersData.value?.data!!
        val link = item[position].deep_linking_type
        if (link.isNotEmpty()) {
            openDeepLink(link, item[position].name_en)
        } else {
            if (item[position].link.isNotEmpty()) {
                openDeepLink(item[position].name_en, item[position].link)
            }
        }
    }

    override fun onClickSpecialCategory(position: Int) {
//        TODO("Not yet implemented")
    }

    override fun onInnerSpecialCategoriesClicked(
        position: Int,
        item: ProductsSpecialCategoriesResponse
    ) {
        val items = item
        findNavController().navigate(
            MobileNavigationDirections.actionGoToShowProduct(
                item = items
            )
        )
    }

    private fun sendToken() {
        Firebase.messaging.token
            .addOnSuccessListener { it ->
                val fcmToken = TokenRequest(it)
                viewModel.sendToken(
                    fcmToken
                )
            }
    }


    private fun getCategoriesId(app_link: String): String {
        val linkArray = app_link.split("/")
        return linkArray[linkArray.size - 1]
    }

    private fun getProductId(app_link: String, flagQuantity: Boolean? = false): String {
        val linkArray = app_link.split("/")
        return if (flagQuantity!!) {
            linkArray[linkArray.size - 2]
        } else {
            linkArray[linkArray.size - 1]
        }
    }

    private fun getIdFromDeepLink(input: String): Long {
        var longList: Long = 0
        val startIndex = input.lastIndexOf("/") + 1 // Finding the start index of long values
        val endIndex = input.length // Finding the end index of long values
        val longValuesString =
            input.substring(startIndex, endIndex) // Extracting the long values substring
        val longValuesArray = longValuesString.split(",") // Splitting the long values by comma
        for (value in longValuesArray) {
            if (value.isNotEmpty()) {
                longList = value.toLong() // Parsing value to Long and adding to the MutableList
            }
        }
        return longList
    }

    private fun getIdsFromMultiCats(input: String): MutableList<Long> {
        val numberList = mutableListOf<Long>()
        val startIndex = input.lastIndexOf("/") + 1
        val endIndex = input.length
        val numbersString = input.substring(startIndex, endIndex)
        val numberStrings = numbersString.split(",")

        for (numberString in numberStrings) {
            val number = numberString.toLongOrNull()
            if (number != null) {
                numberList.add(number)
            }
        }
        return numberList
    }

    override fun onResume() {
        super.onResume()
        binding.recyclerOuterSpecialDeals.clearDisappearingChildren()
        binding.recyclerOuterSpecialDeals.removeAllViews()
        loadHomePageData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        clearObserversFromData()
    }

    override fun onStop() {
        super.onStop()
        clearObserversFromData()
    }

    private fun clearObserversFromData() {
        viewModel.clearObservers()
        cardViewModel.clearObservers()
        addressViewModel.clearObservers()
    }
}