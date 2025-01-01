package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.tt.muzien.data.dto.ServiceInfo
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentSaloonServicesBinding
import com.tt.muzien.ui.adapters.ExpandServiceListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel


class FragmentSaloonServices :
    BaseFragment<HomeViewModel, FragmentSaloonServicesBinding, HomeRepository>() {
    private val servicesMap = HashMap<String, List<ServiceInfo>>()

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setServicesAdopter()
        binding.llAdd.setOnClickListener {
            var nextFragment = FragmentAddSaloonServices()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
    }

    private fun setServicesAdopter() {
        servicesMap.clear()
        val groupTitles = listOf("Hair Service", "Facial", "Nails", "Manicure", "Massage")
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

        // Handle child clicks
        binding.rcyServices.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            val group = groupTitles[groupPosition]
            val child = servicesMap[group]?.get(childPosition)
//            Toast.makeText(requireContext(), "Selected: $child in $group", Toast.LENGTH_SHORT)
//                .show()
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