package com.tt.muzien.ui.service

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.data.dto.ServiceDto
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.databinding.FragmentServicesBinding
import com.tt.muzien.ui.adopters.ServiceListAdopter
import com.tt.muzien.ui.auth.AuthViewModel
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.FragmentFilter
import com.tt.muzien.ui.home.HomeActivity
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentServices : BaseFragment<AuthViewModel, FragmentServicesBinding, AuthRepository>() {
    private val servicesList = arrayListOf<ServiceDto>()
    private var fromDate: String = ""
    private var toDate: String = ""
    private var bookingDuration: String = ""

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setMemberAdopter()
        binding.imgFilter.setOnClickListener {
            var nextFragment = FragmentFilter()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        setFragmentResultListener("requestKey") { key, bundle ->
            val selection = bundle.getString("selection")
            val fromDateFilter = bundle.getString("fromDate")
            val toDateFilter = bundle.getString("toDate")
            if (selection != "") {
                bookingDuration = selection.toString()
                fromDate = fromDateFilter.toString()
                toDate = toDateFilter.toString()
                setMemberAdopter()
            }

        }
    }

    private fun setMemberAdopter() {
        servicesList.clear()
        for (i in 1..10) {
            println("Index: $i")
            servicesList.add(
                ServiceDto("Service $i", i * 5)
            )
        }
        //  binding.txtHeading.text = "Members(${membersList.size})"
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
//                var nextFragment = FragmentPlaceDetails()
//                nextFragment.itemId = featuredItemsList[position].id
//                nextFragment.placeType = EnumItemListType.Featured
//                (activity as DashboardActivity?)?.loadFragment(nextFragment)

            }
        }
        binding.rcyServices.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rcyServices.adapter =
            ServiceListAdopter(
                servicesList,
                requireContext(),
                clickListener
            )
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