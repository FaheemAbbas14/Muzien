package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.tt.muzien.R
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentAddHolidayBinding
import com.tt.muzien.databinding.FragmentAddWorkingDayBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.ui.views.CustomCalendar


class FragmentAddHoliday :
    BaseFragment<HomeViewModel, FragmentAddHolidayBinding, HomeRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.llSave.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()

        }

        // Set the listener for date selection
        binding.customCalendar.setOnDateSelectedListener(object : CustomCalendar.OnDateSelectedListener {
            override fun onDateSelected(date: String) {
                // Handle the selected date
             //   Toast.makeText(requireContext(), "Selected date: $date", Toast.LENGTH_SHORT).show()

            }
        })

    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddHolidayBinding.inflate(inflater, container, false)

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