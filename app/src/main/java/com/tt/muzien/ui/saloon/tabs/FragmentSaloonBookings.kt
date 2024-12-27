package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.R
import com.tt.muzien.data.SaloonBookingData
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentSaloonBookingsBinding
import com.tt.muzien.ui.adopters.SaloonBookingAdopter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.utilities.FilterSelection
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentSaloonBookings :
    BaseFragment<HomeViewModel, FragmentSaloonBookingsBinding, HomeRepository>() {
    private val saloonsBookingList = arrayListOf<SaloonBookingData>()
    private var fromDate: String = ""
    private var toDate: String = ""
    private var bookingDuration: String = ""
    private var bookingStatus: String = ""
    private var bookingServiceProvider: String = ""
    private var adopter: SaloonBookingAdopter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSaloonAdopter()
        binding.customCalendarView.setOnDaySelectedListener { selectedDay ->
           // Toast.makeText(requireContext(), "Selected: ${selectedDay}", Toast.LENGTH_SHORT).show()
        }
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

            }
            adopter?.setBookingStatus(bookingStatus)
            adopter?.notifyDataSetChanged()
           // Toast.makeText(requireContext(), "data received", Toast.LENGTH_SHORT).show()
        }
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
        adopter = SaloonBookingAdopter(
            saloonsBookingList,
            requireContext(),
            clickListener,
            bookingStatus
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
    ) = FragmentSaloonBookingsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java), userPreferences)

    override fun onResume() {
        super.onResume()
        arguments?.getString("key")?.let { data ->
            // Use the data
        }
    }
}