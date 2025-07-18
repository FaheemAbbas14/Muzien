package com.tt.muzien.ui.profile

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.data.dto.LoggedInInfo
import com.tt.muzien.data.network.MemberApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.MemberRepository
import com.tt.muzien.data.requests.AddHolidayRequest
import com.tt.muzien.data.requests.Holiday
import com.tt.muzien.databinding.FragmentAddAdminHolidaysBinding
import com.tt.muzien.ui.adapters.HolidayListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.enable
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.member.MemberViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.ui.views.CustomCalendar
import com.tt.muzien.utilities.TimeHelper
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentAddAdminHolidays :
    BaseFragment<MemberViewModel, FragmentAddAdminHolidaysBinding, MemberRepository>() {
    private val adminHolidays = arrayListOf<String>()
    private val holidaysMap = HashMap<String, Long>()
    private var holidayListAdapter: HolidayListAdapter? = null
    private var selectedDay: ArrayList<String> = arrayListOf()
    private var position: Int = 0
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserHolidays()
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.llSave.enable(false)
        binding.llSave.setOnClickListener {
            addMemberHoliday()
        }
        binding.customCalendar.setMultiSelectEnabled(true)
        // Set the listener for date selection
        binding.customCalendar.setOnDateSelectedListener(object :
            CustomCalendar.OnDateSelectedListener {

            override fun onDatesSelected(selectedDates: ArrayList<String>) {
                binding.llSave.enable(true)
                selectedDay = selectedDates
            }
        })

    }

    private fun setHolidaysAdopter() {
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(selectedPosition: Int) {
                position = selectedPosition
                deleteHoliday(holidaysMap[adminHolidays[selectedPosition]]?.toInt() ?: 0)
            }
        }
        binding.rcyHolidays.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        holidayListAdapter = HolidayListAdapter(
            adminHolidays,
            false,
            clickListener
        )
        binding.rcyHolidays.adapter = holidayListAdapter


    }

    override fun getViewModel(): Class<MemberViewModel> {
        return MemberViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentAddAdminHolidaysBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        MemberRepository(
            remoteDataSource.buildApi(MemberApi::class.java, requireContext()),
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addMemberHoliday() {
        viewModel.addMemberData.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    selectedDay.clear()
                    Log.d("response", "success " + it.toString())
                    if (it.value.status != 0) {
                        getUserHolidays()
                    } else {
                        (activity as HomeActivity?)?.hideLoadingIndicator()
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
        for (date in selectedDay) {
            holidays.add(Holiday(date, date))
        }

        viewModel.addHoliday(LoggedInInfo.user?.id?.toInt()!!, AddHolidayRequest(holidays))
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getUserHolidays() {
        viewModel.getUserHolidays.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    adminHolidays.clear()
                    holidaysMap.clear()
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()

                    if (it.value.status != 0) {
                        for (holiday in it.value.data) {
                            adminHolidays.add(TimeHelper.getFormatedDateISO(holiday.startDate))
                            holidaysMap.put(
                                TimeHelper.getFormatedDateISO(holiday.startDate),
                                holiday.id
                            )

                        }
                        setHolidaysAdopter()
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
        viewModel.getUserHolidays(LoggedInInfo.user?.id?.toInt()!!)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun deleteHoliday(holidayId: Int) {
        viewModel.remove.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        adminHolidays.removeAt(position)
                        holidayListAdapter?.notifyDataSetChanged()
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
        viewModel.removeHoliday(LoggedInInfo.user?.id?.toInt()!!, holidayId)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
}