package com.tt.muzien.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tt.muzien.R
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentAddPaymentBinding
import com.tt.muzien.databinding.FragmentMyAccountBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel


class FragmentMyAccount : BaseFragment<HomeViewModel, FragmentMyAccountBinding, HomeRepository>() {
    var country: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.countrySpinner.setCountryForNameCode("SA")
        binding.countrySpinner.setOnCountryChangeListener {
            country = binding.countrySpinner.selectedCountryName
            binding.txtCountryCode.text=binding.countrySpinner.selectedCountryCode
            // checkValidation()
        }
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMyAccountBinding.inflate(inflater, container, false)

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