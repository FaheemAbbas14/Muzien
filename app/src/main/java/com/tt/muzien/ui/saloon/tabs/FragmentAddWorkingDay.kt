package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tt.muzien.R
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentAddWorkingDayBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel


class FragmentAddWorkingDay :
    BaseFragment<HomeViewModel, FragmentAddWorkingDayBinding, HomeRepository>() {
    var selectedDays = arrayListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.llSave.setOnClickListener {


        }
        binding.txtMon.setOnClickListener {
            if (selectedDays.contains("Mon")) {
                selectedDays.remove("Mon")
                binding.txtMon.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_white_blue))
                binding.txtMon.setTextColor(resources.getColor(R.color.colorPrimary))

            } else {
                selectedDays.add("Mon")
                binding.txtMon.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_blue))
                binding.txtMon.setTextColor(resources.getColor(R.color.white))
            }

        }
        binding.txtTue.setOnClickListener {
            if (selectedDays.contains("Tue")) {
                selectedDays.remove("Tue")
                binding.txtTue.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_white_blue))
                binding.txtTue.setTextColor(resources.getColor(R.color.colorPrimary))

            } else {
                selectedDays.add("Tue")
                binding.txtTue.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_blue))
                binding.txtTue.setTextColor(resources.getColor(R.color.white))
            }

        }
        binding.txtWed.setOnClickListener {
            if (selectedDays.contains("Wed")) {
                selectedDays.remove("Wed")
                binding.txtWed.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_white_blue))
                binding.txtWed.setTextColor(resources.getColor(R.color.colorPrimary))

            } else {
                selectedDays.add("Wed")
                binding.txtWed.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_blue))
                binding.txtWed.setTextColor(resources.getColor(R.color.white))
            }

        }
        binding.txtThu.setOnClickListener {
            if (selectedDays.contains("Thu")) {
                selectedDays.remove("Thu")
                binding.txtThu.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_white_blue))
                binding.txtThu.setTextColor(resources.getColor(R.color.colorPrimary))

            } else {
                selectedDays.add("Thu")
                binding.txtThu.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_blue))
                binding.txtThu.setTextColor(resources.getColor(R.color.white))
            }

        }
        binding.txtFri.setOnClickListener {
            if (selectedDays.contains("Fri")) {
                selectedDays.remove("Fri")
                binding.txtFri.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_white_blue))
                binding.txtFri.setTextColor(resources.getColor(R.color.colorPrimary))

            } else {
                selectedDays.add("Fri")
                binding.txtFri.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_blue))
                binding.txtFri.setTextColor(resources.getColor(R.color.white))
            }

        }
        binding.txtSat.setOnClickListener {
            if (selectedDays.contains("Sat")) {
                selectedDays.remove("Sat")
                binding.txtSat.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_white_blue))
                binding.txtSat.setTextColor(resources.getColor(R.color.colorPrimary))

            } else {
                selectedDays.add("Sat")
                binding.txtSat.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_blue))
                binding.txtSat.setTextColor(resources.getColor(R.color.white))
            }

        }
        binding.txtSun.setOnClickListener {
            if (selectedDays.contains("Sun")) {
                selectedDays.remove("Sun")
                binding.txtSun.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_white_blue))
                binding.txtSun.setTextColor(resources.getColor(R.color.colorPrimary))

            } else {
                selectedDays.add("Sun")
                binding.txtSun.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_blue))
                binding.txtSun.setTextColor(resources.getColor(R.color.white))
            }

        }
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddWorkingDayBinding.inflate(inflater, container, false)

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