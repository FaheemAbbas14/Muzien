package com.tt.muzien.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import com.tt.muzien.R
import com.tt.muzien.data.dto.FilterData
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.databinding.FragmentFilterBinding
import com.tt.muzien.ui.auth.AuthViewModel
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.views.CustomCalendar
import com.tt.muzien.utilities.FilterSelection


class FragmentFilter : BaseFragment<AuthViewModel, FragmentFilterBinding, AuthRepository>() {
    var selection: String = ""
    var fromDate: String = ""
    var toDate: String = ""
    var isFrom: Boolean = true
    var isFromRevenue: Boolean = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbWeek -> {
                    selection = "Week"
                    checkValidation()
                    binding.llFrom.visibility = View.GONE
                    binding.llTo.visibility = View.GONE
                    binding.txtFromLabel.visibility = View.GONE
                    binding.txtToLabel.visibility = View.GONE
                }

                R.id.rbMonth -> {
                    selection = "Month"
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
                FilterSelection.filterData =
                    FilterData(selection, fromDate, toDate,isFromRevenue)
                (activity as HomeActivity?)?.popFragment()
            }
        }
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        // Set the listener for date selection
        binding.customCalendar.setOnDateSelectedListener(object : CustomCalendar.OnDateSelectedListener {
            override fun onDateSelected(date: String) {
                // Handle the selected date
                if (isFrom) {
                    binding.txtFromError.visibility = View.VISIBLE
                    binding.llTo.visibility = View.VISIBLE
                    binding.txtToLabel.visibility = View.VISIBLE
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
    ) = FragmentFilterBinding.inflate(inflater, container, false)

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