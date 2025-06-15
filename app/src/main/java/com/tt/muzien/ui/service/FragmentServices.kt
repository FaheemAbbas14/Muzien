package com.tt.muzien.ui.service

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tt.muzien.data.dto.ServiceInfo
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.ServiceApi
import com.tt.muzien.data.repository.ServiceRepository
import com.tt.muzien.databinding.FragmentServicesBinding
import com.tt.muzien.enums.EnumTabSelection
import com.tt.muzien.ui.adapters.ExpandServiceListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.FragmentFilter
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.utilities.Appelement
import com.tt.muzien.utilities.FilterSelection


class FragmentServices :
    BaseFragment<ServiceViewModel, FragmentServicesBinding, ServiceRepository>() {
    private val servicesMap = HashMap<String, List<ServiceInfo>>()
    private var fromDate: String = ""
    private var toDate: String = ""
    private var bookingDuration: String = ""
    private val groupTitles = arrayListOf<String>()
    private val groupServices = arrayListOf<String>()
    private var totalServices = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setServicesAdopter()
        binding.swipeRefresh.listView = binding.rcyServices
        binding.swipeRefresh.setOnRefreshListener {
            //page=1
            getCategories(true)
        }
        binding.imgFilter.setOnClickListener {
            var nextFragment = FragmentFilter()
            nextFragment.enumTabSelection= EnumTabSelection.Services
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        if (FilterSelection.filterData != null) {
            val selection = FilterSelection.filterData!!.selection
            val fromDateFilter = FilterSelection.filterData!!.from
            val toDateFilter = FilterSelection.filterData!!.to
            if (selection != "") {
                bookingDuration = selection.toString()
                fromDate = fromDateFilter.toString()
                toDate = toDateFilter.toString()
                setServicesAdopter()
            }

        }
        getCategories(false)
    }

    private fun setServicesAdopter() {
        if (totalServices > 0) {
            binding.cnstData.visibility = View.VISIBLE
            binding.llNoDta.visibility = View.GONE
        } else {
            binding.cnstData.visibility = View.GONE
            binding.llNoDta.visibility = View.VISIBLE
        }
        val adapter = ExpandServiceListAdapter(
            requireContext(),
            groupTitles,
            servicesMap,
            true,
            groupServices
        )
        binding.rcyServices.setAdapter(adapter)
        // Handle child clicks
        binding.rcyServices.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            val group = groupTitles[groupPosition]
            val child = servicesMap[group]?.get(childPosition)
            var nextFragment = FragmentUpdateService()
            nextFragment.service = child
            nextFragment.category = group
            (activity as HomeActivity?)?.loadFragment(nextFragment)
            true
        }

        // Handle group expansion
        binding.rcyServices.setOnGroupExpandListener { groupPosition ->
//            Toast.makeText(
//                requireContext(),
//                "Expanded: ${groupTitles[groupPosition]}",
//                Toast.LENGTH_SHORT
//            ).show()
        }

    }

    override fun getViewModel(): Class<ServiceViewModel> {
        return ServiceViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentServicesBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        ServiceRepository(remoteDataSource.buildApi(ServiceApi::class.java, requireContext()))

    private fun getCategories(reload: Boolean?=false) {
        viewModel.getServices.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        servicesMap.clear()
                        groupServices.clear()
                        groupTitles.clear()
                        totalServices = 0
                        for (category in it.value.data) {
                            groupTitles.add(category.name)
                            groupServices.add("${category.services.size}")
                            val servicesList = arrayListOf<ServiceInfo>()
                            for (service in category.services) {
                                servicesList.add(
                                    ServiceInfo(
                                        service.id.toInt(),
                                        service.image,
                                        service.name,
                                        "Duration: ${service.duration} ${
                                            if (service.duration.toInt() == 1) "min" else "mins"
                                        }",
                                        "SAR ${service.price / 100}",
                                        service
                                    )
                                )
                            }
                            totalServices += servicesList.size
                            servicesMap.put(category.name, servicesList)

                        }
                    }
                    setServicesAdopter()
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())
                    setServicesAdopter()
                    binding.swipeRefresh.isRefreshing = false
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.getServices()
        if (!reload!!) {
            (activity as HomeActivity?)?.showLoadingIndicator()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        (activity as HomeActivity?)?.showTabs()
        if (!hidden) {
           if (Appelement.reload){
               Appelement.reload=false
               getCategories(false)
           }
        }
    }
}