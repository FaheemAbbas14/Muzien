package com.tt.muzien.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import com.tt.muzien.R
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.databinding.FragmentFilterBinding
import com.tt.muzien.ui.auth.AuthViewModel
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.enable


class FragmentFilter : BaseFragment<AuthViewModel, FragmentFilterBinding, AuthRepository>() {
    var selection: String = ""
    var fromDate: String = ""
    var toDate: String = ""
    var isFrom: Boolean = true

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
            isFrom = true
            binding.datePicker.visibility = View.VISIBLE
        }
        binding.txtTo.setOnClickListener {
            isFrom = false
            binding.datePicker.visibility = View.VISIBLE
        }
        binding.llApply.setOnClickListener {
            // Inside your current fragment before popping
            val resultBundle = Bundle().apply {
                putString("selection", selection) // Replace with your data
                putString("fromDate", fromDate) // Replace with your data
                putString("toDate", toDate) // Replace with your data
            }

            setFragmentResult("requestKey", resultBundle)
            (activity as HomeActivity?)?.popFragment()
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
                fromDate = date
                binding.txtFrom.text = fromDate
            } else {
                toDate = date
                binding.txtTo.text = toDate
            }
            binding.datePicker.visibility = View.GONE
            checkValidation()
        }
        checkValidation()
    }


    private fun checkValidation() {
        var isValid = false
        if (selection != "") {
            isValid = true
        }
        if (selection == "Custom" && (fromDate == "" || toDate == "")) {
            isValid = false
        }
        if (isValid) {
            binding.llApply.enable(true)
        } else {
            binding.llApply.enable(false)
        }
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