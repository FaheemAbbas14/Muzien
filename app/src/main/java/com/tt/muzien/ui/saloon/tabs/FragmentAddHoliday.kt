package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tt.muzien.data.dto.AddSaloonData
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.SaloonApi
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.data.requests.AddHolidayRequest
import com.tt.muzien.data.requests.Holiday
import com.tt.muzien.databinding.FragmentAddHolidayBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.saloon.SaloonViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.ui.views.CustomCalendar


class FragmentAddHoliday :
    BaseFragment<SaloonViewModel, FragmentAddHolidayBinding, SaloonRepository>() {
    var selectedDate: String = ""
    var isEdit = false
    var saloonId: Int = 0
    var userId: Int = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setMemberRepo((activity as HomeActivity?)?.getMemberRepo()!!)
        if (userId!=0){
            binding.txtText.text="Add user holidays"
        }
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.llSave.setOnClickListener {
            if (selectedDate != "") {
                if (userId != 0) {
                    addMemberHoliday()

                } else if (isEdit) {
                    addHoliday()
                } else {
                    if (!AddSaloonData.holidays.contains(selectedDate)) {
                        AddSaloonData.holidays.add(selectedDate)
                        (activity as HomeActivity?)?.popFragment()
                    }
                }
            }

        }

        // Set the listener for date selection
        binding.customCalendar.setOnDateSelectedListener(object :
            CustomCalendar.OnDateSelectedListener {
            override fun onDateSelected(date: String) {
                selectedDate = date
                // Handle the selected date
                //   Toast.makeText(requireContext(), "Selected date: $date", Toast.LENGTH_SHORT).show()

            }
        })

    }

    override fun getViewModel(): Class<SaloonViewModel> {
        return SaloonViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddHolidayBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        SaloonRepository(
            remoteDataSource.buildApi(SaloonApi::class.java, requireContext())
        )

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.hideTabs()
    }

    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.showTabs()
    }

    private fun addHoliday() {
        viewModel.addHoliday.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        (activity as HomeActivity?)?.popFragment()
                        requireView().snackbar("Holiday added successfully")
                    } else {
                        requireView().snackbar(it.value.message)
                    }
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())

                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        var holidays = ArrayList<Holiday>()
        holidays.add(Holiday(selectedDate, selectedDate))

        viewModel.addHoliday(saloonId, AddHolidayRequest(holidays))
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun addMemberHoliday() {
        viewModel.addMemberData.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        (activity as HomeActivity?)?.popFragment()
                        requireView().snackbar("Holiday added successfully")
                    } else {
                        requireView().snackbar(it.value.message)
                    }
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())

                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        var holidays = ArrayList<Holiday>()
        holidays.add(Holiday(selectedDate, selectedDate))

        viewModel.addMemberHoliday(userId, AddHolidayRequest(holidays))
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
}