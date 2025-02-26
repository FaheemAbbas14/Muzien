package com.tt.muzien.ui.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tt.muzien.R
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentAddPaymentBinding
import com.tt.muzien.databinding.FragmentPayNowBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel


class FragmentAddPayment : BaseFragment<HomeViewModel, FragmentAddPaymentBinding, HomeRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.llPayNow.setOnClickListener {

        }
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddPaymentBinding.inflate(inflater, container, false)

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