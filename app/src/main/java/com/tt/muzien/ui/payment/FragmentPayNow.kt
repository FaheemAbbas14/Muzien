package com.tt.muzien.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentPayNowBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.ui.views.CustomRadioButton


class FragmentPayNow : BaseFragment<HomeViewModel, FragmentPayNowBinding, HomeRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.llPayNow.setOnClickListener {

        }
        binding.llOtherPayment.setOnClickListener {
            var nextFragment = FragmentAddPayment()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        for (i in 0 until binding.rdoPayment.childCount) {
            val radioButton = binding.rdoPayment.getChildAt(i) as CustomRadioButton
            radioButton.setOnClickListener {
                for (j in 0 until binding.rdoPayment.childCount) {
                    val child = binding.rdoPayment.getChildAt(j) as CustomRadioButton
                    child.isChecked = false
                }
                radioButton.isChecked = true
            }
        }
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPayNowBinding.inflate(inflater, container, false)

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