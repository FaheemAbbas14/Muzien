package com.tt.muzien.ui.member

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import com.tt.muzien.R
import com.tt.muzien.data.network.MemberApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.MemberRepository
import com.tt.muzien.data.requests.AddMemberService
import com.tt.muzien.databinding.FragmentAddServiceBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.snackbar


class FragmentAddService :
    BaseFragment<MemberViewModel, FragmentAddServiceBinding, MemberRepository>() {
    var type: String = ""
    var serviceId = 0
    var userId = 0
    private val servicesList = arrayListOf<String>()
    private val servicesMap = HashMap<String, Int>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setServiceRepo((activity as HomeActivity?)?.getServiceRepo()!!)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.llSave.setOnClickListener {
            if (serviceId == 0) {
                serviceId = servicesMap[binding.edtService.text.toString()] ?: 0
            }
            addService()

        }

        getServices()

    }

    override fun getViewModel(): Class<MemberViewModel> {
        return MemberViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddServiceBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        MemberRepository(remoteDataSource.buildApi(MemberApi::class.java, requireContext()))

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.changeStatusBarColor(requireActivity().resources.getColor(R.color.white))
        (activity as HomeActivity?)?.hideTabs()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.changeStatusBarColor(requireActivity().resources.getColor(R.color.white))
        (activity as HomeActivity?)?.showTabs()
    }

    private fun getServices() {
        viewModel.getCategories.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        servicesList.clear()
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
        viewModel.getCategories()
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun setServiceAdopter() {
        // Adapter to link the list with AutoCompleteTextView
        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                servicesList
            )

        // Set adapter to AutoCompleteTextView
        binding.edtService.setAdapter(adapter)

        // Optional: Set the threshold (number of characters before suggestions appear)
        binding.edtService.threshold = 1
    }

    private fun addService() {
        viewModel.addMemberData.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status == 1) {

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
        viewModel.addService(
            userId, AddMemberService(serviceId.toString())
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
}