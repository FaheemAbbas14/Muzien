package com.tt.muzien.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import com.tt.muzien.R
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentHomeBinding
import com.tt.muzien.ui.analytics.FragmentAnalytics
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.member.FragmentMembers
import com.tt.muzien.ui.saloon.FragmentSaloon
import com.tt.muzien.ui.service.FragmentServices


class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding, HomeRepository>() {
    private lateinit var tabLayout: TabLayout
    private lateinit var fragmentManager: FragmentManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = binding.tabLayout
        fragmentManager = requireActivity().supportFragmentManager
        //seTabs()
        // (activity as DashboardActivity?)?.loadFragment(fragment,R.id.container,fragmentManager)
        fragmentManager.beginTransaction().replace(R.id.container, FragmentAnalytics()).commit()
        binding.rdoHomeTabs.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
            val fragment: Fragment = when (selectedRadioButton?.id) {

                R.id.rdoSaloons -> FragmentSaloon()
                R.id.rdoMembers -> FragmentMembers()
                R.id.rdoServices -> FragmentServices()
                else -> FragmentAnalytics()
            }
            resetTabs()
            (activity as HomeActivity?)?.setSelectedTab(selectedRadioButton?.id)
            when (selectedRadioButton?.id) {

                R.id.rdoSaloons -> {
                    binding.rdoSaloons.setBackgroundDrawable(resources.getDrawable(R.drawable.blue_rounded))
                    binding.rdoSaloons.setTextColor(resources.getColor(R.color.white))
                }
                R.id.rdoMembers ->{
                    binding.rdoMembers.setBackgroundDrawable(resources.getDrawable(R.drawable.blue_rounded))
                    binding.rdoMembers.setTextColor(resources.getColor(R.color.white))
                }
                R.id.rdoServices -> {
                    binding.rdoServices.setBackgroundDrawable(resources.getDrawable(R.drawable.blue_rounded))
                    binding.rdoServices.setTextColor(resources.getColor(R.color.white))
                }
                else -> {
                    binding.rdoAnalytics.setBackgroundDrawable(resources.getDrawable(R.drawable.blue_rounded))
                    binding.rdoAnalytics.setTextColor(resources.getColor(R.color.white))
                }
            }
            // (activity as DashboardActivity?)?.loadFragment(fragment,R.id.container,fragmentManager)
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
        }
        // Register a callback for the back button
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val fragmentCount = requireActivity().supportFragmentManager.backStackEntryCount
                    (activity as HomeActivity?)?.popFragment()
                    if (fragmentCount == 1) {
                        requireActivity().finish()
                    }

                }
            })
    }

    private fun resetTabs() {
        binding.rdoAnalytics.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_grey))
        binding.rdoAnalytics.setTextColor(resources.getColor(R.color.colorTextLabelDefault))
        binding.rdoSaloons.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_grey))
        binding.rdoSaloons.setTextColor(resources.getColor(R.color.colorTextLabelDefault))
        binding.rdoMembers.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_grey))
        binding.rdoMembers.setTextColor(resources.getColor(R.color.colorTextLabelDefault))
        binding.rdoServices.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_grey))
        binding.rdoServices.setTextColor(resources.getColor(R.color.colorTextLabelDefault))


    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java), userPreferences)


}