package com.tt.muzien.ui.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.tt.muzien.R
import com.tt.muzien.data.dto.FilterData
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.databinding.FragmentFilterBinding
import com.tt.muzien.ui.auth.AuthViewModel
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.views.CustomCalendar
import com.tt.muzien.utilities.Appelement
import com.tt.muzien.utilities.FilterSelection
import com.tt.muzien.utilities.TimeHelper


class FragmentFilter : BaseFragment<AuthViewModel, FragmentFilterBinding, AuthRepository>() {
    var selection: String = ""
    var fromDate: String? = null
    var toDate: String? = null
    var isFrom: Boolean = true
    var isFromRevenue: Boolean = false
    var status = false
    var selectedStatus: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (status) {
            binding.selectedOptionText.text = requireContext().getString(R.string.timeperiod)
            binding.selectedOptionText.text = "Select Status"
            binding.radioGroup.visibility = View.GONE
            binding.radioGroupStatus.visibility = View.VISIBLE
        } else {
            binding.selectedOptionText.text = requireContext().getString(R.string.timeperiod)
            binding.radioGroup.visibility = View.GONE
            binding.radioGroupStatus.visibility = View.VISIBLE
        }
        binding.radioGroupStatus.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbActive -> {
                    selectedStatus = "Active"
                }

                R.id.rbInActive -> {
                    selectedStatus = "InActive"

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
                    binding.txtFromLabel.visibility = View.GONE
                    binding.txtToLabel.visibility = View.GONE
                }

                R.id.rbMonth -> {
                    selection = "Month"
                    val dates = TimeHelper.getWeekAndMonthDates()
                    fromDate = dates["startOfMonth"]
                    toDate = dates["endOfMonth"]
                    checkValidation()
                    binding.llFrom.visibility = View.GONE
                    binding.llTo.visibility = View.GONE
                    binding.txtFromLabel.visibility = View.GONE
                    binding.txtToLabel.visibility = View.GONE
                }

                R.id.rbCustom -> {
                    selection = "Custom"
                    checkValidation()
                    binding.llFrom.visibility = View.VISIBLE
                    binding.llTo.visibility = View.VISIBLE
                    binding.txtFromLabel.visibility = View.VISIBLE
                    binding.txtToLabel.visibility = View.VISIBLE
                }
            }
        }
        binding.txtFrom.setOnClickListener {
            binding.txtFromError.visibility = View.GONE
            binding.llTo.visibility = View.GONE
            binding.txtToLabel.visibility = View.GONE
            isFrom = true
            binding.customCalendar.visibility = View.VISIBLE
        }
        binding.txtTo.setOnClickListener {
            isFrom = false
            binding.customCalendar.visibility = View.VISIBLE
        }
        binding.llApply.setOnClickListener {
            if (checkValidation()) {
                Appelement.reload = true
                FilterSelection.filterData =
                    FilterData(selection, fromDate, toDate, isFromRevenue, status = selectedStatus)
                (activity as HomeActivity?)?.popFragment()
            }
        }
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        // Set the listener for date selection
        binding.customCalendar.setOnDateSelectedListener(object :
            CustomCalendar.OnDateSelectedListener {
            override fun onDatesSelected(selectedDates: ArrayList<String>) {
                // Handle the selected date
                if (isFrom) {
                    binding.txtFromError.visibility = View.VISIBLE
                    binding.llTo.visibility = View.VISIBLE
                    binding.txtToLabel.visibility = View.VISIBLE
                    fromDate = selectedDates.get(0)
                    binding.txtFrom.text = fromDate
                } else {
                    toDate = selectedDates.get(selectedDates.size-1)
                    binding.txtTo.text = toDate
                }
                binding.customCalendar.visibility = View.GONE
                checkValidation()
            }

        })

        // checkValidation()
    }


    private fun checkValidation(): Boolean {
        var isValid = true
        if (selection == "" && selectedStatus == "") {
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
    ) = FragmentFilterBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        AuthRepository(
            remoteDataSource.buildApi(AuthApi::class.java, requireContext()),
            userPreferences
        )

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
    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.showTabs()
    }

}