package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tt.muzien.R
import com.tt.muzien.data.dto.FilterData
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentSaloonFilterBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.utilities.FilterSelection


class FragmentSaloonFilter :
    BaseFragment<HomeViewModel, FragmentSaloonFilterBinding, HomeRepository>() {
    var selection: String = ""
    var bookingStatus: String = ""
    var serviceProvider: String = ""
    var fromDate: String = ""
    var toDate: String = ""
    var isFrom: Boolean = true

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
                    // checkValidation()
                }

                R.id.rbSpecific -> {
                    serviceProvider = "Specific"
                    // checkValidation()
                }

            }
        }
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbWeek -> {
                    selection = "Week"
                    checkValidation()
                    binding.llFrom.visibility = View.GONE
                    binding.llTo.visibility = View.GONE
                }

                R.id.rbMonth -> {
                    selection = "Month"
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
            binding.datePicker.visibility = View.VISIBLE
        }
        binding.txtTo.setOnClickListener {
            isFrom = false
            binding.datePicker.visibility = View.VISIBLE
        }
        binding.llApply.setOnClickListener {
            if (checkValidation()) {
//                val result = Bundle().apply {
//                    putString("selection", selection) // Replace with your data
//                    putString("bookingStatus", bookingStatus) // Replace with your data
//                    putString("serviceProvider", serviceProvider) // Replace with your data
//                    putString("fromDate", fromDate) // Replace with your data
//                    putString("toDate", toDate) // Replace with your data
//                }
//
//// Set the result before popping the current fragment
//                parentFragmentManager.setFragmentResult("requestKey", result)
                FilterSelection.filterData =
                    FilterData(selection, fromDate, toDate, false,bookingStatus, serviceProvider)
                (activity as HomeActivity?)?.popFragment()
            }
        }
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        // Add the date change listener
        binding.datePicker.init(
            binding.datePicker.year, binding.datePicker.month, binding.datePicker.dayOfMonth
        ) { _, year, monthOfYear, dayOfMonth ->
            val date = "$dayOfMonth/${monthOfYear + 1}/$year"
            if (isFrom) {
                binding.txtFromError.visibility = View.VISIBLE
                binding.llTo.visibility = View.VISIBLE
                fromDate = date
                binding.txtFrom.text = fromDate
            } else {
                toDate = date
                binding.txtTo.text = toDate
            }
            binding.datePicker.visibility = View.GONE
            checkValidation()
        }
        // checkValidation()
    }


    private fun checkValidation(): Boolean {
        var isValid = true
//        if (selection == "") {
//            binding.txtSelectionError.visibility = View.VISIBLE
//            isValid = false
//        } else {
//            binding.txtSelectionError.visibility = View.GONE
//        }
//        if (selection == "Custom" && fromDate == "") {
//            isValid = false
//            binding.txtFromError.visibility = View.VISIBLE
//        } else {
//            binding.txtFromError.visibility = View.GONE
//        }
//        if (selection == "Custom" && toDate == "" && fromDate != "") {
//            binding.txtToError.visibility = View.VISIBLE
//            isValid = false
//        } else {
//            binding.txtToError.visibility = View.GONE
//        }

        return isValid
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSaloonFilterBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java), userPreferences)

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.hideTabs()
    }

    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.showTabs()
    }

}