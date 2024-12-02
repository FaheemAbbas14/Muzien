package com.tt.muzien.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tt.muzien.R
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentAnalyticsBinding
import com.tt.muzien.databinding.FragmentBookingsBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeViewModel


class FragmentBookings : BaseFragment<HomeViewModel, FragmentBookingsBinding, HomeRepository>() {

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentBookingsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java), userPreferences)

}