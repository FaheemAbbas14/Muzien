package com.tt.muzien.ui.service

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.tt.muzien.data.dto.ServiceInfo
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.databinding.FragmentServicesBinding
import com.tt.muzien.ui.adapters.ExpandServiceListAdapter
import com.tt.muzien.ui.auth.AuthViewModel
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.FragmentFilter
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.utilities.FilterSelection


class FragmentServices : BaseFragment<AuthViewModel, FragmentServicesBinding, AuthRepository>() {
    private val servicesMap = HashMap<String, List<ServiceInfo>>()
    private var fromDate: String = ""
    private var toDate: String = ""
    private var bookingDuration: String = ""

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setServicesAdopter()
        binding.imgFilter.setOnClickListener {
            var nextFragment = FragmentFilter()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        if (FilterSelection.filterData!=null){
            val selection = FilterSelection.filterData!!.selection
            val fromDateFilter = FilterSelection.filterData!!.from
            val toDateFilter =FilterSelection.filterData!!.to
            if (selection != "") {
                bookingDuration = selection.toString()
                fromDate = fromDateFilter.toString()
                toDate = toDateFilter.toString()
                setServicesAdopter()
            }

        }
    }

    private fun setServicesAdopter() {
        servicesMap.clear()
        val groupTitles = listOf("Hair Service", "Facial", "Nails", "Manicure", "Massage")
        val groupServices = listOf("30", "10", "5", "8", "100")
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
        val adapter = ExpandServiceListAdapter(requireContext(), groupTitles, servicesMap,true,groupServices )
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

    override fun getViewModel(): Class<AuthViewModel> {
        return AuthViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentServicesBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        AuthRepository(remoteDataSource.buildApi(AuthApi::class.java), userPreferences)


}