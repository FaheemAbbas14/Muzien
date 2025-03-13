package com.tt.muzien.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentAddAdminHolidaysBinding
import com.tt.muzien.ui.adapters.HolidayListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.enable
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.ui.views.CustomCalendar
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentAddAdminHolidays :
    BaseFragment<HomeViewModel, FragmentAddAdminHolidaysBinding, HomeRepository>() {
    private val adminHolidays = arrayListOf<String>()
    private var holidayListAdapter: HolidayListAdapter? = null
    private var selectedDay: String? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHolidaysAdopter()
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.llSave.enable(false)
        binding.llSave.setOnClickListener {
            adminHolidays.add(selectedDay ?: "")
            holidayListAdapter?.notifyDataSetChanged()

        }

        // Set the listener for date selection
        binding.customCalendar.setOnDateSelectedListener(object :
            CustomCalendar.OnDateSelectedListener {
            override fun onDateSelected(date: String) {
                // Handle the selected date
                binding.llSave.enable(true)
                selectedDay = date
                //   Toast.makeText(requireContext(), "Selected date: $date", Toast.LENGTH_SHORT).show()

            }
        })

    }

    private fun setHolidaysAdopter() {
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
                adminHolidays.removeAt(position)
                holidayListAdapter?.notifyDataSetChanged()
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

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddAdminHolidaysBinding.inflate(inflater, container, false)

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