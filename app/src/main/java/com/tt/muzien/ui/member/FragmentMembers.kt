package com.tt.muzien.ui.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.data.dto.MemberDto
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.databinding.FragmentMembersBinding
import com.tt.muzien.ui.adopters.MembersListAdopter
import com.tt.muzien.ui.auth.AuthViewModel
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.FragmentFilter
import com.tt.muzien.ui.home.HomeActivity
import com.zabihah.ui.ui.interfaces.OnItemClickListner

class FragmentMembers : BaseFragment<AuthViewModel, FragmentMembersBinding, AuthRepository>() {
    private val membersList = arrayListOf<MemberDto>()
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
            MembersListAdopter(
                membersList,
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
    ) = FragmentMembersBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        AuthRepository(remoteDataSource.buildApi(AuthApi::class.java), userPreferences)


}