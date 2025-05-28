package com.tt.muzien.ui.profile

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.tt.muzien.R
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentAddAdminServicesBinding
import com.tt.muzien.ui.adapters.HolidayListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.enable
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentAddAdminServices :
    BaseFragment<HomeViewModel, FragmentAddAdminServicesBinding, HomeRepository>() {
    private val services = arrayListOf<String>()
    private var holidayListAdapter: HolidayListAdapter? = null
    private var selectedService: String? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setServicesAdopter()
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.llSave.enable(false)
        binding.llSave.setOnClickListener {
            services.add(selectedService ?: "")
            holidayListAdapter?.notifyDataSetChanged()
            checkServices()
        }
        binding.txtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    selectedService = s.toString()
                    binding.llSave.enable(true)
                } else {
                    binding.llSave.enable(false)
                }
                //checkValidation()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        checkServices()
    }

    private fun checkServices() {
        if (services.size > 0) {
            binding.rcyServices.visibility = View.VISIBLE
            binding.txtNoServices.visibility = View.GONE
        } else {
            binding.rcyServices.visibility = View.GONE
            binding.txtNoServices.visibility = View.VISIBLE
        }
    }

    private fun setServicesAdopter() {

        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
                services.removeAt(position)
                holidayListAdapter?.notifyDataSetChanged()
                checkServices()
            }
        }
        // Set up the FlexboxLayoutManager
        val flexboxLayoutManager = FlexboxLayoutManager(requireActivity()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START // Items align to the start
        }
        binding.rcyServices.layoutManager = flexboxLayoutManager
        holidayListAdapter = HolidayListAdapter(
            services, true, clickListener
        )
        binding.rcyServices.adapter = holidayListAdapter


    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentAddAdminServicesBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java,requireContext()), userPreferences)

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.hideTabs()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as HomeActivity?)?.hideTabs()
        }
    }
    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.showTabs()
    }

}