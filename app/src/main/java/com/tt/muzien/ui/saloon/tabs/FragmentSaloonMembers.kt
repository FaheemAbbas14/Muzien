package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.data.dto.MemberDto
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentSaloonMembersBinding
import com.tt.muzien.ui.adapters.MembersListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.FragmentFilter
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.utilities.FilterSelection
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentSaloonMembers :
    BaseFragment<HomeViewModel, FragmentSaloonMembersBinding, HomeRepository>() {
    private val membersList = arrayListOf<MemberDto>()
    private var fromDate: String = ""
    private var toDate: String = ""
    private var bookingDuration: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMemberAdopter()
        binding.imgFilter.setOnClickListener {
            var nextFragment = FragmentFilter()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.llAdd.setOnClickListener {
            var nextFragment = FragmentAddSaloonMember()
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
                setMemberAdopter()
            }

        }
    }

    private fun setMemberAdopter() {
        membersList.clear()
        for (i in 1..10) {
            println("Index: $i")
            membersList.add(
                MemberDto(
                    "",
                    if (i % 2 == 0) true else false,
                    "Jennifer Austin",
                    "Hair Stylist",
                    "4.1 (50 reviews)",
                    "Store  Tye Style Zone",
                    4
                )
            )
        }
        binding.txtHeading.text = "Members(${membersList.size})"
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
//                var nextFragment = FragmentPlaceDetails()
//                nextFragment.itemId = featuredItemsList[position].id
//                nextFragment.placeType = EnumItemListType.Featured
//                (activity as DashboardActivity?)?.loadFragment(nextFragment)

            }
        }
        binding.rcyMembers.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rcyMembers.adapter =
            MembersListAdapter(
                membersList,
                requireContext(),
                clickListener, true
            )
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSaloonMembersBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java), userPreferences)

}