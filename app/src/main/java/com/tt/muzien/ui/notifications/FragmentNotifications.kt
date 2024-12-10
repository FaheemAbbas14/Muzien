package com.tt.muzien.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.R
import com.tt.muzien.data.dto.NotificationDto
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentNotificationsBinding
import com.tt.muzien.enums.EnumNotificationType
import com.tt.muzien.ui.adopters.NotificationListAdopter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentNotifications :
    BaseFragment<HomeViewModel, FragmentNotificationsBinding, HomeRepository>() {
    private val notificationList = arrayListOf<NotificationDto>()

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
                    "Heading $i",
                    "Description $i", EnumNotificationType.Category, false
                )

            )
        }
        for (i in 1..10) {
            println("Index: $i")
            notificationList.add(
                NotificationDto(
                    "https://graph.facebook.com/580534664534485/picture?type=large",
                    null,
                    "Heading $i",
                    "Description $i", EnumNotificationType.Plan, true
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
            NotificationListAdopter(
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