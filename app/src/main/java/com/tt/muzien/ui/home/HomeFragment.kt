package com.tt.muzien.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tabLayout = binding.tabLayout
        fragmentManager = requireActivity().supportFragmentManager
        seTabs()
    }

    private fun seTabs() {
        // Create tabs with custom view
        val homeTab = tabLayout.newTab().setCustomView(R.layout.custom_tab)
        val halalTab = tabLayout.newTab().setCustomView(R.layout.custom_tab)
        val deliveryTab = tabLayout.newTab().setCustomView(R.layout.custom_tab)
        val prayerTab = tabLayout.newTab().setCustomView(R.layout.custom_tab)
        // Set text and icons for tabs
        homeTab.customView?.let {
            it.findViewById<TextView>(R.id.tab_title).text = "Anaylitics"
            it.findViewById<TextView>(R.id.tab_title).setBackgroundResource(R.drawable.blue_rounded)
            it.findViewById<TextView>(R.id.tab_title)
                .setTextColor(resources.getColor(R.color.white))
        }

        halalTab.customView?.let {
            it.findViewById<TextView>(R.id.tab_title).text = "Saloon"
            it.findViewById<TextView>(R.id.tab_title).setBackgroundResource(R.drawable.rounded_grey)
            it.findViewById<TextView>(R.id.tab_title)
                .setTextColor(resources.getColor(R.color.colorTextDefault))
        }
        deliveryTab.customView?.let {
            it.findViewById<TextView>(R.id.tab_title).text = "Members"
            it.findViewById<TextView>(R.id.tab_title).setBackgroundResource(R.drawable.rounded_grey)
            it.findViewById<TextView>(R.id.tab_title)
                .setTextColor(resources.getColor(R.color.colorTextDefault))
        }

        prayerTab.customView?.let {
            it.findViewById<TextView>(R.id.tab_title).text = "Services"
            it.findViewById<TextView>(R.id.tab_title).setBackgroundResource(R.drawable.rounded_grey)
            it.findViewById<TextView>(R.id.tab_title)
                .setTextColor(resources.getColor(R.color.colorTextDefault))
        }
        tabLayout.addTab(homeTab)
        tabLayout.addTab(halalTab)
        tabLayout.addTab(deliveryTab)
        tabLayout.addTab(prayerTab)
        fragmentManager.beginTransaction().replace(R.id.container, FragmentAnalytics()).commit()
        // Add TabLayout listener to catch tab selection events
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                println("Selected tab: ${tab?.position}")
                tab?.view?.findViewById<TextView>(R.id.tab_title)
                    ?.setBackgroundResource(R.drawable.blue_rounded)
                tab?.view?.findViewById<TextView>(R.id.tab_title)
                    ?.setTextColor(resources.getColor(R.color.white))
                val fragment: Fragment = when (tab?.position) {
                    1 -> FragmentSaloon()
                    2 -> FragmentMembers()
                    3 -> FragmentServices()
                    else -> FragmentAnalytics()
                }
                // (activity as DashboardActivity?)?.loadFragment(fragment,R.id.container,fragmentManager)
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view?.findViewById<TextView>(R.id.tab_title)
                    ?.setBackgroundResource(R.drawable.rounded_grey)
                tab?.view?.findViewById<TextView>(R.id.tab_title)
                    ?.setTextColor(resources.getColor(R.color.colorTextDefault))
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                println("Reselected tab: ${tab?.position}")
            }
        })

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