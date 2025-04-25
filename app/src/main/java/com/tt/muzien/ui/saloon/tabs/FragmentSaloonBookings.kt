package com.tt.muzien.ui.saloon.tabs

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tt.muzien.R
import com.tt.muzien.data.SaloonBookingData
import com.tt.muzien.data.dto.CalendarDay
import com.tt.muzien.data.dto.FilterData
import com.tt.muzien.data.dto.SaloonDto
import com.tt.muzien.data.network.BookingApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.BookingRepository
import com.tt.muzien.databinding.FragmentSaloonBookingsBinding
import com.tt.muzien.ui.adapters.SaloonBookingAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.bookings.BookingViewModel
import com.tt.muzien.ui.bookings.FragmentBookingFilter
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.FilterSelection
import com.tt.muzien.utilities.TimeHelper
import com.zabihah.ui.ui.interfaces.OnItemClickListner
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class FragmentSaloonBookings :
    BaseFragment<BookingViewModel, FragmentSaloonBookingsBinding, BookingRepository>() {
    private val saloonsBookingList = arrayListOf<SaloonBookingData>()
    private var fromDate: String? = null
    private var toDate: String? = null
    private var bookingDuration: String? = null
    private var bookingStatus: String? = null
    private var bookingServiceProvider: String? = null
    private var adopter: SaloonBookingAdapter? = null
    private var page = 1
    private var totalPage = 1
    private var selection: Int = 0
    private var isLoading: Boolean = false
    var selectedSaloon: SaloonDto? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dates = TimeHelper.getWeekAndMonthDates()
        fromDate = dates["startOfMonth"]
        toDate = dates["endOfMonth"]
        binding.customCalendarView.setOnMonthChangedListener { startDate, endDate ->
            Log.d("CalendarFragment", "Month range: $startDate to $endDate")
            // Fetch data or update UI based on date range
            fromDate = startDate
            toDate = endDate
            getBooking()
        }
        getBooking()
        binding.customCalendarView.setOnDaySelectedListener { selectedDay ->
            // Toast.makeText(requireContext(), "Selected: ${selectedDay}", Toast.LENGTH_SHORT).show()
            fromDate = selectedDay
            toDate = selectedDay
            FilterSelection.filterData=FilterData("", fromDate, toDate,false)
            getBooking()
        }
        binding.customCalendarView.setDays(generateDaysWithEvents())
        binding.imgBookingFilter.setOnClickListener {
            if (bookingStatus != null) {
                binding.imgBookingFilter.setImageResource(
                    R.drawable.filter_icon
                )
                binding.customCalendarView.visibility = View.VISIBLE
                binding.txtBookingStatus.visibility = View.GONE
                bookingStatus = null
                FilterSelection.filterData!!.bookingStatus = bookingStatus
                setMargins(false)
                getBooking()
            } else {
                var nextFragment = FragmentBookingFilter()
                (activity as HomeActivity?)?.loadFragment(nextFragment)
            }
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
            if (bookingStatus != null && bookingStatus != "") {
                binding.imgBookingFilter.setImageResource(
                    R.drawable.blue_cancel
                )
                binding.txtBookingStatus.text = "$bookingStatus Bookings"
                binding.customCalendarView.visibility = View.GONE
                setMargins(true)

            }
            adopter?.setBookingStatus(bookingStatus)
            adopter?.notifyDataSetChanged()
            // Toast.makeText(requireContext(), "data received", Toast.LENGTH_SHORT).show()
        }
//        val days = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
//        binding.customCalendarView.setCurrentDay(days.minus(1))
    }

    private fun setMargins(show: Boolean) {
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            if (show) {
                setMargins(40, 100, 40, 0) // Left, Top, Right, Bottom in pixels
            }
            else{
                setMargins(80, 100, 40, 0) // Left, Top, Right, Bottom in pixels
            }
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        }

        binding.rcyBookings.layoutParams = layoutParams
    }

    private fun setBookingsAdopter() {
        if (saloonsBookingList.isNotEmpty()) {
            binding.cnstData.visibility = View.VISIBLE
            binding.llNoDta.visibility = View.GONE
        } else {
            binding.cnstData.visibility = View.GONE
            binding.llNoDta.visibility = View.VISIBLE
        }
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
            bookingStatus,
            true
        )
        binding.rcyBookings.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rcyBookings.adapter = adopter

        binding.rcyBookings.scrollToPosition(selection)
        binding.rcyBookings.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && totalItemCount > 0) {
                    // Reached the end of the list
                    loadMoreData()
                }
            }
        })

    }

    fun loadMoreData() {
        if (!isLoading && saloonsBookingList.size > 0) {
            if (page < totalPage) {
                page = page + 1
                selection = saloonsBookingList.size - 1
                (activity as HomeActivity?)?.showLoadingIndicator()
                getBooking()

            }
        }

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

    private fun getBooking() {
        isLoading = true
        viewModel.getBooking.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        isLoading = false
                        if (page == 1) {
                            saloonsBookingList.clear()
                        }
                        totalPage = it.value.data.totalPages.toInt()
                        for (booking in it.value.data.items) {
                            var services = ""
                            for (service in booking.bookingServices) {
                                services += service.serviceDetails.name
                            }
                            saloonsBookingList.add(
                                SaloonBookingData(
                                    booking.customer.picture ?: "",
                                    booking.customer.fullName ?: "",
                                    booking.saloon.name,
                                    booking.serviceProvider.fullName ?: "",
                                    services,
                                    booking.date,
                                    booking.time,
                                    booking.status,
                                    booking.duration
                                )
                            )
                        }

                        setBookingsAdopter()
                    } else {
                        requireView().snackbar(it.value.message)
                    }
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())
                    isLoading = false
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.getBookings(
            saloonIds = selectedSaloon?.id.toString(),
            page = page.toString(),
            status = bookingStatus,
            startDate = fromDate,
            endDate = toDate
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
}