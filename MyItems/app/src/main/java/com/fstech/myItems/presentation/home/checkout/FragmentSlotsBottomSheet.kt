package store.msolapps.flamingo.presentation.home.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.domain.models.DayModel
import store.msolapps.domain.models.response.DataGetSlotsResponse
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentSlotsBottomSheetBinding
import store.msolapps.flamingo.presentation.home.checkout.actions.DaysAdapterActions
import store.msolapps.flamingo.presentation.home.checkout.actions.TimeAdapterActions
import store.msolapps.flamingo.presentation.home.checkout.adapter.DaysAdapter
import store.msolapps.flamingo.presentation.home.checkout.adapter.TimeAdapter
import store.msolapps.flamingo.presentation.home.checkout.viewmodels.SlotsViewModel
import java.util.Calendar

@AndroidEntryPoint
class FragmentSlotsBottomSheet : BottomSheetDialogFragment(), DaysAdapterActions,
    TimeAdapterActions {
    private var _binding: FragmentSlotsBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var daysAdapter: DaysAdapter
    private lateinit var timeAdapter: TimeAdapter
    private val viewModel: SlotsViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlotsBottomSheetBinding.inflate(inflater, container, false)
        this.dialog?.setCancelable(false)
        this.dialog?.setCanceledOnTouchOutside(false)
        setupAdapters()
        setupObservers()
        setupListeners()
        return binding.root
    }

    private fun cancelBottomSheet() {
        //when user start Checkout first time and don't save the Schaduale
        //Radio button stay selected so I need notify Checkout that user canceled and I notify him when dismiss the bottom fragment and most latest statue is not selected anything
        if (viewModel.selectedTimeSlotPositionLiveData.value == null || viewModel.selectedTimeSlotPositionLiveData.value == -1) {
//            viewModel.postSelectedTimeSlotPosition(null)
        }
        findNavController().popBackStack()
    }

    private fun setupListeners() {
        binding.buttonBookBottomSlots.setOnClickListener {
            //we need to set selected items
            //first check if it's not null
            if (timeAdapter.mSelectedItem == -1) {
                //nothing selected
                Toast.makeText(context,getString(R.string.please_select_time),Toast.LENGTH_LONG ).show()
                return@setOnClickListener
            }
            //second we post values and positions
            viewModel.postSelectedDaySlotPosition(daysAdapter.mSelectedItem)
            viewModel.postSelectedDaySlot(daysAdapter.getSelectedItem())
            viewModel.postSelectedTimeSlotPosition(timeAdapter.mSelectedItem)
            viewModel.postSelectedTimeSlot(timeAdapter.getSelectedItem())

            viewModel.setSelectedSlot(timeAdapter.mSelectedItem)
            findNavController().popBackStack()
        }
        binding.buttonCancelBottomSlots.setOnClickListener {
            cancelBottomSheet()
        }
        binding.imageView22.setOnClickListener {
            cancelBottomSheet()
        }
    }

    private fun setupObservers() {
        viewModel.getSlotsData.observe(viewLifecycleOwner) { `object` ->
            `object`.data = `object`.data.sortedWith(compareBy({ it.from }))
            timeAdapter.setData(`object`)
        }
        viewModel.selectedDaySlotPositionLiveData.observe(viewLifecycleOwner) {

        }
        viewModel.selectedTimeSlotPositionLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                timeAdapter.setSelectedItem(it)
            }
        }
        viewModel.getSlotsLoad.observe(viewLifecycleOwner) {
            if (it) showLoading() else hideLoading()
        }

    }

    private fun getNameOfMonthByNumber(currentMonthName: Int): String {
        return when (currentMonthName) {
            1 -> {
                getString(R.string.january)
            }

            2 -> {
                getString(R.string.february)
            }

            3 -> {
                getString(R.string.march)
            }

            4 -> {
                getString(R.string.april)
            }

            5 -> {
                getString(R.string.may)
            }

            6 -> {
                getString(R.string.june)
            }

            7 -> {
                getString(R.string.july)
            }

            8 -> {
                getString(R.string.august)
            }

            9 -> {
                getString(R.string.september)
            }

            10 -> {
                getString(R.string.october)
            }

            11 -> {
                getString(R.string.november)
            }

            12 -> {
                getString(R.string.december)
            }

            else -> {
                getString(R.string.january)
            }
        }
    }

    private fun setupAdapters() {
        viewModel.getWeeklyDaysAndDates()
        val nameOfTodayMonth = getNameOfMonthByNumber(viewModel.getCurrentMonthNameNumber())
        val nameOfTomorrowMonth = getNameOfMonthByNumber(viewModel.getTomorrowMonthNameNumber())
        val nameOfAfterTomorrowMonth =
            getNameOfMonthByNumber(viewModel.getAfterTomorrowMonthNameNumber())
        val daysList = mutableListOf<DayModel>().apply {
            add(
                DayModel(
                    viewModel.getCurrentDayNameNumber(),
                    nameOfTodayMonth,
                    getString(R.string.today),
                    viewModel.getCurrentDate(),
                )
            )
            add(
                DayModel(
                    viewModel.getTomorrowDayNameNumber(),
                    nameOfTomorrowMonth,
                    getString(R.string.tomorrow),
                    viewModel.getTomorrowDate(),
                )
            )
            //todo problems will happen between 31/12/2024 and 1/1/2025
            add(
                DayModel(
                    viewModel.getAfterTomorrowDayNameNumber(),
                    nameOfAfterTomorrowMonth,
                    getDayStringOfAfterTommorrow(),
                    viewModel.getAfterTomorrowDate(),
                )
            )
        }
        daysAdapter = DaysAdapter(this)
        daysAdapter.setData(daysList)
        val linearLayout = LinearLayoutManager(context)
        linearLayout.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerTop.adapter = daysAdapter
        binding.recyclerTop.layoutManager = linearLayout
        if (viewModel.selectedDaySlotPositionLiveData.value != null) {
            daysAdapter.setSelectedItem(viewModel.selectedDaySlotPositionLiveData.value ?: 0)
        } else {
            daysAdapter.setSelectedItem(0)
        }

        timeAdapter = TimeAdapter(this)
//        val linearLayout2 =
//            GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = timeAdapter
//        binding.recyclerView.layoutManager = linearLayout2
        if (viewModel.selectedTimeSlotPositionLiveData.value != null) {
            timeAdapter.setSelectedItem(viewModel.selectedTimeSlotPositionLiveData.value ?: -1)
        }
    }

    override fun updateBottomSlotRecycler(date: String) {
        viewModel.getSlots(flag_id = viewModel.flag_id, date)
    }

    override fun onClickDaysAdapter(date: DayModel, position: Int) {
        timeAdapter.mSelectedItem = -1
        daysAdapter.setSelectedItem(position)
    }

    fun getDayStringOfAfterTommorrow(): String {
        var result = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return when (result) {
            Calendar.SATURDAY -> getString(R.string.monday)
            Calendar.SUNDAY -> getString(R.string.tuesday)
            Calendar.MONDAY -> getString(R.string.wednesday)
            Calendar.TUESDAY -> getString(R.string.thursday)
            Calendar.WEDNESDAY -> getString(R.string.friday)
            Calendar.THURSDAY -> getString(R.string.saturday)
            Calendar.FRIDAY -> getString(R.string.sunday)
            else -> getString(R.string.saturday)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClickTimeAdapterItem(position: Int, item: DataGetSlotsResponse) {
        //we need to set date and slots_id and slot_date and cost and flag and date
        if (item.max_orders <= item.orders.size) {
            //here orders slot is complete so we don't change selection
//            timeAdapter.setSelectedItem(-1)
        } else {
            //and change mSelected and update adapter
            timeAdapter.setSelectedItem(position)
        }
    }

    override fun setDataToViewModel(position: Int, item: DataGetSlotsResponse) {
        if (daysAdapter.getSelectedItem() == null) {
            return
        } else {
            viewModel.botFrag_slotDate = daysAdapter.getSelectedItem()?.date ?: "0"
            viewModel.botFrag_slotsId = item.id
//        viewModel.botFrag_slot_date = item.slot_date
            viewModel.botFrag_flagId = item.flag_id
        }
    }

    fun showLoading() {
        binding.frameLayoutLoadingBottomSheet.visibility = View.VISIBLE
        binding.imageViewLoadingBottomSheet.visibility = View.VISIBLE
        Glide.with(this).asGif().load(R.raw.loading).into(binding.imageViewLoadingBottomSheet)
    }

    fun hideLoading() {
        binding.frameLayoutLoadingBottomSheet.visibility = View.GONE
        binding.imageViewLoadingBottomSheet.visibility = View.GONE
    }
}