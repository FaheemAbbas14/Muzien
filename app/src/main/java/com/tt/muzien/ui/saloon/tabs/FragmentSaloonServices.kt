package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import com.tt.muzien.data.dto.SaloonDto
import com.tt.muzien.data.dto.ServiceInfo
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.ServiceApi
import com.tt.muzien.data.repository.ServiceRepository
import com.tt.muzien.databinding.FragmentSaloonServicesBinding
import com.tt.muzien.ui.adapters.ExpandServiceListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.service.FragmentUpdateService
import com.tt.muzien.ui.service.ServiceViewModel
import com.tt.muzien.utilities.Helper


class FragmentSaloonServices :
    BaseFragment<ServiceViewModel, FragmentSaloonServicesBinding, ServiceRepository>() {
    private val servicesMap = HashMap<String, List<ServiceInfo>>()
    var saloonId: Int? = 1
    var servicesCount: Int? = 0
    var selectedSaloon: SaloonDto? = null
    private val groupTitles = arrayListOf<String>()
    private val groupServices = arrayListOf<String>()
    private var totalServices = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCategories()
        binding.llAdd.setOnClickListener {
            var nextFragment = FragmentAddSaloonServices()
            nextFragment.saloonId = saloonId
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
    }

    private fun setServicesAdopter() {
        if (totalServices>0) {
            binding.nestedScrollView.visibility = View.VISIBLE
            binding.llNoDta.visibility = View.GONE
        } else {
            binding.nestedScrollView.visibility = View.GONE
            binding.llNoDta.visibility = View.VISIBLE
        }
        val adapter = ExpandServiceListAdapter(requireContext(), groupTitles, servicesMap, true,
            groupServices)
        binding.rcyServices.setAdapter(adapter)
        // Adjust height dynamically
        adjustExpandableListViewHeight(binding.rcyServices)

        // Update height on expand/collapse
        binding.rcyServices.setOnGroupExpandListener {
            adjustExpandableListViewHeight(binding.rcyServices)
        }

        binding.rcyServices.setOnGroupCollapseListener {
            adjustExpandableListViewHeight(binding.rcyServices)
        }
        // Handle child clicks
        binding.rcyServices.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            val group = groupTitles[groupPosition]
            val child = servicesMap[group]?.get(childPosition)
            var nextFragment = FragmentUpdateService()
            nextFragment.service = child
            nextFragment.category = group
            nextFragment.saloonId=saloonId.toString()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
            true
        }

        // Handle group expansion
        binding.rcyServices.setOnGroupExpandListener { groupPosition ->

            adjustExpandableListViewHeight(binding.rcyServices)
            //            Toast.makeText(
//                requireContext(),
//                "Expanded: ${groupTitles[groupPosition]}",
//                Toast.LENGTH_SHORT
//            ).show()
        }

    }

    private fun adjustExpandableListViewHeight(listView: ExpandableListView) {
        val adapter = listView.expandableListAdapter ?: return

        var totalHeight = 0
        for (i in 0 until adapter.groupCount) {
            val groupItem = adapter.getGroupView(i, false, null, listView)
            groupItem.measure(0, 0)
            Log.d("ExpandableListView", "parent height: ${groupItem.measuredHeight}")
            totalHeight += Helper.dpToPx(requireContext(), 45)

            if (listView.isGroupExpanded(i)) {
                for (j in 0 until adapter.getChildrenCount(i)) {
                    val childItem = adapter.getChildView(i, j, false, null, listView)
                    childItem.measure(0, 0)
                    Log.d("ExpandableListView", "child height: ${childItem.measuredHeight}")
                    totalHeight += Helper.dpToPx(requireContext(), 115)
                }
            }
        }
        Log.d("ExpandableListView", "Calculated height: $totalHeight")
        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (adapter.groupCount - 1))
        listView.layoutParams = params
    }

    override fun getViewModel(): Class<ServiceViewModel> {
        return ServiceViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSaloonServicesBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        ServiceRepository(remoteDataSource.buildApi(ServiceApi::class.java, requireContext()))

    private fun getCategories() {
        viewModel.getSaloonCategories.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        servicesMap.clear()
                        groupServices.clear()
                        groupTitles.clear()
                        totalServices=0
                        for (category in it.value.data) {
                            if (category.services.size>0) {
                                groupTitles.add(category.name)
                                groupServices.add("${category.services.size}")
                                val servicesList = arrayListOf<ServiceInfo>()
                                for (service in category.services) {
                                    servicesList.add(
                                        ServiceInfo(
                                            service.id.toInt(),
                                            service.image,
                                            service.name!!,
                                            "Duration: ${service.duration} ${
                                                if (service.duration.toInt() == 1) "min" else "mins"
                                            }",
                                            "SAR ${service.price / 100}"
                                        )
                                    )
                                }
                                totalServices += servicesList.size
                                servicesMap.put(category.name, servicesList)
                            }
                        }
                    }
                    setServicesAdopter()
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())
                    setServicesAdopter()
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.getCategories(selectedSaloon?.id.toString())
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
}