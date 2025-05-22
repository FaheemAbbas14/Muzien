package com.tt.muzien.ui.bookings

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import com.tt.muzien.R
import com.tt.muzien.data.dto.FilterData
import com.tt.muzien.data.dto.SaloonDto
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.SaloonApi
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.databinding.FragmentBookingFilterBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.saloon.SaloonViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.ui.views.CustomCalendar
import com.tt.muzien.utilities.FilterSelection
import com.tt.muzien.utilities.TimeHelper


class FragmentBookingFilter :
    BaseFragment<SaloonViewModel, FragmentBookingFilterBinding, SaloonRepository>() {
    var selection: String = ""
    var bookingStatus: String? = null
    var serviceProvider: String? = null
    var fromDate: String? = null
    var toDate: String? = null
    var isFrom: Boolean = true
    var saloon: String = ""
    var saloonId: Int? = null
    var serviceProviderId: Int? = null
    var showSaloon: Boolean = true
    var showServiceProvider: Boolean = true
    private val saloonsList = arrayListOf<String>()
    private val saloonMap = HashMap<String, SaloonDto>()
    private val serviceProviderList = arrayListOf<String>()
    private val serviceProviderMap = HashMap<String, Int>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setMemberRepo((activity as HomeActivity?)?.getMemberRepo()!!)
        if (!showSaloon) {
            binding.llMainSaloon.visibility = View.GONE
        }
        if (!showServiceProvider) {
            binding.llMainServiceProvider.visibility = View.GONE
        }
        getSaloons()
        binding.radioBookingStatus.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbPending -> {
                    bookingStatus = "pending-approval"
                    // checkValidation()
                }

                R.id.rbScheduled -> {
                    bookingStatus = "scheduled"
                    // checkValidation()
                }

                R.id.rbOverdue -> {
                    bookingStatus = "overdue"
                    // checkValidation()

                }

                R.id.rbCompleted -> {
                    bookingStatus = "completed"
                    //checkValidation()
                }

                R.id.rbCancelled -> {
                    bookingStatus = "cancelled"
                    //checkValidation()
                }
            }
        }
        binding.radioServiceProvider.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbAllProvider -> {
                    serviceProvider = "AllProvider"
                    binding.llProvider.visibility = View.GONE
                    // checkValidation()
                }

                R.id.rbSpecific -> {
                    serviceProvider = "Specific"
                    binding.llProvider.visibility = View.VISIBLE
                    // checkValidation()
                }

            }
        }
        binding.radioSaloon.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbAllSaloon -> {
                    saloon = "AllSaloon"
                    binding.llSaloon.visibility = View.GONE
                    // checkValidation()
                }

                R.id.rbSpecificSaloon -> {
                    saloon = "Specific"
                    binding.llSaloon.visibility = View.VISIBLE
                    // checkValidation()
                }

            }
        }
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbWeek -> {
                    selection = "Week"
                    val dates = TimeHelper.getWeekAndMonthDates()
                    fromDate = dates["startOfWeek"]
                    toDate = dates["endOfWeek"]
                    checkValidation()
                    binding.llFrom.visibility = View.GONE
                    binding.llTo.visibility = View.GONE
                }

                R.id.rbMonth -> {
                    selection = "Month"
                    val dates = TimeHelper.getWeekAndMonthDates()
                    fromDate = dates["startOfMonth"]
                    toDate = dates["endOfMonth"]
                    checkValidation()
                    binding.llFrom.visibility = View.GONE
                    binding.llTo.visibility = View.GONE
                }

                R.id.rbCustom -> {
                    selection = "Custom"
                    checkValidation()
                    binding.llFrom.visibility = View.VISIBLE
                    // binding.llTo.visibility = View.VISIBLE
                }
            }
        }
        binding.txtFrom.setOnClickListener {
            binding.txtFromError.visibility = View.GONE
            binding.llTo.visibility = View.GONE
            isFrom = true
            binding.customCalendar.visibility = View.VISIBLE
        }
        binding.txtTo.setOnClickListener {
            isFrom = false
            binding.customCalendar.visibility = View.VISIBLE
        }
        binding.llApply.setOnClickListener {
            if (checkValidation()) {
                if (serviceProvider != "AllProvider") {
                    serviceProvider = binding.edtProvider.text.toString()
                    if (serviceProviderId == null && binding.edtProvider.text.toString() != "") {
                        serviceProviderId =
                            serviceProviderMap[binding.edtProvider.text.toString()] ?: 0
                    } else {
                        serviceProviderId = null
                    }
                }
                if (saloon != "AllSaloon") {
                    if (saloonId == null && binding.edtSaloon.text.toString() != "") {
                        saloonId = saloonMap[binding.edtSaloon.text.toString()]?.id ?: null
                    } else {
                        saloonId = null
                    }
                    saloon = binding.edtSaloon.text.toString()
                }
                FilterSelection.filterData =
                    FilterData(
                        selection,
                        fromDate,
                        toDate,
                        false,
                        bookingStatus,
                        serviceProvider,
                        saloon,
                        saloonId = saloonId,
                        serviceProviderId = serviceProviderId
                    )
                (activity as HomeActivity?)?.popFragment()
            }
        }
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.customCalendar.setOnDateSelectedListener(object :
            CustomCalendar.OnDateSelectedListener {
            override fun onDateSelected(date: String) {
                // Handle the selected date
                if (isFrom) {
                    // binding.txtFromError.visibility = View.VISIBLE
                    binding.llTo.visibility = View.VISIBLE
                    fromDate = date
                    binding.txtFrom.text = fromDate
                } else {
                    toDate = date
                    binding.txtTo.text = toDate
                }
                binding.customCalendar.visibility = View.GONE
                checkValidation()
            }
        })

    }


    private fun checkValidation(): Boolean {
        var isValid = true
//        if (selection == "") {
//            binding.txtSelectionError.visibility = View.VISIBLE
//            isValid = false
//        } else {
//            binding.txtSelectionError.visibility = View.GONE
//        }
        if (selection == "Custom" && fromDate == "") {
            isValid = false
            binding.txtFromError.visibility = View.VISIBLE
        } else {
            binding.txtFromError.visibility = View.GONE
        }
        if (selection == "Custom" && toDate == "" && fromDate != "") {
            binding.txtToError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.txtToError.visibility = View.GONE
        }

        return isValid
    }

    override fun getViewModel(): Class<SaloonViewModel> {
        return SaloonViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentBookingFilterBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        SaloonRepository(
            remoteDataSource.buildApi(SaloonApi::class.java, requireContext())
        )

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.hideTabs()
        (activity as HomeActivity?)?.setSystemWindow(true)
    }

    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.showTabs()
        (activity as HomeActivity?)?.setSystemWindow(true)
    }

    private fun getSaloons() {
        viewModel.getSaloon.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    // (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        saloonsList.clear()
                        for (saloon in it.value.data.items) {
                            if (!saloonsList.contains(saloon.name)) {
                                saloonsList.add(saloon.name)
                                var saloonData = SaloonDto(
                                    saloon.id.toInt(),
                                    saloon.SaloonImages,
                                    saloon.name,
                                    if (saloon.status == "open") true else false,
                                    saloon.address ?: "",
                                    "${saloon.tRating} (${saloon.numReviews} ${
                                        if (saloon.numReviews.toInt() == 1) "review" else "reviews"
                                    })",
                                    saloon.SaloonWorkHours,
                                    saloon.locationLat.toDouble(), saloon.locationLong.toDouble()
                                )
                                saloonMap.put(saloon.name, saloonData)
                            }


                        }
                        getServiceProvider()

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
        viewModel.getSaloons()
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun getServiceProvider() {
        viewModel.getServiceProviders.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        serviceProviderList.clear()
                        for (serviceProvider in it.value.data) {
                            if (!serviceProviderList.contains(serviceProvider.User.fullName)) {
                                serviceProviderList.add(serviceProvider.User.fullName)
                                serviceProviderMap.put(
                                    serviceProvider.User.fullName,
                                    serviceProvider.User.id.toInt()
                                )
                            }


                        }
                        setSaloonAdopter()
                        setServiceProviderAdopter()
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
        viewModel.getServiceProviders(true, null)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun setSaloonAdopter() {
        // Adapter to link the list with AutoCompleteTextView
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, saloonsList)

        // Set adapter to AutoCompleteTextView
        binding.edtSaloon.setAdapter(adapter)

        // Optional: Set the threshold (number of characters before suggestions appear)
        binding.edtSaloon.threshold = 1
    }

    private fun setServiceProviderAdopter() {
        // Adapter to link the list with AutoCompleteTextView
        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                serviceProviderList
            )

        // Set adapter to AutoCompleteTextView
        binding.edtProvider.setAdapter(adapter)

        // Optional: Set the threshold (number of characters before suggestions appear)
        binding.edtProvider.threshold = 1
    }
}