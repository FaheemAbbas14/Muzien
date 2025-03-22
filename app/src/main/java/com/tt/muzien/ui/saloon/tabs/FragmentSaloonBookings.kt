package com.tt.muzien.ui.saloon.tabs

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.R
import com.tt.muzien.data.SaloonBookingData
import com.tt.muzien.data.dto.CalendarDay
import com.tt.muzien.data.dto.SaloonDto
import com.tt.muzien.data.network.BookingApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.BookingRepository
import com.tt.muzien.databinding.FragmentSaloonBookingsBinding
import com.tt.muzien.ui.adapters.SaloonBookingAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.bookings.BookingViewModel
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.FilterSelection
import com.zabihah.ui.ui.interfaces.OnItemClickListner
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class FragmentSaloonBookings :
    BaseFragment<BookingViewModel, FragmentSaloonBookingsBinding, BookingRepository>() {
    private val saloonsBookingList = arrayListOf<SaloonBookingData>()
    private var fromDate: String?=null
    private var toDate: String?=null
    private var bookingDuration: String?=null
    private var bookingStatus: String?=null
    private var bookingServiceProvider: String?=null
    private var adopter: SaloonBookingAdapter? = null
    var selectedSaloon: SaloonDto? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBooking(saloonIds = selectedSaloon?.id.toString())
        binding.customCalendarView.setOnDaySelectedListener { selectedDay ->
            // Toast.makeText(requireContext(), "Selected: ${selectedDay}", Toast.LENGTH_SHORT).show()
        }
        binding.customCalendarView.setDays(generateDaysWithEvents())
        binding.imgBookingFilter.setOnClickListener {
            var nextFragment = FragmentSaloonFilter()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        if (FilterSelection.filterData != null) {

            bookingStatus = FilterSelection.filterData!!.bookingStatus
            bookingServiceProvider = FilterSelection.filterData!!.serviceProvider
            fromDate = FilterSelection.filterData!!.from
            toDate = FilterSelection.filterData!!.to
            if (FilterSelection.filterData!!.selection != "") {

                bookingDuration = FilterSelection.filterData!!.selection.toString()
                if (bookingDuration == "Custom") {
                    //  binding.txtMonth.text = "$fromDate To ${toDate}"
                } else {
                    // binding.txtMonth.text = bookingDuration
                }

            }
            if (bookingStatus != "") {
                binding.imgBookingFilter.setImageResource(
                    R.drawable.blue_cancel
                )
                binding.txtBookingStatus.text = "$bookingStatus Bookings"
                binding.customCalendarView.visibility = View.GONE
                setMargins()

            }
            adopter?.setBookingStatus(bookingStatus)
            adopter?.notifyDataSetChanged()
            getBooking(selectedSaloon?.id.toString(),fromDate,toDate,bookingStatus)
            // Toast.makeText(requireContext(), "data received", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setMargins() {
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(40, 100, 40, 0) // Left, Top, Right, Bottom in pixels
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        }

        binding.rcyBookings.layoutParams = layoutParams
    }

    private fun setBookingsAdopter() {
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
//                var nextFragment = FragmentSaloonDetails()
//                nextFragment.selectedSaloon=saloonsBookingList[position]
//                (activity as HomeActivity?)?.loadFragment(nextFragment)

            }
        }
        adopter = SaloonBookingAdapter(
            saloonsBookingList,
            requireContext(),
            clickListener,
            bookingStatus
        )
        binding.rcyBookings.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rcyBookings.adapter = adopter

    }

    override fun getViewModel(): Class<BookingViewModel> {
        return BookingViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSaloonBookingsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        BookingRepository(remoteDataSource.buildApi(BookingApi::class.java, requireContext()))

    override fun onResume() {
        super.onResume()
        arguments?.getString("key")?.let { data ->
            // Use the data
        }
    }

    private fun generateDaysWithEvents(): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (day in 1..maxDay) {
            val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)

            // Example dot data for specific days
            val eventDotColors = when (day) {
                5 -> listOf(Color.RED, Color.GREEN) // Two dots
                10 -> listOf(Color.BLUE) // One dot
                15 -> listOf(Color.YELLOW, Color.MAGENTA, Color.CYAN) // Three dots
                else -> emptyList()
            }

            days.add(CalendarDay(dayOfWeek, day, eventDotColors = eventDotColors))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return days
    }

    private fun getBooking(
        saloonIds: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        status: String? = null
    ) {
        viewModel.getBooking.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        saloonsBookingList.clear()
//                        for (saloon in it.value.data) {
//                            saloonsBookingList.add(
//                                SaloonBookingData(
//                                    "",
//                                    "Jennifer Austin",
//                                    "Tye Style Zone",
//                                    "Dibra Morgan",
//                                    "Undercut Haircut, Thin Shaving, Shampoo Hair wash"
//                                )
//                            )
//                        }
                        setBookingsAdopter()
                    } else {
                        requireView().snackbar(it.value.message)
                    }
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())

                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.getBookings(saloonIds, startDate, endDate, status)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
}