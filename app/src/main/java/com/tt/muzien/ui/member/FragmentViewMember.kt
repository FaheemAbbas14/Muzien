package com.tt.muzien.ui.member

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.R
import com.tt.muzien.data.dto.MemberDto
import com.tt.muzien.data.dto.WorkingHourData
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentViewMemberBinding
import com.tt.muzien.ui.adapters.ServiceGridviewAdapter
import com.tt.muzien.ui.adapters.WorkingHoursAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.ui.saloon.tabs.FragmentAddHoliday
import com.tt.muzien.ui.saloon.tabs.FragmentAddWorkingDay
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentViewMember :
    BaseFragment<HomeViewModel, FragmentViewMemberBinding, HomeRepository>() {
    var member: MemberDto? = null
    private val workingHourrList = arrayListOf<WorkingHourData>()
    private var workingHoursAdopter: WorkingHoursAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.imgMenu.setOnClickListener {
            showCustomMenu(binding.imgMenu)
        }
        binding.imgAddHoliday.setOnClickListener {
            var nextFragment = FragmentAddHoliday()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.imgAddWorkingHour.setOnClickListener {
            var nextFragment = FragmentAddWorkingDay()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        setData()
    }

    private fun showCustomMenu(anchor: View) {
        // Inflate the custom menu layout
        val inflater = LayoutInflater.from(requireActivity())
        val menuView = inflater.inflate(R.layout.invite_layout, null)

        // Initialize the PopupWindow
        val popupWindow = PopupWindow(
            menuView,
            ViewGroup.LayoutParams.WRAP_CONTENT, // Width matches the anchor view width
            ViewGroup.LayoutParams.WRAP_CONTENT, // Height wraps the content
            true // Focusable to handle clicks outside the menu
        )

        // Set click listeners for menu options
        val invite_salon: TextView = menuView.findViewById(R.id.invite_salon)
        val invite_user: TextView = menuView.findViewById(R.id.invite_user)
        val delete_user: TextView = menuView.findViewById(R.id.delete_user)

        invite_salon.setOnClickListener {
            var nextFragment = FragmentInvite()
            nextFragment.type = "Saloon"
            (activity as HomeActivity?)?.loadFragment(nextFragment)
            // Handle Option 1 click
            popupWindow.dismiss()
        }

        invite_user.setOnClickListener {
            var nextFragment = FragmentInvite()
            nextFragment.type = "User"
            (activity as HomeActivity?)?.loadFragment(nextFragment)
            // Handle Option 2 click
            popupWindow.dismiss()
        }

        delete_user.setOnClickListener {
            // Handle Option 3 click
            popupWindow.dismiss()
        }

        // Show the PopupWindow below the anchor view
        popupWindow.showAsDropDown(anchor, 0, 10) // Adjust offset as needed
    }

    private fun setData() {
        setWorkingHourAdopter()
        setServicesAdapter()
    }

    private fun setServicesAdapter() {

        // Sample data
        val items =
            listOf<String>("Hair Stylist", "Threading", "Facials", "Nails", "Waxing", "Massage")

        // Set adapter
        binding.gridView.adapter = ServiceGridviewAdapter(items, requireContext())
    }

    private fun setWorkingHourAdopter() {
        workingHourrList.clear()
        workingHourrList.add(WorkingHourData("1", "Saturday - Thursday", "10:00 AM - 11:00 PM"))
        workingHourrList.add(WorkingHourData("2", "Friday", "02:00 AM - 11:00 PM"))
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
                workingHourrList.removeAt(position)
                workingHoursAdopter?.notifyDataSetChanged()
            }
        }
        binding.rcyWorkingHours.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        workingHoursAdopter = WorkingHoursAdapter(
            workingHourrList,
            requireContext(),
            clickListener
        )
        binding.rcyWorkingHours.adapter = workingHoursAdopter


    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentViewMemberBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java,requireContext()), userPreferences)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.changeStatusBarColor(requireActivity().resources.getColor(R.color.colorPrimary))
        (activity as HomeActivity?)?.hideTabs()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.changeStatusBarColor(requireActivity().resources.getColor(R.color.white))
        (activity as HomeActivity?)?.showTabs()
    }
}