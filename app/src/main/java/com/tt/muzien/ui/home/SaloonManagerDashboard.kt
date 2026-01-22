package com.tt.muzien.ui.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.tt.muzien.R
import com.tt.muzien.data.dto.SaloonDto
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentSaloonManagerDashboardBinding
import com.tt.muzien.ui.adapters.ViewPagerAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.saloon.tabs.FragmentSaloonAnalytics
import com.tt.muzien.ui.saloon.tabs.FragmentSaloonBookings
import com.tt.muzien.ui.saloon.tabs.FragmentSaloonInfo
import com.tt.muzien.ui.saloon.tabs.FragmentSaloonMembers
import com.tt.muzien.ui.saloon.tabs.FragmentSaloonReviews
import com.tt.muzien.ui.saloon.tabs.FragmentSaloonServices
import com.tt.muzien.utilities.FilterSelection
import com.tt.muzien.utilities.TimeHelper


class SaloonManagerDashboard :
    BaseFragment<HomeViewModel, FragmentSaloonManagerDashboardBinding, HomeRepository>() {

    var selectedSaloon: SaloonDto? = null
    private lateinit var fragmentManager: FragmentManager
    var showInviationRequired = false

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setStatusBar(view)
        fragmentManager = requireActivity().supportFragmentManager
        // setStatusBar(view)
        binding.imgMore.visibility = View.GONE
        binding.imgCamera.visibility = View.GONE
        binding.imgMore.setOnClickListener {

            //   uploadImage()
        }
        binding.imgCamera.setOnClickListener {

            //  uploadImage()
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finishAffinity()
                }
            }
        )
//        binding.imgBack.setOnClickListener {
//            FilterSelection.filterData = null
//            (activity as HomeActivity?)?.popFragment()
//        }
        //  (activity as HomeActivity?)?.loadFragment(FragmentSaloonAnalytics(), R.id.tab_container)
        var nextFragment = FragmentSaloonAnalytics()
        nextFragment.selectedSaloon = selectedSaloon
//        childFragmentManager.beginTransaction().replace(R.id.tab_container, nextFragment)
//            .commit()
//        childFragmentManager.executePendingTransactions()
        showFragment(nextFragment)
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
            val fragment: Fragment = when (selectedRadioButton?.id) {
                R.id.rdoAnalytics -> {
                    var nextFragment = FragmentSaloonAnalytics()
                    nextFragment.selectedSaloon = selectedSaloon
                    nextFragment
                }

                R.id.rdoBookings -> {
                    var nextFragment = FragmentSaloonBookings()
                    nextFragment.selectedSaloon = selectedSaloon
                    nextFragment
                }

                R.id.rdoStoreInfo -> {
                    var nextFragment = FragmentSaloonInfo()
                    nextFragment.selectedSaloon = selectedSaloon
                    nextFragment
                }

                R.id.rdoReviews -> {
                    var nextFragment = FragmentSaloonReviews()
                    nextFragment.selectedSaloon = selectedSaloon
                    nextFragment
                }

                R.id.rdoServices -> {
                    val nextFragment = FragmentSaloonServices()
                    nextFragment.saloonId = selectedSaloon?.id
                    nextFragment.selectedSaloon = selectedSaloon
                    nextFragment
                }

                R.id.rdoMembers -> {
                    var nextFragment = FragmentSaloonMembers()
                    nextFragment.selectedSaloon = selectedSaloon
                    nextFragment
                }

                else -> {
                    var nextFragment = FragmentSaloonAnalytics()
                    nextFragment.selectedSaloon = selectedSaloon
                    nextFragment
                }
            }
            resetTabs()
            when (selectedRadioButton?.id) {

                R.id.rdoBookings -> {
                    //setFrameLayoutHeight(true)
                    binding.rdoBookings.setBackgroundDrawable(resources.getDrawable(R.drawable.blue_rounded))
                    binding.rdoBookings.setTextColor(resources.getColor(R.color.white))
                }

                R.id.rdoStoreInfo -> {
                    setFrameLayoutHeight(false)
                    binding.rdoStoreInfo.setBackgroundDrawable(resources.getDrawable(R.drawable.blue_rounded))
                    binding.rdoStoreInfo.setTextColor(resources.getColor(R.color.white))
                }

                R.id.rdoReviews -> {
                   // setFrameLayoutHeight(true)
                    binding.rdoReviews.setBackgroundDrawable(resources.getDrawable(R.drawable.blue_rounded))
                    binding.rdoReviews.setTextColor(resources.getColor(R.color.white))
                }

                R.id.rdoServices -> {
                   // setFrameLayoutHeight(true)
                    binding.rdoServices.setBackgroundDrawable(resources.getDrawable(R.drawable.blue_rounded))
                    binding.rdoServices.setTextColor(resources.getColor(R.color.white))
                }

                R.id.rdoMembers -> {
                    //setFrameLayoutHeight(true)
                    binding.rdoMembers.setBackgroundDrawable(resources.getDrawable(R.drawable.blue_rounded))
                    binding.rdoMembers.setTextColor(resources.getColor(R.color.white))
                }

                else -> {
                    setFrameLayoutHeight(false)
                    binding.rdoAnalytics.setBackgroundDrawable(resources.getDrawable(R.drawable.blue_rounded))
                    binding.rdoAnalytics.setTextColor(resources.getColor(R.color.white))
                }
            }
            // (activity as HomeActivity?)?.loadFragment(fragment, R.id.tab_container)
