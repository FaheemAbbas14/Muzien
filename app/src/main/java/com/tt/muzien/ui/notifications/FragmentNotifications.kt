package com.tt.muzien.ui.notifications

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.R
import com.tt.muzien.data.dto.NotificationDto
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentNotificationsBinding
import com.tt.muzien.enums.EnumNotificationType
import com.tt.muzien.ui.adapters.NotificationListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentNotifications :
    BaseFragment<HomeViewModel, FragmentNotificationsBinding, HomeRepository>() {
    private val notificationList = arrayListOf<NotificationDto>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        setNotificationAdopter()
    }


    private fun setNotificationAdopter() {
        notificationList.clear()
        for (i in 1..3) {
            println("Index: $i")
            notificationList.add(
                NotificationDto(
                    null,
                    R.drawable.salon_icon,
                    "Salons",
                    "Salon “All Ways Hair” has been approved", EnumNotificationType.Category, false
                )

            )
        }
        for (i in 1..10) {
            println("Index: $i")
            notificationList.add(
                NotificationDto(
                    "",
                    null,
                    "Booking Pending Approval",
                    "Customer: David Garcia\n" +
                            "Salon: The Style Zone", EnumNotificationType.Plan, true
                )

            )
        }
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
//                var nextFragment = FragmentPlaceDetails()
//                nextFragment.itemId = featuredItemsList[position].id
//                nextFragment.placeType = EnumItemListType.Featured
//                (activity as DashboardActivity?)?.loadFragment(nextFragment)

            }
        }
        binding.rcyNotifications.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rcyNotifications.adapter =
            NotificationListAdapter(
                notificationList,
                requireContext(),
                clickListener,
            )
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNotificationsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java,requireContext()), userPreferences)

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.hideTabs()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as HomeActivity?)?.hideTabs()
        }
    }
    override fun onPause() {
        super.onPause()
       // (activity as HomeActivity?)?.showTabs()
    }
}