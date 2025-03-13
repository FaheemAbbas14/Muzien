package com.tt.muzien.ui.service

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.data.dto.ServiceInfo
import com.tt.muzien.data.dto.ServiceSaloon
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentUpdateServiceBinding
import com.tt.muzien.ui.adapters.ServiceUpdateListAdopter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.utilities.FilterSelection


class FragmentUpdateService :
    BaseFragment<HomeViewModel, FragmentUpdateServiceBinding, HomeRepository>() {
    var service: ServiceInfo? = null
    var category: String? = null
    private val salonList = arrayListOf<ServiceSaloon>()
    var adapter: ServiceUpdateListAdopter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgBack.setOnClickListener {
            FilterSelection.filterData = null
            (activity as HomeActivity?)?.popFragment()
        }
        setData()

    }

    private fun setData() {
        binding.txtItemName.text = service?.name
        binding.txtItemCategory.text = "Category: $category"
        binding.txtItemDuration.text = "${service?.duration}  |  ${service?.rate}"
        setSaloonsAdapter()
    }

    private fun setSaloonsAdapter() {
        salonList.add(
            ServiceSaloon(
                "Salon A",
                "Rd. 2121 Alemal Dist. 12643 Riyadh SA",
                "45 mins",
                "SAR",
                "10",
                false
            )
        )
        salonList.add(
            ServiceSaloon(
                "Salon B",
                "Rd. 2121 Alemal Dist. 12643 Riyadh SA",
                "45 mins",
                "SAR",
                "10",
                false
            )
        )
        salonList.add(
            ServiceSaloon(
                "Salon C",
                "Rd. 2121 Alemal Dist. 12643 Riyadh SA",
                "45 mins",
                "SAR",
                "10",
                false
            )
        )

        adapter = ServiceUpdateListAdopter(salonList) { position, isEnabled ->
            salonList[position].isEnabled = isEnabled
            adapter?.notifyDataSetChanged()

        }

        binding.rcySaloons.layoutManager = LinearLayoutManager(requireActivity())
        binding.rcySaloons.adapter = adapter
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentUpdateServiceBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java,requireContext()), userPreferences)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.setSystemWindow(false)
        (activity as HomeActivity?)?.changeStatusBarColor(Color.TRANSPARENT)
        (activity as HomeActivity?)?.hideTabs()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.setSystemWindow(true)
        (activity as HomeActivity?)?.changeStatusBarColor(Color.WHITE)
        (activity as HomeActivity?)?.showTabs()
    }
}