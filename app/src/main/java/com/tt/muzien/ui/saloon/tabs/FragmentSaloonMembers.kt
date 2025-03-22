package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.data.dto.MemberDto
import com.tt.muzien.data.dto.SaloonDto
import com.tt.muzien.data.network.MemberApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.MemberRepository
import com.tt.muzien.databinding.FragmentSaloonMembersBinding
import com.tt.muzien.ui.adapters.MembersListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.FragmentFilter
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.member.MemberViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.FilterSelection
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentSaloonMembers :
    BaseFragment<MemberViewModel, FragmentSaloonMembersBinding, MemberRepository>() {
    private val membersList = arrayListOf<MemberDto>()
    private var fromDate: String = ""
    private var toDate: String = ""
    private var bookingDuration: String = ""
    var selectedSaloon: SaloonDto? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getMembers()
        binding.imgFilter.setOnClickListener {
            var nextFragment = FragmentFilter()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.llAdd.setOnClickListener {
            var nextFragment = FragmentAddSaloonMember()
            nextFragment.saloonId= selectedSaloon?.id!!
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
                getMembers()
            }

        }
    }

    private fun setMemberAdopter() {
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

    override fun getViewModel(): Class<MemberViewModel> {
        return MemberViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSaloonMembersBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        MemberRepository(remoteDataSource.buildApi(MemberApi::class.java, requireContext()))

    private fun getMembers() {
        viewModel.getMembers.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        membersList.clear()
//                        for (saloon in it.value.data.saloons) {
//                            println("Index: $i")
//                            membersList.add(
//                                MemberDto(
//                                    "",
//                                    if (i % 2 == 0) true else false,
//                                    "Jennifer Austin",
//                                    "Hair Stylist",
//                                    "4.1 (50 reviews)",
//                                    "Store  Tye Style Zone",
//                                    4
//                                )
//                            )
//
//                        }
                        setMemberAdopter()
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
        viewModel.getMembers(selectedSaloon?.id.toString())
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
}