package com.tt.muzien.ui.member

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.tt.muzien.R
import com.tt.muzien.data.network.MemberApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.MemberRepository
import com.tt.muzien.data.requests.AddMemberService
import com.tt.muzien.databinding.FragmentAddServiceBinding
import com.tt.muzien.ui.adapters.SelectedServiceAdaptor
import com.tt.muzien.ui.adapters.ServiceSpinnerAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.Appelement
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentAddService :
    BaseFragment<MemberViewModel, FragmentAddServiceBinding, MemberRepository>() {
    var type: String = ""
    var serviceId = 0
    var userId = 0
    var saloonId: Int? = 0
    var isEdit = false
    private val servicesList = arrayListOf<String>()
    var addedServicesList = arrayListOf<String>()
    var addedServicesMap = HashMap<String, Int>()
    var selectedServices = arrayListOf<String>()
    var selectedPosition = 0
    private val servicesMap = HashMap<String, Int>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setServiceRepo((activity as HomeActivity?)?.getServiceRepo()!!)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.llSave.setOnClickListener {
            if (selectedServices.size > 0) {
                addService()
            }

        }

        getServices()

    }

    private fun setServicesAdapter() {
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
                if (position <= addedServicesList.size - 1) {
                    if (servicesMap[addedServicesList[position]] !=null) {
                        deleteService(position)
                    }
                } else {
                    var position = position - addedServicesList.size
                    selectedServices.removeAt(position)
                    setServicesAdapter()
                }

            }
        }
        var services = arrayListOf<String>()
        for (service in addedServicesList) {
            //if (servicesList.contains(service)) {
                services.add(service)
          //  }
        }
        for (newService in selectedServices) {
            services.add(newService)
        }
        val adapter = SelectedServiceAdaptor(services, clickListener)

        val flexboxLayoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW  // horizontal flow
            flexWrap = FlexWrap.WRAP            // wrap to next line
            justifyContent = JustifyContent.FLEX_START
        }

        binding.gridRecyclerView.layoutManager = flexboxLayoutManager
        binding.gridRecyclerView.adapter = adapter
    }

    override fun getViewModel(): Class<MemberViewModel> {
        return MemberViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentAddServiceBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        MemberRepository(remoteDataSource.buildApi(MemberApi::class.java, requireContext()))

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
        (activity as HomeActivity?)?.changeStatusBarColor(requireActivity().resources.getColor(R.color.white))
        (activity as HomeActivity?)?.hideTabs()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
            (activity as HomeActivity?)?.changeStatusBarColor(requireActivity().resources.getColor(R.color.white))
            (activity as HomeActivity?)?.hideTabs()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, false)
        (activity as HomeActivity?)?.changeStatusBarColor(requireActivity().resources.getColor(R.color.white))
        (activity as HomeActivity?)?.showTabs()
    }

    private fun getServices() {
        viewModel.getSaloonCategories.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        servicesList.clear()
                        servicesList.add(0, "Select Service")
                        for (categories in it.value.data) {
                            for (service in categories?.services!!) {
                                if (!servicesList.contains(service?.name)) {
                                    servicesList.add(service?.name ?: "")
                                    servicesMap.put(service?.name!!, service.id.toInt())
                                }
                            }

                        }
                        setServiceAdopter()
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
        viewModel.getCategories(saloonId ?: 0)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun setServiceAdopter() {
        // Adapter to link the list with AutoCompleteTextView
        val adapter = ServiceSpinnerAdapter(requireContext(), servicesList)
        binding.edtService.adapter = adapter
        binding.edtService.setSelection(0)
        binding.edtService.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val selectedItem = parent.getItemAtPosition(position) as String
                if (!selectedServices.contains(selectedItem) && selectedItem != "Select Service") {
                    selectedServices.add(selectedItem)
                }
                setServicesAdapter()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        setServicesAdapter()
    }

    private fun addService() {
        viewModel.addMemberData.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status == 1) {
                        Appelement.reload = true
                        (activity as HomeActivity?)?.popFragment()
                        requireView().snackbar("Service added successfully")
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
        var selectedServicesFinal = arrayListOf<Int>()
        for (service in selectedServices) {
            selectedServicesFinal.add(servicesMap[service] ?: 0)

        }
        viewModel.addService(
            userId, AddMemberService(selectedServicesFinal)
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun deleteService(position: Int) {
        selectedPosition = position
        viewModel.removeHoliday.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status == 1) {
                        addedServicesList.removeAt(selectedPosition)
                        setServicesAdapter()
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

        viewModel.deleteService(userId, servicesMap[addedServicesList[position]]!!)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
}