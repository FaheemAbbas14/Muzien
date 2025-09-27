package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.R
import com.tt.muzien.data.dto.MemberDto
import com.tt.muzien.data.dto.SaloonDto
import com.tt.muzien.data.network.MemberApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.MemberRepository
import com.tt.muzien.databinding.FragmentSaloonMembersBinding
import com.tt.muzien.enums.EnumTabSelection
import com.tt.muzien.interfaces.OnStateChange
import com.tt.muzien.ui.adapters.MembersListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.FragmentFilter
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.member.FragmentViewMember
import com.tt.muzien.ui.member.MemberViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.Appelement
import com.tt.muzien.utilities.FilterSelection
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentSaloonMembers :
    BaseFragment<MemberViewModel, FragmentSaloonMembersBinding, MemberRepository>() {
    private val membersList = arrayListOf<MemberDto>()
    private var fromDate: String = ""
    private var toDate: String = ""
    private var bookingDuration: String = ""
    var selectedSaloon: SaloonDto? = null
    private var isActive: Boolean? = null
    private var status: String? = null
    private var tag: String? = null
    private var filterApplied: Boolean = false
    var rolesMap = HashMap<String, String>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rolesMap.put("salon-manager","Manager")
        rolesMap.put("service-provider","Service Provider")
        tag=resources.getString(R.string.members)
        getMembers()
        binding.imgFilter.setOnClickListener {
            if (filterApplied) {
                binding.imgFilter.setImageResource(
                    R.drawable.filter_icon
                )
                tag=resources.getString(R.string.members)
                status = null
                getMembers(false)
                filterApplied=false
            } else {

                var nextFragment = FragmentFilter()
                nextFragment.status = true
                nextFragment.enumTabSelection = EnumTabSelection.Member
                (activity as HomeActivity?)?.loadFragment(nextFragment)
            }

        }
        binding.llAdd.setOnClickListener {
            var nextFragment = FragmentAddSaloonMember()
            nextFragment.saloonId = selectedSaloon?.id!!
            (activity as HomeActivity?)?.loadFragment(nextFragment,true)
        }

//        binding.swipeRefresh.setOnRefreshListener {
//            binding.swipeRefresh.isRefreshing = false
//            // page=1
//            getMembers()
//        }
    }

    private fun setMemberAdopter() {
        if (membersList.isNotEmpty()) {
            binding.cnstData.visibility = View.VISIBLE
            binding.llNoDta.visibility = View.GONE
        } else {
            binding.cnstData.visibility = View.GONE
            binding.llNoDta.visibility = View.VISIBLE
        }
        binding.txtHeading.text =
            "$tag(${membersList.size})"
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
                var nextFragment = FragmentViewMember()
                nextFragment.member = membersList[position]
                nextFragment.isFromSaloon = true
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
                clickListener, stateChangeListener, true
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

    private fun getMembers(reload: Boolean? = false) {
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
                                    member.User.picture ?: "",
                                    member.isActive,
                                    member.User.fullName ?: "Name",
                                    rolesMap[member.User.role]!!,
                                    "${member.tRating} (${member.numReviews} ${
                                        if (member.numReviews.toInt() == 1) "review" else "reviews"
                                    })",
                                    member.Saloon.name,
                                    member.todayBookings,
                                    member.isAdmin,
                                    member.isMember
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
        viewModel.getMembers(selectedSaloon?.id.toString(), isActive, if (status != null) status!!.toInt() else null)
        if (reload == false) {
            (activity as HomeActivity?)?.showLoadingIndicator()
        }
    }

    private fun inActiveMember(id: Int) {
        viewModel.inActiveMember.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    //(activity as HomeActivity?)?.hideLoadingIndicator()
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
                    if (it.value.status != 0){
                        Log.d("response", "success " + it.toString())
                        // (activity as HomeActivity?)?.hideLoadingIndicator()
                        requireView().snackbar("Member marked manager successfully")
                        //  (activity as HomeActivity?)?.popFragment()
                        getMembers(false)
                    }
                    else{
                        (activity as HomeActivity?)?.hideLoadingIndicator()
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
        viewModel.makeManager(id)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            if (Appelement.reload) {
                Appelement.reload = false
                if (FilterSelection.filterData != null) {
                    if (FilterSelection.filterData!!.status != null && FilterSelection.filterData!!.status != "") {
                        if (FilterSelection.filterData!!.status == "Active") {
                            tag="Active members"
                            isActive = true
                            status = null
                        } else if (FilterSelection.filterData!!.status == "InActive") {
                            tag="Inactive members"
                            isActive = false
                            status = null
                        } else {
                            status = FilterSelection.filterData!!.status
                            if (status=="0"){
                                tag=resources.getString(R.string.invitation_sent)
                            }
                            else if (status=="1"){
                                tag=resources.getString(R.string.working_today)
                            }
                            else if (status=="2"){
                                tag=resources.getString(R.string.on_leave_today)
                            }

                            isActive = null
                        }
                        getMembers(false)
                    }


                } else {
                    getMembers(false)
                }

            }
        }
    }
}