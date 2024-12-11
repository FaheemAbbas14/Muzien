package com.tt.muzien.ui.saloon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.data.dto.SaloonDto
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.databinding.FragmentSaloonBinding
import com.tt.muzien.ui.adopters.SaloonListAdopter
import com.tt.muzien.ui.auth.AuthViewModel
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.FragmentFilter
import com.tt.muzien.ui.home.HomeActivity
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentSaloon : BaseFragment<AuthViewModel, FragmentSaloonBinding, AuthRepository>() {
    private val saloonsList = arrayListOf<SaloonDto>()
    private var fromDate: String = ""
    private var toDate: String = ""
    private var bookingDuration: String = ""
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setSaloonAdopter()
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
                setSaloonAdopter()
            }

        }
    }

    private fun setSaloonAdopter() {
        saloonsList.clear()
        for (i in 0..10) {
            println("Index: $i")
            saloonsList.add(
                SaloonDto(
                    "",
                    "The Style Zone",
                    if (i % 2 == 0) true else false,
                    "Rd. 2121 Alamal Dist. 12643 Riyadh SA",
                    "4.5 (2398 reviews)",
                    "10:00 AM - 11:00 PM"
                )
            )
        }
        binding.txtHeading.text = "Salons(${saloonsList.size})"
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
//                var nextFragment = FragmentPlaceDetails()
//                nextFragment.itemId = featuredItemsList[position].id
//                nextFragment.placeType = EnumItemListType.Featured
//                (activity as DashboardActivity?)?.loadFragment(nextFragment)

            }
        }
        binding.rcySaloons.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rcySaloons.adapter =
            SaloonListAdopter(
                saloonsList,
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
    ) = FragmentSaloonBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        AuthRepository(remoteDataSource.buildApi(AuthApi::class.java), userPreferences)


}