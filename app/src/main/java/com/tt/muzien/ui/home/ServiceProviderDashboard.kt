package com.tt.muzien.ui.home

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
import com.tt.muzien.data.dto.LoggedInInfo
import com.tt.muzien.data.network.BookingApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.BookingRepository
import com.tt.muzien.data.requests.UpdateBookingRequest
import com.tt.muzien.databinding.FragmentServiceProviderDashboardBinding
import com.tt.muzien.interfaces.IbookingCancel
import com.tt.muzien.ui.adapters.SaloonBookingAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.bookings.BookingViewModel
import com.tt.muzien.ui.bookings.FragmentBookingFilter
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.FilterSelection
import com.tt.muzien.utilities.TimeHelper
import com.zabihah.ui.ui.interfaces.OnItemClickListner
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class ServiceProviderDashboard :
    BaseFragment<BookingViewModel, FragmentServiceProviderDashboardBinding, BookingRepository>() {
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
    var scheduleBookings = 0
    var overdueBookings = 0
    var cancledBookings = 0
    var completedBookings = 0
    var inviteId = 0
    var slaoonId = 0
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setMemberRepo((activity as HomeActivity?)?.getMemberRepo()!!)
      //  LoggedInInfo.user?.status = "Invited"
        val dates = TimeHelper.getWeekAndMonthDates()
        fromDate = dates["startOfMonth"]
        toDate = dates["endOfMonth"]
        binding.llAccept.setOnClickListener {
            acceptInvite()
        }
        binding.customCalendarView.setOnMonthChangedListener { startDate, endDate ->
            Log.d("CalendarFragment", "Month range: $startDate to $endDate")
            // Fetch data or update UI based on date range
            fromDate = startDate
            toDate = endDate
            getAnalytics()
        }
        binding.customCalendarView.setOnDaySelectedListener { selectedDay ->
            // Toast.makeText(requireContext(), "Selected: ${selectedDay}", Toast.LENGTH_SHORT).show()
            fromDate = selectedDay
            toDate = selectedDay
            FilterSelection.filterData = FilterData("", fromDate, toDate, false)
            (activity as HomeActivity?)?.showLoadingIndicator()
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
                getAnalytics()
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
            slaoonId= FilterSelection.filterData!!.saloonId
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
            if (fromDate!="") {
                binding.customCalendarView.setMonthFromDate(fromDate ?: "")
            }
            // Toast.makeText(requireContext(), "data received", Toast.LENGTH_SHORT).show()
            getAnalytics()
        } else {
            if (LoggedInInfo.user?.status == "None") {
                binding.llSendInvite.visibility = View.VISIBLE
                binding.llAcceptInvite.visibility = View.GONE
                binding.llBookings.visibility = View.GONE
            } else if (LoggedInInfo.user?.status == "invited") {
                getLatestInvite()
            } else {
                binding.llBookings.visibility = View.VISIBLE
                binding.llAcceptInvite.visibility = View.GONE
                binding.llSendInvite.visibility = View.GONE
                getAnalytics()
            }
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
        binding.txtScheduled.text = "$scheduleBookings"
        binding.txtOverdue.text = "$overdueBookings"
        binding.txtCompleted.text = "$completedBookings"
        binding.txtCancelled.text = "$cancledBookings"
        binding.customCalendarView.setDays(generateDaysWithEvents())
        if (saloonsBookingList.isNotEmpty()) {
            binding.cnstBookings.visibility = View.VISIBLE
            binding.llNoBookings.visibility = View.GONE
            binding.llAcceptInvite.visibility = View.GONE
            binding.llSendInvite.visibility = View.GONE
        } else {
            binding.llAcceptInvite.visibility = View.GONE
            binding.llSendInvite.visibility = View.GONE
            binding.cnstBookings.visibility = View.GONE
            binding.llNoBookings.visibility = View.VISIBLE
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
    ) = FragmentServiceProviderDashboardBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        BookingRepository(remoteDataSource.buildApi(BookingApi::class.java, requireContext()))

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
        (activity as HomeActivity?)?.setSystemWindow(true)
        (activity as HomeActivity?)?.showTabs()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
        (activity as HomeActivity?)?.setSystemWindow(true)
        (activity as HomeActivity?)?.hideTabs()
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

    private fun getLatestInvite() {
        viewModel.getLatestInvite.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0 && it.value.data.id!=null) {
                        inviteId=it.value.data.id.toInt()
                        binding.txtInvitedText.text="${it.value.data.InvitedBy.fullName} has invited you to join their salon “${it.value.data.Saloon.name}” as a service provider.\n" +
                                "\n" +
                                "Do you want to accept?"
                        binding.llAcceptInvite.visibility = View.VISIBLE
                        binding.llSendInvite.visibility = View.GONE
                        binding.llBookings.visibility = View.GONE
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
        viewModel.getLatestInvite(
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun acceptInvite() {
        viewModel.acceptInvite.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        getAnalytics()
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
        viewModel.acceptInvite(
            inviteId
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAnalytics() {
        viewModel.getAnalytics.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {

                        for (analytics in it.value.data.booking) {
                            if (analytics.status == "completed") {
                                completedBookings = analytics.total_count.toInt()
                            } else if (analytics.status == "cancelled") {
                                cancledBookings = analytics.total_count.toInt()
                            } else if (analytics.status == "overdue") {
                                overdueBookings = analytics.total_count.toInt()
                            } else if (analytics.status == "scheduled") {
                                scheduleBookings = analytics.total_count.toInt()
                            }
                        }
                        getCalendarbar()
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
        viewModel.getAnalytics(
            startDate = fromDate,
            endDate = toDate
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
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
            saloonIds = if (slaoonId>0) slaoonId.toString() else null,
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