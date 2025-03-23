package com.tt.muzien.ui.saloon.tabs

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tt.muzien.R
import com.tt.muzien.data.dto.SaloonDto
import com.tt.muzien.data.dto.WorkingHourData
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.SaloonApi
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.data.responses.SaloonDetailsInfo
import com.tt.muzien.databinding.FragmentSaloonInfoBinding
import com.tt.muzien.ui.adapters.HolidayListAdapter
import com.tt.muzien.ui.adapters.WorkingHoursAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.saloon.FragmentSearchAddress
import com.tt.muzien.ui.saloon.SaloonViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.TimeHelper
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentSaloonInfo :
    BaseFragment<SaloonViewModel, FragmentSaloonInfoBinding, SaloonRepository>(),
    OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private val workingHourrList = arrayListOf<WorkingHourData>()
    private var workingHoursAdopter: WorkingHoursAdapter? = null
    private val holidaysList = arrayListOf<String>()
    private var holidayListAdapter: HolidayListAdapter? = null
    private var isExpanded = false
    var selectedSaloon: SaloonDto? = null
    var selectedSaloonDetails: SaloonDetailsInfo? = null
    var position: Int = 0
    private val holidaysMap = HashMap<String, Long>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSaloons()
        binding.imgAddHoliday.setOnClickListener {
            var nextFragment = FragmentAddHoliday()
            nextFragment.isEdit = true
            nextFragment.saloonId = selectedSaloonDetails?.id?.toInt()!!
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.imgAddWorkingHour.setOnClickListener {
            var nextFragment = FragmentAddWorkingDay()
            nextFragment.isEdit = true
            nextFragment.saloonId = selectedSaloonDetails?.id?.toInt()!!
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.imgEditAbout.setOnClickListener {
            var nextFragment = EditSaloonAbout()
            nextFragment.about = selectedSaloonDetails?.description ?: ""
            nextFragment.saloonId = selectedSaloonDetails?.id?.toInt()!!
            nextFragment.phone = selectedSaloonDetails!!.phoneNumber
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.imgEditContact.setOnClickListener {
            var nextFragment = EditSaloonContact()
            nextFragment.phone = selectedSaloonDetails?.phoneNumber ?: ""
            nextFragment.saloonId = selectedSaloonDetails?.id?.toInt()!!
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.imgEditAddress.setOnClickListener {
            var nextFragment = FragmentSearchAddress()
            nextFragment.isEdit = true
            nextFragment.saloonId = selectedSaloonDetails?.id?.toInt()!!
            nextFragment.selectedAddress = selectedSaloonDetails?.address ?: ""
            nextFragment.latitude = selectedSaloonDetails?.locationLat?.toDouble()!!
            nextFragment.longitude = selectedSaloonDetails?.locationLong?.toDouble()!!
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        // Initialize the SupportMapFragment and request the map.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun setAboutData() {

        if (selectedSaloonDetails?.description != null) {
            // Truncated text preview (show first 100 chars)
            selectedSaloonDetails?.description?.length?.let {
                if (it > 100) {
                    val previewText = selectedSaloonDetails?.description?.substring(
                        0,
                        100
                    ) + "..." // Truncated preview text

                    // Set initial text with "Read More" link
                    setTextWithToggle(previewText, selectedSaloonDetails?.description ?: "")
                } else {
                    binding.txtAbout.text = selectedSaloonDetails?.description
                }

            }
        }
    }

    private fun setHolidaysAdopter() {
        if (selectedSaloonDetails?.SaloonHolidays != null) {
            selectedSaloonDetails?.SaloonHolidays?.size?.let {
                if (it > 0) {
                    binding.txtHolidaysData.visibility = View.GONE
                    binding.rcyHolidays?.visibility = View.VISIBLE
                } else {
                    binding.txtHolidaysData.visibility = View.VISIBLE
                    binding.rcyHolidays?.visibility = View.GONE
                }
            }
            holidaysList.clear()
            holidaysMap.clear()
            for (holiday in selectedSaloonDetails?.SaloonHolidays!!) {
                holidaysMap.put(holiday.startDate, holiday.id)
                holidaysList.add(holiday.startDate)
            }
            val clickListener = object : OnItemClickListner {
                override fun onItemClick(pos: Int) {
                    position = pos
                    deleteHoliday(holidaysMap[holidaysList[pos]]?.toInt() ?: 0)
                }
            }
            binding.rcyHolidays?.layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            holidayListAdapter = HolidayListAdapter(
                holidaysList,
                false,
                clickListener
            )
            binding.rcyHolidays?.adapter = holidayListAdapter
        }

    }

    private fun setTextWithToggle(previewText: String, fullText: String) {
        val spannable = SpannableString(previewText + " Read More")

        // Set click listener on "Read More" text
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                toggleText(fullText, previewText)
            }
        }, previewText.length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.txtAbout.text = spannable
        binding.txtAbout.movementMethod =
            LinkMovementMethod.getInstance() // Enable clicking the text
    }

    private fun toggleText(fullText: String, previewText: String) {
        try {

            val spannable: SpannableString
            if (isExpanded) {
                // Show preview text with "Read More"
                spannable = SpannableString(previewText + " Read More")
                // Set click listener for "Read More"
                spannable.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        toggleText(fullText, previewText)
                    }
                }, previewText.length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                // Show full text with "Read Less"
                spannable = SpannableString(fullText + " Read Less")
                // Set click listener for "Read Less"
                spannable.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        toggleText(fullText, previewText)
                    }
                }, fullText.length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            binding.txtAbout.text = spannable
            binding.txtAbout.movementMethod =
                LinkMovementMethod.getInstance() // Enable clicking the text

            isExpanded = !isExpanded // Toggle state between expanded and collapsed

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @SuppressLint("NewApi")
    private fun setData() {
        binding.txtContactNo.text = selectedSaloonDetails?.phoneNumber
        binding.txtAddressData.text = selectedSaloonDetails?.address
        setCertificateData()
        setAddressData()
        setAboutData()
        setWorkingHourAdopter()
        setHolidaysAdopter()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setCertificateData() {
        if (selectedSaloonDetails?.certificate != null && selectedSaloonDetails?.certificate != "") {
            binding.imgCertificateIcon.visibility = View.VISIBLE
            binding.txtCertificateName.visibility = View.VISIBLE
            binding.txtCertificateExpiry.visibility = View.VISIBLE
            binding.txtUploadedAt.visibility = View.VISIBLE
            binding.txtRenew.visibility = View.VISIBLE
            binding.txtRenew.text = requireContext().resources.getString(R.string.renew)
            binding.txtUploadedAt.text =
                TimeHelper.convertISOToDate(selectedSaloonDetails!!.createdAt, "yyyy-MM-dd HH:mm")
        } else {
            binding.imgCertificateIcon.visibility = View.GONE
            binding.txtCertificateName.visibility = View.GONE
            binding.txtCertificateExpiry.visibility = View.GONE
            binding.txtUploadedAt.visibility = View.GONE
            binding.txtRenew.visibility = View.VISIBLE
            binding.txtRenew.text = requireContext().resources.getString(R.string.add)
        }
    }

    private fun setAddressData() {
        // Add a marker at a specific location and move the camera
        val location = LatLng(
            selectedSaloonDetails?.locationLat?.toDouble() ?: 0.0,
            selectedSaloonDetails?.locationLong?.toDouble() ?: 0.0
        ) // Example coordinates (Sydney, Australia)
        map.addMarker(MarkerOptions().position(location).title(selectedSaloonDetails?.address))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
    }

    private fun setWorkingHourAdopter() {
        workingHourrList.clear()
        if (selectedSaloonDetails?.SaloonWorkHours != null) {
            for (hour in selectedSaloonDetails?.SaloonWorkHours!!) {
                workingHourrList.add(
                    WorkingHourData(
                        1,
                        "${hour.day}",
                        "${hour.openingTime} - ${hour.closingTime}"
                    )
                )

            }
        }
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(pos: Int) {
                position = pos
                deleteWorkingHour(workingHourrList[pos].title)
            }
        }
        binding.rcyWorkingHours.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        workingHoursAdopter = WorkingHoursAdapter(
            workingHourrList,
            requireContext(),
            clickListener
        )
        binding.rcyWorkingHours.adapter = workingHoursAdopter


    }

    override fun getViewModel(): Class<SaloonViewModel> {
        return SaloonViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSaloonInfoBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        SaloonRepository(remoteDataSource.buildApi(SaloonApi::class.java, requireContext()))

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap


    }

    private fun getSaloons() {
        viewModel.getSaloonDetails.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        selectedSaloonDetails = it.value.data.saloon
                        setData()
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
        viewModel.getSaloonsDetails(selectedSaloon?.id ?: 0)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun deleteHoliday(holidayId: Int) {
        viewModel.deleteHoliday.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        holidaysList.removeAt(position)
                        holidayListAdapter?.notifyDataSetChanged()
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
        viewModel.deleteHoliday(selectedSaloon?.id ?: 0, holidayId)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun deleteWorkingHour(day: String) {
        viewModel.deleteHoliday.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        workingHourrList.removeAt(position)
                        workingHoursAdopter?.notifyDataSetChanged()
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
        viewModel.deleteWorkingHour(selectedSaloon?.id ?: 0, day)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

}