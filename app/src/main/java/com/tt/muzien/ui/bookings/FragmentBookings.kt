package com.tt.muzien.ui.bookings

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.R
import com.tt.muzien.data.SaloonBookingData
import com.tt.muzien.data.dto.CalendarDay
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentAnalyticsBinding
import com.tt.muzien.databinding.FragmentBookingFilterBinding
import com.tt.muzien.databinding.FragmentBookingsBinding
import com.tt.muzien.databinding.FragmentSaloonBookingsBinding
import com.tt.muzien.ui.adapters.SaloonBookingAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.ui.saloon.tabs.FragmentSaloonFilter
import com.tt.muzien.utilities.FilterSelection
import com.zabihah.ui.ui.interfaces.OnItemClickListner
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class FragmentBookings : BaseFragment<HomeViewModel, FragmentBookingsBinding, HomeRepository>() {
    private val saloonsBookingList = arrayListOf<SaloonBookingData>()
    private var fromDate: String = ""
    private var toDate: String = ""
    private var bookingDuration: String = ""
    private var bookingStatus: String = ""
    private var bookingServiceProvider: String = ""
    private var adopter: SaloonBookingAdapter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSaloonAdopter()
        binding.customCalendarView.setOnDaySelectedListener { selectedDay ->
            // Toast.makeText(requireContext(), "Selected: ${selectedDay}", Toast.LENGTH_SHORT).show()
        }
        binding.customCalendarView.setDays(generateDaysWithEvents())
        binding.imgBookingFilter.setOnClickListener {
            var nextFragment = FragmentBookingFilter()
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

    private fun setSaloonAdopter() {
        saloonsBookingList.clear()
        for (i in 0..10) {
            println("Index: $i")
            saloonsBookingList.add(
                SaloonBookingData(
                    "",
                    "Jennifer Austin",
                    "Tye Style Zone",
                    "Dibra Morgan",
                    "Undercut Haircut, Thin Shaving, Shampoo Hair wash"
                )
            )
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

    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentBookingsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java), userPreferences)

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

}