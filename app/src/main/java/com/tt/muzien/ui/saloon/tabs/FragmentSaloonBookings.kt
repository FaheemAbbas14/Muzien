package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.data.SaloonBookingData
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentSaloonBookingsBinding
import com.tt.muzien.ui.adopters.SaloonBookingAdopter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.FragmentFilter
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentSaloonBookings :
    BaseFragment<HomeViewModel, FragmentSaloonBookingsBinding, HomeRepository>() {
    private val saloonsBookingList = arrayListOf<SaloonBookingData>()
    private var fromDate: String = ""
    private var toDate: String = ""
    private var bookingDuration: String = ""
    private var bookingStatus: String = ""
    private var bookingServiceProvider: String = ""

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setSaloonAdopter()
        binding.imgBookingFilter.setOnClickListener {
            var nextFragment = FragmentSaloonFilter()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        setFragmentResultListener("requestKey") { key, bundle ->
            val selection = bundle.getString("selection")
            val bookingStatusSelected = bundle.getString("bookingStatus")
            val serviceProvider = bundle.getString("serviceProvider")
            val fromDateFilter = bundle.getString("fromDate")
            val toDateFilter = bundle.getString("toDate")
            if (selection != "") {
                bookingDuration = selection.toString()
                bookingStatus = bookingStatusSelected.toString()
                bookingServiceProvider = serviceProvider.toString()
                fromDate = fromDateFilter.toString()
                toDate = toDateFilter.toString()
                if (selection == "Custom") {
                    binding.txtMonth.text = "$fromDate To ${toDate}"
                } else {
                    binding.txtMonth.text = selection
                }
                setSaloonAdopter()
            }

        }
    }

    private fun setSaloonAdopter() {
        saloonsBookingList.clear()
        for (i in 0..10) {
            println("Index: $i")
            saloonsBookingList.add(
                SaloonBookingData("","Jennifer Austin","Tye Style Zone","Dibra Morgan","Undercut Haircut, Thin Shaving, Shampoo Hair wash")
            )
        }

        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
//                var nextFragment = FragmentSaloonDetails()
//                nextFragment.selectedSaloon=saloonsBookingList[position]
//                (activity as HomeActivity?)?.loadFragment(nextFragment)

            }
        }
        binding.rcyBookings.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rcyBookings.adapter =
            SaloonBookingAdopter(
                saloonsBookingList,
                requireContext(),
                clickListener
            )
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

}