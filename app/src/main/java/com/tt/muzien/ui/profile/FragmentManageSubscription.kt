package com.tt.muzien.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.data.dto.SubscriptionData
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.SaloonApi
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.databinding.FragmentManageSubscriptionBinding
import com.tt.muzien.ui.adapters.SubscriptionListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.saloon.SaloonViewModel
import com.tt.muzien.ui.snackbar
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentManageSubscription :
    BaseFragment<SaloonViewModel, FragmentManageSubscriptionBinding, SaloonRepository>() {
    private val subscriptiopnsList = arrayListOf<SubscriptionData>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        getSaloonsSubscriptions()
    }

    private fun setSubscriptionAdopter() {

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

    override fun getViewModel(): Class<SaloonViewModel> {
        return SaloonViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentManageSubscriptionBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        SaloonRepository(remoteDataSource.buildApi(SaloonApi::class.java, requireContext()))

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.hideTabs()
    }

    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.showTabs()
    }

    private fun getSaloonsSubscriptions() {
        viewModel.getSaloonsSubscriptions.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        subscriptiopnsList.clear()
                        for (subscription in it.value.data.items) {
                            subscriptiopnsList.add(
                                SubscriptionData(subscription.id.toString(), subscription.Saloon.name, subscription.startDate, subscription.validTill)
                            )
                        }

                        setSubscriptionAdopter()
                       // requireView().snackbar("Subscription added successfully")
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


        viewModel.getSaloonsSubscriptions()
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

}