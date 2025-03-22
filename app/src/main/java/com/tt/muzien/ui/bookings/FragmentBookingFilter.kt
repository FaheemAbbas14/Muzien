package com.tt.muzien.ui.bookings

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.tt.muzien.R
import com.tt.muzien.data.dto.FilterData
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentBookingFilterBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.ui.views.CustomCalendar
import com.tt.muzien.utilities.FilterSelection
import com.tt.muzien.utilities.TimeHelper


class FragmentBookingFilter :
    BaseFragment<HomeViewModel, FragmentBookingFilterBinding, HomeRepository>() {
    var selection: String = ""
    var bookingStatus: String?=null
    var serviceProvider: String?=null
    var fromDate: String?=null
    var toDate: String?=null
    var isFrom: Boolean = true
    var saloon: String = ""
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.radioBookingStatus.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbPending -> {
                    bookingStatus = "Pending Approval"
                    // checkValidation()
                }

                R.id.rbScheduled -> {
                    bookingStatus = "Scheduled"
                    // checkValidation()
                }

                R.id.rbOverdue -> {
                    bookingStatus = "Overdue/Incomplete"
                    // checkValidation()

                }

                R.id.rbCompleted -> {
                    bookingStatus = "Completed"
                    //checkValidation()
                }

                R.id.rbCancelled -> {
                    bookingStatus = "Cancelled"
                    //checkValidation()
                }
            }
        }
        binding.radioServiceProvider.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbAllProvider -> {
                    serviceProvider = "AllProvider"
                    binding.llProvider.visibility = View.GONE
                    // checkValidation()
                }

                R.id.rbSpecific -> {
                    serviceProvider = "Specific"
                    binding.llProvider.visibility = View.VISIBLE
                    // checkValidation()
                }

            }
        }
        binding.radioSaloon.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbAllSaloon -> {
                    saloon = "AllSaloon"
                    binding.llSaloon.visibility = View.GONE
                    // checkValidation()
                }

                R.id.rbSpecificSaloon -> {
                    saloon = "Specific"
                    binding.llSaloon.visibility = View.VISIBLE
                    // checkValidation()
                }

            }
        }
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbWeek -> {
                    selection = "Week"
                    val dates = TimeHelper.getWeekAndMonthDates()
                    fromDate = dates["startOfWeek"]
                    toDate = dates["endOfWeek"]
                    checkValidation()
                    binding.llFrom.visibility = View.GONE
                    binding.llTo.visibility = View.GONE
                }

                R.id.rbMonth -> {
                    selection = "Month"
                    val dates = TimeHelper.getWeekAndMonthDates()
                    fromDate = dates["startOfMonth"]
                    toDate = dates["endOfMonth"]
                    checkValidation()
                    binding.llFrom.visibility = View.GONE
                    binding.llTo.visibility = View.GONE
                }

                R.id.rbCustom -> {
                    selection = "Custom"
                    checkValidation()
                    binding.llFrom.visibility = View.VISIBLE
                    // binding.llTo.visibility = View.VISIBLE
                }
            }
        }
        binding.txtFrom.setOnClickListener {
            binding.txtFromError.visibility = View.GONE
            binding.llTo.visibility = View.GONE
            isFrom = true
            binding.customCalendar.visibility = View.VISIBLE
        }
        binding.txtTo.setOnClickListener {
            isFrom = false
            binding.customCalendar.visibility = View.VISIBLE
        }
        binding.llApply.setOnClickListener {
            if (checkValidation()) {
                if (serviceProvider != "AllProvider") {
                    serviceProvider = binding.edtProvider.text.toString()
                }
                if (saloon != "AllSaloon") {
                    saloon = binding.edtSaloon.text.toString()
                }
                FilterSelection.filterData =
                    FilterData(
                        selection,
                        fromDate,
                        toDate,
                        false,
                        bookingStatus,
                        serviceProvider,
                        saloon
                    )
                (activity as HomeActivity?)?.popFragment()
            }
        }
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.customCalendar.setOnDateSelectedListener(object :
            CustomCalendar.OnDateSelectedListener {
            override fun onDateSelected(date: String) {
                // Handle the selected date
                if (isFrom) {
                    // binding.txtFromError.visibility = View.VISIBLE
                    binding.llTo.visibility = View.VISIBLE
                    fromDate = date
                    binding.txtFrom.text = fromDate
                } else {
                    toDate = date
                    binding.txtTo.text = toDate
                }
                binding.customCalendar.visibility = View.GONE
                checkValidation()
            }
        })

    }


    private fun checkValidation(): Boolean {
        var isValid = true
//        if (selection == "") {
//            binding.txtSelectionError.visibility = View.VISIBLE
//            isValid = false
//        } else {
//            binding.txtSelectionError.visibility = View.GONE
//        }
        if (selection == "Custom" && fromDate == "") {
            isValid = false
            binding.txtFromError.visibility = View.VISIBLE
        } else {
            binding.txtFromError.visibility = View.GONE
        }
        if (selection == "Custom" && toDate == "" && fromDate != "") {
            binding.txtToError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.txtToError.visibility = View.GONE
        }

        return isValid
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentBookingFilterBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java,requireContext()), userPreferences)

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.hideTabs()
    }

    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.showTabs()
    }

}