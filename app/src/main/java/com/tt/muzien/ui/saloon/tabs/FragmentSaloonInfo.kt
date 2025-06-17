package com.tt.muzien.ui.saloon.tabs

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
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
import com.tt.muzien.data.dto.AddSaloonData
import com.tt.muzien.data.dto.PayDto
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
import com.tt.muzien.ui.payment.FragmentPayNow
import com.tt.muzien.ui.saloon.FragmentSaloonDetails
import com.tt.muzien.ui.saloon.FragmentSearchAddress
import com.tt.muzien.ui.saloon.SaloonViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.Appelement
import com.tt.muzien.utilities.Helper
import com.tt.muzien.utilities.TimeHelper
import com.zabihah.ui.ui.interfaces.OnItemClickListner
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody


class FragmentSaloonInfo :
    BaseFragment<SaloonViewModel, FragmentSaloonInfoBinding, SaloonRepository>(),
    OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private var workingHourList = arrayListOf<WorkingHourData>()
    private var workingHoursAdopter: WorkingHoursAdapter? = null
    private val holidaysList = arrayListOf<String>()
    private var holidayListAdapter: HolidayListAdapter? = null
    private var isExpanded = false
    var selectedSaloon: SaloonDto? = null
    var subscriptionId: Int? = null
    var selectedSaloonDetails: SaloonDetailsInfo? = null
    var position: Int = 0
    private var certificate_uri: Uri? = null
    private val DOCUMENT_PICKER_REQUEST_CODE = 1001
    private val holidaysMap = HashMap<String, Long>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.swipeRefresh.setOnRefreshListener {
//            binding.swipeRefresh.isRefreshing = false
//           // page=1
//          getSaloons()
//        }
        getSaloons()
        binding.txtRenew.setOnClickListener {
            getPlans()
        }
        binding.txtUploadCertificate.setOnClickListener {
            selectDocument()
        }
        binding.imgAddHoliday.setOnClickListener {
            var nextFragment = FragmentAddHoliday()
            nextFragment.systemWindow = true
            nextFragment.isEdit = true
            nextFragment.saloonId = selectedSaloonDetails?.id?.toInt()!!
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.imgAddWorkingHour.setOnClickListener {
            var nextFragment = FragmentAddWorkingDay()
            nextFragment.isEdit = true
            nextFragment.systemWindow = true
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
                var startDate = holiday.startDate
                if (startDate.contains("T")) {
                    startDate = startDate.split("T")[0]
                }
                holidaysMap.put(startDate, holiday.id)
                holidaysList.add(startDate)
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
            binding.txtUploadCertificate.visibility = View.GONE
            binding.txtUploadedAt.visibility = View.VISIBLE
            binding.txtRenew.visibility = View.VISIBLE
            binding.txtRenew.text = requireContext().resources.getString(R.string.renew)
            binding.txtUploadedAt.text =
                TimeHelper.convertISOToDate(selectedSaloonDetails!!.createdAt, "yyyy-MM-dd HH:mm")
            if (selectedSaloonDetails?.certificate!!.contains("-")) {
                var nameArray = selectedSaloonDetails?.certificate!!.split("-")
                binding.txtCertificateName.text = nameArray[nameArray.size - 1]
            }
        } else {
            binding.imgCertificateIcon.visibility = View.GONE
            binding.txtCertificateName.visibility = View.GONE
            // binding.txtUploadCertificate.visibility = View.VISIBLE
            binding.txtUploadedAt.visibility = View.GONE
            binding.txtRenew.visibility = View.VISIBLE
            //   binding.txtRenew.text = requireContext().resources.getString(R.string.add)
        }
        if (selectedSaloonDetails!!.subscriptionExpiry != null) {
            binding.txtCertificateExpiry.visibility = View.VISIBLE
            binding.txtCertificateExpiry.text =
                "${resources.getString(R.string.subscription_expiry_date_12_12_25)} ${selectedSaloonDetails!!.subscriptionExpiry}"
        } else {
            binding.txtCertificateExpiry.visibility = View.GONE
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
        workingHourList.clear()
        if (selectedSaloonDetails?.SaloonWorkHours != null) {
            for (hour in selectedSaloonDetails?.SaloonWorkHours!!) {
                workingHourList.add(
                    WorkingHourData(
                        1,
                        "${hour.day}",
                        "${TimeHelper.convertUtcToLocalTime(hour?.openingTime ?: "")} - ${
                            TimeHelper.convertUtcToLocalTime(
                                hour?.closingTime ?: ""
                            )
                        }"
                    )
                )

            }
            workingHourList = Helper.sortWorkingHoursByWeekday(workingHourList)
        }
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(pos: Int) {
                position = pos
                deleteWorkingHour(workingHourList[pos].title)
            }
        }
        binding.rcyWorkingHours.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        workingHoursAdopter = WorkingHoursAdapter(
            workingHourList,
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
        container: ViewGroup?,
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
                        selectedSaloonDetails = it.value.data
                        val fragmentB =
                            requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container) as? FragmentSaloonDetails
                        fragmentB?.updateStatus(it.value.data.isActive, true)
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
                        workingHourList.removeAt(position)
                        workingHoursAdopter?.notifyDataSetChanged()
                       getSaloons()
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


    private fun uploadSaloonCertificate() {
        viewModel.uploadSaloonCertificate.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    if (it.value.status != 0) {
                        getSaloons()
                    } else {
                        requireView().snackbar(it.value.message)
                        (activity as HomeActivity?)?.hideLoadingIndicator()
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
        var certificatePart: MultipartBody.Part? = null
        if (AddSaloonData.certificate_uri != null) {
            var fileType =
                Helper.getFileExtension(requireContext(), AddSaloonData.certificate_uri!!)
            Log.d("fileType", "$fileType")
            val certificateFile =
                Helper.getFileFromUri(requireContext(), AddSaloonData.certificate_uri!!)
                    ?: return // Get file from URI
            val certificateRequestFile =
                RequestBody.create("application/$fileType".toMediaTypeOrNull(), certificateFile)
            certificatePart =
                MultipartBody.Part.createFormData(
                    "certificate",
                    "${selectedSaloonDetails?.name}_certificate.$fileType",
                    certificateRequestFile
                )
        }
        viewModel.uploadSaloonCertificate(
            selectedSaloonDetails?.id?.toInt() ?: 0,
            certificatePart
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    //    @RequiresApi(Build.VERSION_CODES.O)
//    private fun handleDocument(uri: Uri) {
//        // Example: Reading file name
//        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
//        cursor?.use {
//            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//            it.moveToFirst()
//            fileName = it.getString(nameIndex)
//            binding.txtDocName.text = fileName
//            println("Selected document name: $fileName")
//            val currentTime = TimeHelper.getCurrentTime("HH:mm:ss")
//            binding.txtDocTiming.text = "Uploaded on $currentTime"
//            //  binding.imgCertificate.setImageURI(image_uri)
//            binding.cnstCertificateData.visibility = View.VISIBLE
//            binding.imgCertificate.visibility = View.GONE
//        }
    fun selectDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type =
                "*/*" // Allows all document types. You can specify MIME types like "application/pdf" for PDFs.
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, DOCUMENT_PICKER_REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DOCUMENT_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            val documentUri = data?.data

            documentUri?.let {
                certificate_uri = documentUri
                AddSaloonData.certificate_uri = certificate_uri
                uploadSaloonCertificate()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            if (Appelement.reload) {
                Appelement.reload = false
                getSaloons()

            }
        }
    }

    private fun getPlans() {
        viewModel.getPlans.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        for (plan in it.value.data) {
                            if (plan.name == "saloon-annual" && plan.isActive) {
                                var nextFragment = FragmentPayNow()
                                nextFragment.subscriptionId = subscriptionId
                                var payDto =
                                    PayDto(
                                        selectedSaloon?.id ?: 0,
                                        plan.id.toInt(),
                                        selectedSaloon?.name ?: "",
                                        plan.name,
                                        plan.actualFee.toInt() * 100,
                                        (plan.actualFee - plan.discountedFee).toInt() * 100,
                                        plan.discountedFee.toInt() * 100,
                                        plan.currency,
                                        plan.durationInDays.toInt()
                                    )
                                nextFragment.payDto = payDto
                                (activity as HomeActivity?)?.loadFragment(nextFragment)
                            }
                        }
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
        viewModel.getPlans()
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
}