//            childFragmentManager.beginTransaction().replace(R.id.tab_container, fragment).commit()
//            childFragmentManager.executePendingTransactions()
            showFragment(fragment)
        }
        setData()
    }
    fun showFragment(fragment: Fragment) {
        val childManager = childFragmentManager
        val transaction = childManager.beginTransaction()

        // Hide all other fragments
        for (frag in childManager.fragments) {
            if (frag.isVisible) transaction.hide(frag)
        }
        val tag = fragment::class.java.simpleName
        // Check if fragment already exists
        var existingFragment = childManager.findFragmentByTag(tag)
        if (existingFragment == null) {
            transaction.add(R.id.tab_container, fragment, tag)
        } else {
            transaction.show(existingFragment)
        }

        transaction.commit()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData() {
        if (selectedSaloon != null) {
            binding.llNoSaloon.visibility = View.GONE
            binding.llSaloonData.visibility = View.VISIBLE
            binding.txtItemName.text = selectedSaloon?.name
            binding.txtLocation.text = selectedSaloon?.location
            binding.txtRating.text = selectedSaloon?.ratings
            var timing = TimeHelper.getCurrentDayTiming(selectedSaloon?.timing)
            binding.txtTiming.text = timing
            if (selectedSaloon?.isOpened == true) {
                binding.llStatus.setBackgroundDrawable(
                    ResourcesCompat.getDrawable(
                        requireContext().resources,
                        R.drawable.rounded_green,
                        requireContext().theme
                    )
                )
                binding.txtStatusTexts.text = "open today"
            } else {
                binding.llStatus.setBackgroundDrawable(
                    ResourcesCompat.getDrawable(
                        requireContext().resources,
                        R.drawable.rounded_red,
                        requireContext().theme
                    )
                )
                binding.txtStatusTexts.text = "close today"
            }
            setViewPager()
        } else {
            binding.llNoSaloon.visibility = View.VISIBLE
            binding.llSaloonData.visibility = View.GONE
        }
    }

    private fun setViewPager() {

        var images = ArrayList<String>()
        for (image in selectedSaloon?.icon!!) {
            images.add(image?.image ?: "")
        }
        if (images.isEmpty()) {
            images.add("")
        }
        // Sample data for ViewPager
        val adapter = ViewPagerAdapter(images)
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
    ) = FragmentSaloonManagerDashboardBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(
            remoteDataSource.buildApi(HomeApi::class.java, requireContext()),
            userPreferences
        )

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
        (activity as HomeActivity?)?.setSystemWindow(true)
        (activity as HomeActivity?)?.showTabs()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
            (activity as HomeActivity?)?.setSystemWindow(true)
            (activity as HomeActivity?)?.showTabs()
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
        (activity as HomeActivity?)?.setSystemWindow(true)
        (activity as HomeActivity?)?.hideTabs()
    }

    fun setFrameLayoutHeight(enablScrool: Boolean) {
        if (enablScrool) {
            val scale = binding.tabContainer.resources.displayMetrics.density
            val heightInPx = (0 * scale + 0.5f).toInt()

            val layoutParams = binding.tabContainer.layoutParams
            layoutParams.height = heightInPx
            binding.tabContainer.layoutParams = layoutParams
        } else {
            val layoutParams = binding.tabContainer.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.tabContainer.layoutParams = layoutParams
        }
    }

}
