package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentAddWorkingDayBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel


class FragmentAddWorkingDay :
    BaseFragment<HomeViewModel, FragmentAddWorkingDayBinding, HomeRepository>() {
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.llSave.setOnClickListener {


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