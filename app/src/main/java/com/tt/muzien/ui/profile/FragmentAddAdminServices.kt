package com.tt.muzien.ui.profile

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.tt.muzien.data.dto.LoggedInInfo
import com.tt.muzien.data.network.MemberApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.MemberRepository
import com.tt.muzien.data.requests.AddMemberService
import com.tt.muzien.databinding.FragmentAddAdminServicesBinding
import com.tt.muzien.ui.adapters.HolidayListAdapter
import com.tt.muzien.ui.adapters.ServiceSpinnerAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.enable
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.member.MemberViewModel
import com.tt.muzien.ui.snackbar
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentAddAdminServices :
    BaseFragment<MemberViewModel, FragmentAddAdminServicesBinding, MemberRepository>() {
    private val services = arrayListOf<String>()
    private val userServices = arrayListOf<String>()
    var ServicesMap = HashMap<String, Int>()
    private var holidayListAdapter: HolidayListAdapter? = null
    private var selectedService: String? = null
    var position = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setServiceRepo((activity as HomeActivity?)?.getServiceRepo()!!)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.llSave.enable(false)
        binding.llSave.setOnClickListener {
//            services.add(selectedService ?: "")
//            holidayListAdapter?.notifyDataSetChanged()
//            checkServices()
            if (ServicesMap[selectedService] != null) {
                addService()
            }
        }

        if (LoggedInInfo.user != null && LoggedInInfo.user?.saloonId != null) {
            getServices()
        }
    }

    private fun setServicesListAdaptor() {
        val adapter = ServiceSpinnerAdapter(requireContext(), services,false)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.txtSearch.adapter = adapter
        binding.txtSearch.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    var service = services[position]
                    if (service.isNotEmpty()) {
                        selectedService = service.toString()
                        binding.llSave.enable(true)
                    } else {
                        binding.llSave.enable(false)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
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
        setServicesListAdaptor()
        checkServices()
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(pos: Int) {
                position = pos
                deleteService()
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
            userServices, true, clickListener
        )
        binding.rcyServices.adapter = holidayListAdapter


    }

    override fun getViewModel(): Class<MemberViewModel> {
        return MemberViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater, container: ViewGroup?,
    ) = FragmentAddAdminServicesBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        MemberRepository(remoteDataSource.buildApi(MemberApi::class.java, requireContext()))

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

    private fun getUserServices() {
        viewModel.getUserServices.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        userServices.clear()
                        // ServicesMap.clear()
                        for (service in it.value.data) {
                            if (!userServices.contains(service.service.name)) {
                                userServices.add(service.service.name)
                                ServicesMap.put(service.service.name, service.service.id.toInt())

                            }

                        }
                        setServicesAdopter()
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
        viewModel.getUserServices(LoggedInInfo.user?.id?.toInt()!!)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun getServices() {
        viewModel.getSaloonCategories.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    if (it.value.status != 0) {
                        services.clear()
                        ServicesMap.clear()
                        for (categories in it.value.data) {
                            for (service in categories?.services!!) {
                                if (!services.contains(service?.name)) {
                                    services.add(service?.name ?: "")
                                    ServicesMap.put(service?.name!!, service.id.toInt())
                                }
                            }

                        }
                        getUserServices()
                    } else {

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
        viewModel.getCategories(LoggedInInfo.user?.saloonId!!)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun addService() {
        viewModel.addMemberData.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    if (it.value.status == 1) {
                        getServices()
                    } else {
                        (activity as HomeActivity?)?.hideLoadingIndicator()
                        requireView().snackbar(it.value.message)
                    }

                }

                is Resource.Failure -> {
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    Log.d("response", "failure " + it.toString())
if (it.errorCode== 403){
    requireView().snackbar("Operation not allowed for you")
    return@observe
}

                    handleApiError(it)
                }

                else -> {}
            }
        }
        var selectedServicesFinal = arrayListOf<Int>()
//        for (service in services) {
        selectedServicesFinal.add(ServicesMap[selectedService] ?: 0)

        //}
        viewModel.addService(
            LoggedInInfo.user?.id?.toInt()!!,
            AddMemberService(serviceId = ServicesMap[selectedService].toString() ?: "0")
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun deleteService() {
        viewModel.remove.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status == 1) {
                        userServices.removeAt(position)
                        setServicesAdopter()
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

        viewModel.deleteService(
            LoggedInInfo.user?.id?.toInt()!!,
            ServicesMap[userServices[position]]!!
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
}