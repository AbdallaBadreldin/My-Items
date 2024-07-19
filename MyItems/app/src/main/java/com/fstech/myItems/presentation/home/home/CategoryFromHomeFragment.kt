package store.msolapps.flamingo.presentation.home.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.domain.models.response.CategoriesResponseModel
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentCategoryBinding
import store.msolapps.flamingo.presentation.home.category.CategoryViewModel
import store.msolapps.flamingo.presentation.home.category.adapter.CategoriesAdapter
import store.msolapps.flamingo.presentation.home.category.adapter.ClickItem

@AndroidEntryPoint
class CategoryFromHomeFragment : BaseFragment(), ClickItem {

    private var _binding: FragmentCategoryBinding? = null
    private val viewModel: CategoryViewModel by viewModels()
    private val binding get() = _binding!!
    private lateinit var categoriesAdapter: CategoriesAdapter
    private var originalData: MutableList<CategoriesResponseModel.Data> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        loadPageData()
        setUpData()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        setupListeners()
        loadPageData()
        setUpData()
        setupObservers()
    }

    private fun loadPageData() {
        viewModel.getCategories()
    }

    private fun setUpData() {
        categoriesAdapter = CategoriesAdapter(this)
        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerCategories.layoutManager = layoutManager
        binding.recyclerCategories.adapter = categoriesAdapter
    }

    private fun setupObservers() {
        viewModel.getCategoriesLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.recyclerCategories.visibility = View.GONE
                showLoading()
            } else {
                binding.recyclerCategories.visibility = View.VISIBLE
                hideLoading()
            }
        }

        viewModel.getCategoriesData.observe(viewLifecycleOwner) {
            if(it != null){
                originalData = it.data
                if (it.data.isNotEmpty()) {
                    categoriesAdapter.setData(originalData)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.backIc.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClick(position: Int) {
        val idMainParent: Long = originalData[position].id
        val action = CategoryFromHomeFragmentDirections
            .actionCategoryFromHomeFragmentToShowSingleCategory(idMainParent)
        findNavController().navigate(action)
    }


    override fun onStop() {
        super.onStop()
        viewModel.clearObservers()
    }
}