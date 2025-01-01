package com.tt.muzien.ui.saloon

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.tt.muzien.R
import com.tt.muzien.data.dto.SaloonDto
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentSaloonDetailsBinding
import com.tt.muzien.ui.adapters.ViewPagerAdapter
import com.tt.muzien.ui.analytics.FragmentAnalytics
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.ui.saloon.tabs.FragmentSaloonAnalytics
import com.tt.muzien.ui.saloon.tabs.FragmentSaloonBookings
import com.tt.muzien.ui.saloon.tabs.FragmentSaloonInfo
import com.tt.muzien.ui.saloon.tabs.FragmentSaloonMembers
import com.tt.muzien.ui.saloon.tabs.FragmentSaloonReviews
import com.tt.muzien.ui.saloon.tabs.FragmentSaloonServices
import com.tt.muzien.utilities.FilterSelection


class FragmentSaloonDetails :
    BaseFragment<HomeViewModel, FragmentSaloonDetailsBinding, HomeRepository>() {

    var selectedSaloon: SaloonDto? = null
    private lateinit var fragmentManager: FragmentManager

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setStatusBar(view)
        fragmentManager = requireActivity().supportFragmentManager
        // setStatusBar(view)
        binding.imgMore.setOnClickListener {

            //   uploadImage()
        }
        binding.imgCamera.setOnClickListener {

            //  uploadImage()
        }
        binding.imgBack.setOnClickListener {
            FilterSelection.filterData = null
            (activity as HomeActivity?)?.popFragment()
        }
        //  (activity as HomeActivity?)?.loadFragment(FragmentSaloonAnalytics(), R.id.tab_container)
        fragmentManager.beginTransaction().replace(R.id.tab_container, FragmentAnalytics())
            .commit()
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
            val fragment: Fragment = when (selectedRadioButton?.id) {
                R.id.rdoAnalytics -> FragmentAnalytics()
                R.id.rdoBookings -> FragmentSaloonBookings()
                R.id.rdoStoreInfo -> FragmentSaloonInfo()
                R.id.rdoReviews -> FragmentSaloonReviews()
                R.id.rdoServices -> FragmentSaloonServices()
                R.id.rdoMembers -> FragmentSaloonMembers()
                else -> FragmentSaloonAnalytics()
            }
            resetTabs()
            when (selectedRadioButton?.id) {

                R.id.rdoBookings -> {
                    binding.rdoBookings.setBackgroundDrawable(resources.getDrawable(R.drawable.blue_rounded))
                    binding.rdoBookings.setTextColor(resources.getColor(R.color.white))
                }

                R.id.rdoStoreInfo -> {
                    binding.rdoStoreInfo.setBackgroundDrawable(resources.getDrawable(R.drawable.blue_rounded))
                    binding.rdoStoreInfo.setTextColor(resources.getColor(R.color.white))
                }

                R.id.rdoReviews -> {
                    binding.rdoReviews.setBackgroundDrawable(resources.getDrawable(R.drawable.blue_rounded))
                    binding.rdoReviews.setTextColor(resources.getColor(R.color.white))
                }

                R.id.rdoServices -> {
                    binding.rdoServices.setBackgroundDrawable(resources.getDrawable(R.drawable.blue_rounded))
                    binding.rdoServices.setTextColor(resources.getColor(R.color.white))
                }

                R.id.rdoMembers -> {
                    binding.rdoMembers.setBackgroundDrawable(resources.getDrawable(R.drawable.blue_rounded))
                    binding.rdoMembers.setTextColor(resources.getColor(R.color.white))
                }

                else -> {
                    binding.rdoAnalytics.setBackgroundDrawable(resources.getDrawable(R.drawable.blue_rounded))
                    binding.rdoAnalytics.setTextColor(resources.getColor(R.color.white))
                }
            }
            // (activity as HomeActivity?)?.loadFragment(fragment, R.id.tab_container)
            fragmentManager.beginTransaction().replace(R.id.tab_container, fragment).commit()
        }
        setViewPager()
    }

    private fun setViewPager() {

        // Sample data for ViewPager
        val items = listOf("", "", "", "")
        val adapter = ViewPagerAdapter(items)
        binding.viewPager.adapter = adapter

        // Attach the DotsIndicator to the ViewPager
        binding.dotIndicator.attachTo(binding.viewPager)
    }

    private fun resetTabs() {
        binding.rdoAnalytics.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_grey))
        binding.rdoAnalytics.setTextColor(resources.getColor(R.color.colorTextLabelDefault))
        binding.rdoBookings.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_grey))
        binding.rdoBookings.setTextColor(resources.getColor(R.color.colorTextLabelDefault))
        binding.rdoMembers.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_grey))
        binding.rdoMembers.setTextColor(resources.getColor(R.color.colorTextLabelDefault))
        binding.rdoServices.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_grey))
        binding.rdoServices.setTextColor(resources.getColor(R.color.colorTextLabelDefault))
        binding.rdoStoreInfo.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_grey))
        binding.rdoStoreInfo.setTextColor(resources.getColor(R.color.colorTextLabelDefault))
        binding.rdoReviews.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_grey))
        binding.rdoReviews.setTextColor(resources.getColor(R.color.colorTextLabelDefault))


    }

    private fun setStatusBar(view: View) {
// Enable full-screen mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (API 30) and above
            requireActivity().window.setDecorFitsSystemWindows(false)
        } else {
            // For older Android versions
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Adjust the image padding if needed
            v.setPadding(0, 0, 0, systemBarsInsets.bottom)
            insets
        }
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSaloonDetailsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java), userPreferences)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.setSystemWindow(false)
        (activity as HomeActivity?)?.changeStatusBarColor(Color.TRANSPARENT)
        (activity as HomeActivity?)?.hideTabs()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
       (activity as HomeActivity?)?.setSystemWindow(true)
        (activity as HomeActivity?)?.changeStatusBarColor(Color.WHITE)
        (activity as HomeActivity?)?.showTabs()
    }
}
