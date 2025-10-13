package com.tt.muzien.ui.notifications

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.R
import com.tt.muzien.data.dto.NotificationDto
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentNotificationsByTypeBinding
import com.tt.muzien.enums.EnumNotificationType
import com.tt.muzien.ui.adapters.NotificationListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.bookings.FragmentBookingDetails
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.ui.saloon.FragmentSaloonDetails
import com.tt.muzien.ui.snackbar
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentNotificationsByType :
    BaseFragment<HomeViewModel, FragmentNotificationsByTypeBinding, HomeRepository>() {
    private val notificationList = arrayListOf<NotificationDto>()
    var types: String? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setUserRepo((activity as HomeActivity?)?.getUserRepo()!!)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        getNotifications()
    }

    private fun getNotifications() {
        viewModel.getNotifications.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        for (item in it.value.data.items) {
                            notificationList.add(
                                NotificationDto(
                                    item.image,
                                    R.drawable.salon_icon,
                                    item.type,
                                    item.title,
                                    item.message,
                                    if (item.image != null && item.image != "") EnumNotificationType.Plan else EnumNotificationType.Category,
                                    if (item.notifType == 0) false else true,
                                    item.itemId
                                )
                            )

                        }
                        setNotificationAdopter()
                    } else {
                        requireView().snackbar(it.value.message)
                    }
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())

                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.getNotificationsByType(types ?: "")
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun setNotificationAdopter() {
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
                if (notificationList.get(position).type.contains("item")) {
                    if (notificationList.get(position).type == "saloon-item") {
                        var nextFragment = FragmentSaloonDetails()
                        nextFragment.saloonId = notificationList.get(position).itemId?.toInt()
                        (activity as HomeActivity?)?.loadFragment(nextFragment)
                        //saloon details
                    } else if (notificationList.get(position).type == "invite-item") {
                        //invites details
                    } else if (notificationList.get(position).type == "booking-item" || notificationList.get(position).type == "booking-reminder-item") {
                        //booking details
                        var nextFragment = FragmentBookingDetails()
                        nextFragment.bookingId = notificationList.get(position).itemId.toString()
                        (activity as HomeActivity?)?.loadFragment(nextFragment)
                    }


                } else {
                    var nextFragment = FragmentNotificationsByType()
                    nextFragment.types = notificationList.get(position).type
                    (activity as HomeActivity?)?.loadFragment(nextFragment)
                }

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
        container: ViewGroup?,
    ) = FragmentNotificationsByTypeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(
            remoteDataSource.buildApi(HomeApi::class.java, requireContext()),
            userPreferences
        )

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
        (activity as HomeActivity?)?.showTabs()
    }
}