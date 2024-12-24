package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import com.tt.muzien.R
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.databinding.FragmentFilterBinding
import com.tt.muzien.databinding.FragmentSaloonFilterBinding
import com.tt.muzien.ui.auth.AuthViewModel
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity


class FragmentSaloonFilter : BaseFragment<AuthViewModel, FragmentSaloonFilterBinding, AuthRepository>() {
    var selection: String = ""
    var bookingStatus: String = ""
    var serviceProvider: String = ""
    var fromDate: String = ""
    var toDate: String = ""
    var isFrom: Boolean = true

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.radioBookingStatus.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbPending -> {
                    bookingStatus = "Pending"
                   // checkValidation()
                }

                R.id.rbScheduled -> {
                    bookingStatus = "Scheduled"
                   // checkValidation()
                }

                R.id.rbOverdue -> {
                    bookingStatus = "Overdue"
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
                // Inside your current fragment before popping
                val resultBundle = Bundle().apply {
                    putString("selection", selection) // Replace with your data
                    putString("bookingStatus", bookingStatus) // Replace with your data
                    putString("serviceProvider", serviceProvider) // Replace with your data
                    putString("fromDate", fromDate) // Replace with your data
                    putString("toDate", toDate) // Replace with your data
                }

                setFragmentResult("requestKey", resultBundle)
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
        if (selection == "") {
            binding.txtSelectionError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.txtSelectionError.visibility = View.GONE
        }
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

    override fun getViewModel(): Class<AuthViewModel> {
        return AuthViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSaloonFilterBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        AuthRepository(remoteDataSource.buildApi(AuthApi::class.java), userPreferences)

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.hideTabs()
    }

    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.showTabs()
    }

}