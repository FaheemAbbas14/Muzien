package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import com.tt.muzien.data.dto.ServiceInfo
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentSaloonServicesBinding
import com.tt.muzien.ui.adapters.ExpandServiceListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.ui.service.FragmentUpdateService
import com.tt.muzien.utilities.Helper


class FragmentSaloonServices :
    BaseFragment<HomeViewModel, FragmentSaloonServicesBinding, HomeRepository>() {
    private val servicesMap = HashMap<String, List<ServiceInfo>>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setServicesAdopter()
        binding.llAdd.setOnClickListener {
            var nextFragment = FragmentAddSaloonServices()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
    }

    private fun setServicesAdopter() {
        servicesMap.clear()
        val groupTitles = listOf("Hair Service", "Facial", "Nails", "Manicure", "Massage",)
        for (type in groupTitles) {
            val servicesList = arrayListOf<ServiceInfo>()
            for (i in 1..4) {
                println("Index: $i")
                servicesList.add(
                    ServiceInfo("", "Buzz cut", "Duration: 45mins", "SAR 10")
                )
            }
            servicesMap.put(type, servicesList)
        }
        val adapter = ExpandServiceListAdapter(requireContext(), groupTitles, servicesMap)
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
            nextFragment.service=child
            nextFragment.category=group
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
            totalHeight += Helper.dpToPx(requireContext(),50)

            if (listView.isGroupExpanded(i)) {
                for (j in 0 until adapter.getChildrenCount(i)) {
                    val childItem = adapter.getChildView(i, j, false, null, listView)
                    childItem.measure(0, 0)
                    Log.d("ExpandableListView", "child height: ${childItem.measuredHeight}")
                    totalHeight += Helper.dpToPx(requireContext(),115)
                }
            }
        }
        Log.d("ExpandableListView", "Calculated height: $totalHeight")
        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (adapter.groupCount - 1))
        listView.layoutParams = params
    }
    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSaloonServicesBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java), userPreferences)

}