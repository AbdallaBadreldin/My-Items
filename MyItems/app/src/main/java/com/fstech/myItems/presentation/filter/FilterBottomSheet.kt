package store.msolapps.flamingo.presentation.filter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FilterBottomSheetBinding
import store.msolapps.flamingo.presentation.home.FilterViewModel

@AndroidEntryPoint
class FilterBottomSheet(
    private val listener: FilterImplementation,
    private val min: Double,
    private val max: Double,
) : BottomSheetDialogFragment() {
    private val viewmodel: FilterViewModel by activityViewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext())

    private var _binding: FilterBottomSheetBinding? = null
    private val binding get() = _binding!!
    val data: MutableList<FilterData> = ArrayList()
    private var filterValue = ""
    private var minValue = 0
    private var maxValue = 0
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FilterBottomSheetBinding.inflate(inflater, container, false)
        minValue = min.toInt()
        maxValue = max.toInt()


        binding.minValue.text = getString(R.string.egps, minValue.toString())
        binding.maxValue.text = getString(R.string.egps, maxValue.toString())
        if (binding.rangeSlider.valueFrom <= minValue.toFloat() && binding.rangeSlider.valueTo >= maxValue.toFloat()) {
            binding.rangeSlider.values = listOf(minValue.toFloat(), maxValue.toFloat())
            binding.rangeSlider.valueFrom = minValue.toFloat()
            binding.rangeSlider.valueTo = maxValue.toFloat()
        } else {
            binding.rangeSlider.values = listOf(minValue.toFloat(), maxValue.toFloat())
            binding.rangeSlider.valueFrom = minValue.toFloat()+1
            binding.rangeSlider.valueTo = maxValue.toFloat()-1
        }

        binding.startValue.text = minValue.toString()
        binding.endValue.text = maxValue.toString()

        binding.fromPrice.text =
            Editable.Factory.getInstance().newEditable(minValue.toString())
        binding.toPrice.text =
            Editable.Factory.getInstance().newEditable(maxValue.toString())

        binding.rangeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            binding.minValue.text = getString(R.string.egps, values[0].toInt().toString())
            binding.maxValue.text = getString(R.string.egps, values[1].toInt().toString())
            binding.fromPrice.text =
                Editable.Factory.getInstance().newEditable(values[0].toInt().toString())
            binding.toPrice.text =
                Editable.Factory.getInstance().newEditable(values[1].toInt().toString())
            this.minValue = values[0].toInt()
            this.maxValue = values[1].toInt()
        }
        binding.showResultsButton.setOnClickListener {
            if (binding.relevance.isChecked) {
                filterValue = ""
            } else if (binding.priceHighLow.isChecked) {
                filterValue = "price_high"
            } else {
                filterValue = "price_low"
            }
            if (binding.fromPrice.text.toString().toInt() < minValue
                || binding.fromPrice.text.toString().toInt() > maxValue
            ) {
                binding.fromPrice.text =
                    Editable.Factory.getInstance().newEditable(minValue.toString())
            }

            if (binding.toPrice.text.toString().toInt() > maxValue ||
                binding.toPrice.text.toString().toInt() < minValue
            ) {
                binding.toPrice.text =
                    Editable.Factory.getInstance().newEditable(maxValue.toString())
            }
            listener.onFilterListener(
                binding.fromPrice.text.toString().toInt(),
                binding.toPrice.text.toString().toInt(), filterValue
            )
            viewmodel.filterValue = filterValue
            viewmodel.minValue = minValue
            viewmodel.maxValue = maxValue
            dismiss()
        }

        binding.resetText.setOnClickListener {
            viewmodel.resetFilter()
            listener.onFilterListener(min.toInt(), max.toInt(), "")
            dismiss()
        }
        binding.closeIc.setOnClickListener {
            dismiss()
        }
        setDataOnScreen()
        setUpFilter()
        return binding.root
    }

    private fun setUpFilter() {
        when (viewmodel.filterValue) {
            null, "" -> {
                binding.relevance.isChecked = true
                binding.priceLowHigh.isChecked = false
                binding.priceHighLow.isChecked = false
            }

            "price_high" -> {
                binding.priceHighLow.isChecked = true
                binding.relevance.isChecked = false
                binding.priceLowHigh.isChecked = false
            }

            else -> {
                binding.priceLowHigh.isChecked = true
                binding.relevance.isChecked = false
                binding.priceHighLow.isChecked = false
            }
        }
    }

    private fun setDataOnScreen() {
        if (viewmodel.minValue != null && viewmodel.maxValue != null) {
            val mini = viewmodel.minValue!!.toFloat()
            val maxi = viewmodel.maxValue!!.toFloat()
            binding.rangeSlider.values = listOf(mini, maxi)
            minValue = viewmodel.minValue!!
            maxValue = viewmodel.maxValue!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button = requireView().findViewById<Button>(R.id.show_results_button)
        button.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dark_blue))
    }
}

data class FilterData(val name: String, val value: String, var clicked: Boolean)

interface FilterImplementation {
    fun onFilterListener(
        minValue: Int, maxValue: Int, filterTxt: String
    )
}
