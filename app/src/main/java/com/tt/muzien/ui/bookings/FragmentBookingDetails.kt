package com.tt.muzien.ui.bookings

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.R
import com.tt.muzien.data.SaloonBookingData
import com.tt.muzien.data.dto.BookingServiceDto
import com.tt.muzien.data.network.BookingApi
import com.tt.muzien.data.repository.BookingRepository
import com.tt.muzien.databinding.FragmentBookingDetailsBinding
import com.tt.muzien.ui.adapters.BookingServiceListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.utilities.FilterSelection
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentBookingDetails :
    BaseFragment<BookingViewModel, FragmentBookingDetailsBinding, BookingRepository>() {
    var bookingDto: SaloonBookingData? = null
    private val bookingServices = arrayListOf<BookingServiceDto>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgBack.setOnClickListener {
            FilterSelection.filterData = null
            (activity as HomeActivity?)?.popFragment()
        }
        binding.cnstBookingInfo.setOnClickListener {
            showPopupDialog()
        }
        setBookingServicesAdopter()
    }

    override fun getViewModel(): Class<BookingViewModel> {
        return BookingViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentBookingDetailsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        BookingRepository(remoteDataSource.buildApi(BookingApi::class.java, requireContext()))

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.hideTabs()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as HomeActivity?)?.hideTabs()
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.showTopBar()
    }

    private fun setBookingServicesAdopter() {
        for (i in 1..10) {
            bookingServices.add(BookingServiceDto("Undercut Haircut", "30 mins", "SAR40"))
        }
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
                showPopupDialog()

            }
        }
        binding.rcyServices.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rcyServices.adapter =
            BookingServiceListAdapter(
                bookingServices,
                requireContext(),
                clickListener
            )
    }

    private fun showPopupDialog() {
        // Create Dialog
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.complete_booking, null)
        dialog.setContentView(view)

        // Make dialog background transparent
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Find buttons and handle click events
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val proceedButton = view.findViewById<Button>(R.id.proceed_button)

        btnCancel.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog
        }

        proceedButton.setOnClickListener {
            // Add your logic here (e.g., enable the service)
            dialog.dismiss()
            showCompletedPopupDialog()
        }

        // Show the dialog
        dialog.show()
    }

    private fun showCompletedPopupDialog() {
        // Create Dialog
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.completed_success_popup, null)
        dialog.setContentView(view)

        // Make dialog background transparent
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Show the dialog
        dialog.show()
    }

}