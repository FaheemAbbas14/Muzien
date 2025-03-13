package com.tt.muzien.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.data.dto.SubscriptionData
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentManageSubscriptionBinding
import com.tt.muzien.ui.adapters.SubscriptionListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentManageSubscription :
    BaseFragment<HomeViewModel, FragmentManageSubscriptionBinding, HomeRepository>() {
    private val subscriptiopnsList = arrayListOf<SubscriptionData>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        setSubscriptionAdopter()
    }

    private fun setSubscriptionAdopter() {
        subscriptiopnsList.clear()
        for (i in 1..10) {
            println("Index: $i")
            subscriptiopnsList.add(
                SubscriptionData("", "Highbrow Beauty Bar", "12/12/2024", "22/12/2025")
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
        binding.rcySubscriptions.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rcySubscriptions.adapter =
            SubscriptionListAdapter(
                subscriptiopnsList,
                requireContext(),
                clickListener
            )
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentManageSubscriptionBinding.inflate(inflater, container, false)

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