package com.tt.muzien.ui.bookings

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
import com.tt.muzien.data.dto.BookingsCountData
import com.tt.muzien.data.dto.CalendarDay
import com.tt.muzien.data.dto.FilterData
import com.tt.muzien.data.network.BookingApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.BookingRepository
import com.tt.muzien.data.requests.UpdateBookingRequest
import com.tt.muzien.databinding.FragmentBookingsBinding
import com.tt.muzien.interfaces.IbookingCancel
import com.tt.muzien.ui.adapters.SaloonBookingAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.FilterSelection
import com.tt.muzien.utilities.TimeHelper
import com.zabihah.ui.ui.interfaces.OnItemClickListner
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class FragmentBookings :
    BaseFragment<BookingViewModel, FragmentBookingsBinding, BookingRepository>() {
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
    private val bookingMap = HashMap<Int, BookingsCountData>()

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
            getCalendarbar()
        }
        binding.customCalendarView.setOnDaySelectedListener { selectedDay ->
            // Toast.makeText(requireContext(), "Selected: ${selectedDay}", Toast.LENGTH_SHORT).show()
            fromDate = selectedDay
            toDate = selectedDay
            FilterSelection.filterData = FilterData("", fromDate, toDate, false)
            getBooking()
        }

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
                getCalendarbar()
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
            binding.customCalendarView.setMonthFromDate(fromDate ?: "")
            // Toast.makeText(requireContext(), "data received", Toast.LENGTH_SHORT).show()
            getCalendarbar()
        } else {
            getCalendarbar()
        }


    }

    private fun setMargins(show: Boolean) {
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            if (show) {
                setMargins(40, 100, 40, 0) // Left, Top, Right, Bottom in pixels
            } else {
                setMargins(40, 300, 40, 0) // Left, Top, Right, Bottom in pixels
            }
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        }

        binding.rcyBookings.layoutParams = layoutParams
    }

    private fun setBookingsAdopter() {
        binding.customCalendarView.setDays(generateDaysWithEvents())
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
        val ibookingCancel = object : IbookingCancel {

            override fun onItemClick(position: Int, reason: String) {
                updateBooking(saloonsBookingList[position].bookingId, reason)
            }
        }
        adopter = SaloonBookingAdapter(
            saloonsBookingList,
            requireContext(),
            clickListener,
            ibookingCancel,
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
    ) = FragmentBookingsBinding.inflate(inflater, container, false)

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
            val bookings = bookingMap[day]
            val eventDotColors = mutableListOf<Int>()
            if (bookings != null) {
                if (bookings.pendingApproval > 0) {
                    eventDotColors.add(Color.YELLOW)
                }
                if (bookings.scheduled > 0) {
                    eventDotColors.add(Color.BLUE)
                }
                if (bookings.completed > 0) {
                    eventDotColors.add(Color.GREEN)
                }
                if (bookings.cancelled > 0) {
                    eventDotColors.add(Color.BLACK)
                }
                if (bookings.overdue > 0) {
                    eventDotColors.add(Color.RED)
                }
            }
            days.add(CalendarDay(dayOfWeek, day, eventDotColors = eventDotColors))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return days
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCalendarbar() {
        isLoading = true
        viewModel.getCalendarbar.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    bookingMap.clear()
                    Log.d("response", "success " + it.toString())
                    for (day in it.value.data) {
                        try {
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val date = LocalDate.parse(day.date, formatter)
                            val dayOfMonth = date.dayOfMonth
                            bookingMap.put(
                                dayOfMonth,
                                BookingsCountData(
                                    day.pendingApproval,
                                    day.scheduled,
                                    day.cancelled,
                                    day.completed,
                                    day.cancelled
                                )
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    getBooking()

                }

                is Resource.Failure -> {
                    setBookingsAdopter()
                    Log.d("response", "failure " + it.toString())
                    isLoading = false
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.getCalendarbar(
            startDate = fromDate,
            endDate = toDate
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
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
                                    booking.id.toString(),
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
                    setBookingsAdopter()
                    Log.d("response", "failure " + it.toString())
                    isLoading = false
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.getBookings(
            page = page.toString(),
            status = bookingStatus,
            startDate = fromDate,
            endDate = toDate
        )
        //  (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun updateBooking(bookingId: String, reason: String) {
        isLoading = true
        viewModel.updateBooking.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    requireView().snackbar("Booking updated successfully")

                }

                is Resource.Failure -> {
                    setBookingsAdopter()
                    Log.d("response", "failure " + it.toString())
                    isLoading = false
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.updateBooking(bookingId, UpdateBookingRequest("cancelled", reason))
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

}