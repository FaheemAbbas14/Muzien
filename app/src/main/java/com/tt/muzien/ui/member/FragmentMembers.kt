package com.tt.muzien.ui.member

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.data.dto.MemberDto
import com.tt.muzien.data.network.MemberApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.MemberRepository
import com.tt.muzien.databinding.FragmentMembersBinding
import com.tt.muzien.interfaces.OnStateChange
import com.tt.muzien.ui.adapters.MembersListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.FragmentFilter
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.FilterSelection
import com.zabihah.ui.ui.interfaces.OnItemClickListner

class FragmentMembers : BaseFragment<MemberViewModel, FragmentMembersBinding, MemberRepository>() {
    private val membersList = arrayListOf<MemberDto>()
    private var fromDate: String = ""
    private var toDate: String = ""
    private var bookingDuration: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setSaloonRepo((activity as HomeActivity?)?.getSaloonRepo()!!)
        getMembers()
        binding.imgFilter.setOnClickListener {
            var nextFragment = FragmentFilter()
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
        if (membersList.isNotEmpty()) {
            binding.cnstData.visibility = View.VISIBLE
            binding.llNoDta.visibility = View.GONE
        } else {
            binding.cnstData.visibility = View.GONE
            binding.llNoDta.visibility = View.VISIBLE
        }
        binding.txtHeading.text = "Members(${membersList.size})"
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
                var nextFragment = FragmentViewMember()
                nextFragment.member = membersList[position]
                (activity as HomeActivity?)?.loadFragment(nextFragment)

            }
        }
        val stateChangeListener = object : OnStateChange {

            override fun onStateChange(position: Int, state: Int) {
                if (state == 1) {
                    makeManger(membersList[position].id)
                } else if (state == 2) {
                    inActiveMember(membersList[position].id)
                } else {
                    deleteMember(membersList[position].id)
                }
            }
        }
        binding.rcyMembers.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rcyMembers.adapter =
            MembersListAdapter(
                membersList,
                requireContext(),
                clickListener,
                stateChangeListener
            )
    }

    override fun getViewModel(): Class<MemberViewModel> {
        return MemberViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMembersBinding.inflate(inflater, container, false)

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
                        for (member in it.value.data) {
                            membersList.add(
                                MemberDto(
                                    member.id.toInt(),
                                    member.User.id.toInt(),
                                    member.User.picture,
                                    member.isActive,
                                    member.User.fullName ?: "Name",
                                    "",
                                    "${member.tRating} (${member.numReviews} ${
                                        if (member.numReviews.toInt() == 1) "review" else "reviews"
                                    })",
                                    member.Saloon.name,
                                    member.todayBookings,
                                    member.isAdmin
                                )
                            )
                        }
                        setMemberAdopter()
                    } else {
                        requireView().snackbar(it.value.message)
                    }
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())
                    setMemberAdopter()
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.getMembers()
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun inActiveMember(id: Int) {
        viewModel.inActiveMember.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    // (activity as HomeActivity?)?.hideLoadingIndicator()
                    requireView().snackbar("Member inactive successfully")
                    getMembers()
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())

                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.inActiveMember(id)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun deleteMember(id: Int) {
        viewModel.deleteMember.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    // (activity as HomeActivity?)?.hideLoadingIndicator()
                    requireView().snackbar("Member deleted successfully")
                    //  (activity as HomeActivity?)?.popFragment()
                    getMembers()
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())

                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.deleteMember(id)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun makeManger(id: Int) {
        viewModel.makeManger.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    // (activity as HomeActivity?)?.hideLoadingIndicator()
                    requireView().snackbar("Member marked manager successfully")
                    //  (activity as HomeActivity?)?.popFragment()
                    getMembers()
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())

                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.makeManager(id)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
}