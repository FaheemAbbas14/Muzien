package com.tt.muzien.ui.saloon

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.R
import com.tt.muzien.data.dto.AddSaloonData
import com.tt.muzien.data.dto.PayDto
import com.tt.muzien.data.dto.WorkingHourData
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.SaloonApi
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.data.requests.HolidayData
import com.tt.muzien.data.requests.WorkHourData
import com.tt.muzien.data.responses.AddSaloon
import com.tt.muzien.databinding.FragmentAddSaloonBinding
import com.tt.muzien.ui.adapters.HolidayListAdapter
import com.tt.muzien.ui.adapters.ImagesListAdopter
import com.tt.muzien.ui.adapters.WorkingHoursAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.payment.FragmentPayNow
import com.tt.muzien.ui.saloon.tabs.FragmentAddHoliday
import com.tt.muzien.ui.saloon.tabs.FragmentAddWorkingDay
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.Helper
import com.tt.muzien.utilities.InputValidator
import com.tt.muzien.utilities.TimeHelper
import com.zabihah.ui.ui.interfaces.OnItemClickListner
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.Locale


class FragmentAddSaloon :
    BaseFragment<SaloonViewModel, FragmentAddSaloonBinding, SaloonRepository>() {
    private var workingHourList = arrayListOf<WorkingHourData>()
    private val holidaysList = arrayListOf<String>()
    private var workingHoursAdopter: WorkingHoursAdapter? = null
    private var holidayListAdapter: HolidayListAdapter? = null
    private var imagesListAdopter: ImagesListAdopter? = null
    private val REQUEST_CAMERA = 1
    private val REQUEST_GALLERY = 2
    private val REQUEST_PERMISSIONS = 3
    private var image_uris = arrayListOf<Uri>()
    private var image_uri: Uri? = null
    private var certificate_uri: Uri? = null
    private var isCertificate: Boolean = false
    private val DOCUMENT_PICKER_REQUEST_CODE = 1001
    var fileName: String = ""
    var country: String = ""
    var saloonId: Int = 0
    var selectedSaloon: AddSaloon? = null
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                // Permission denied
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llBack.setOnClickListener {
            AddSaloonData.clear()
            (activity as HomeActivity?)?.popFragment()
        }
        binding.txtCancel.setOnClickListener {
            AddSaloonData.clear()
            (activity as HomeActivity?)?.popFragment()
        }
        setFragmentResultListener("requestKey") { key, bundle ->
            var reload = bundle.getBoolean("reload")
            if (reload) {
                setData()
            }

        }
        binding.countrySpinner.setCountryForNameCode("SA")
        binding.countrySpinner.setOnCountryChangeListener {
            country = binding.countrySpinner.selectedCountryName
            val countryCode = binding.countrySpinner.selectedCountryCode
            binding.txtCountryCode.text = "+$countryCode"
            AddSaloonData.country = "+$countryCode"
            binding.edtPhoneNumber.hint =
                Editable.Factory.getInstance()
                    .newEditable(InputValidator.getPhoneNumberPlaceholder(binding.countrySpinner.selectedCountryNameCode))

            checkValidation()
        }
        binding.edtPhoneNumber.addTextChangedListener(object : TextWatcher {
            var length_before = 0
            private var isFormatting: Boolean = false
            private var lastText: String = ""
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                length_before = s.length
            }

            override fun afterTextChanged(s: Editable) {
                if (isFormatting || s == null) return

                isFormatting = true

                val digits = s.toString().replace(" ", "")
                val formatted = StringBuilder()

                for (i in digits.indices) {
                    formatted.append(digits[i])
                    if ((i == 2 || i == 5) && i != digits.length - 1) {
                        formatted.append(" ")
                    }
                }

                if (formatted.toString() != s.toString()) {
                    binding.edtPhoneNumber.setText(formatted.toString())
                    binding.edtPhoneNumber.setSelection(formatted.length)
                }

                isFormatting = false
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {


                //checkValidation()


            }
        })

        binding.imgPhoto.setOnClickListener {
            isCertificate = false
            uploadImage()
        }
        binding.imgCertificate.setOnClickListener {
            isCertificate = true
            selectDocument()
        }
        binding.llSelectLocation.setOnClickListener {
            var nextFragment = FragmentSearchAddress()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.imgEditLocation.setOnClickListener {
            var nextFragment = FragmentSearchAddress()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.imgAddHoliday.setOnClickListener {
            var nextFragment = FragmentAddHoliday()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.imgAddWorkingHour.setOnClickListener {
            var nextFragment = FragmentAddWorkingDay()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.imgMinus.setOnClickListener {
            binding.cnstCertificateData.visibility = View.GONE
            binding.imgCertificate.visibility = View.VISIBLE
        }
        binding.txtContinue.setOnClickListener {
            if (checkValidation()) {
                addSaloon()
            }
//            var nextFragment = FragmentPayNow()
//            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        setFragmentResultListener("requestKey") { key, bundle ->
            setData()

        }
        checkValidation()
        setPrivacyText()
    }

    private fun setAddress() {
        if (AddSaloonData.address != null && AddSaloonData.address != "") {
            binding.llSelectLocation.visibility = View.GONE
            binding.txtAddress.visibility = View.VISIBLE
            binding.imgEditLocation.visibility = View.VISIBLE
            binding.txtAddress.text = AddSaloonData.address
        }
    }

    private fun checkValidation(): Boolean {
        var isValid = false
        var phone =
            binding.txtCountryCode.text.toString() + binding.edtPhoneNumber.text.toString()
                .replace(" ", "")
        if (AddSaloonData.addressLng != 0.0 && AddSaloonData.addressLat != 0.0 && binding.edtName.text.toString() != "" && binding.edtDescription.text.toString() != "" && phone != ""
        ) {
            isValid = true
        }
        return isValid
    }

    private fun uploadImage() {

// Usage
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
//        if (checkPermissions()) {
//            // showImagePickerDialog()
//            openGallery()
//        }
    }

    private fun showImagePickerDialog() {
        val options =
            arrayOf(getString(R.string.camera), getString(R.string.select_from_gallery_photo))
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.choose_avatar)
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    fun selectDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type =
                "*/*" // Allows all document types. You can specify MIME types like "application/pdf" for PDFs.
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, DOCUMENT_PICKER_REQUEST_CODE)
    }

    private fun checkPermissions(): Boolean {
        val cameraPermission =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)

        val listPermissionsNeeded = mutableListOf<String>()

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }

//        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
//        }
//        if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(), listPermissionsNeeded.toTypedArray(), REQUEST_PERMISSIONS
            )
            return false
        }
        return true
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, REQUEST_CAMERA)
    }


    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(galleryIntent, REQUEST_GALLERY)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleDocument(uri: Uri) {
        // Example: Reading file name
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            fileName = it.getString(nameIndex)
            binding.txtDocName.text = fileName
            println("Selected document name: $fileName")
            val currentTime = TimeHelper.getCurrentTime("HH:mm:ss")
            binding.txtDocTiming.text = "Uploaded on $currentTime"
            //  binding.imgCertificate.setImageURI(image_uri)
            binding.cnstCertificateData.visibility = View.VISIBLE
            binding.imgCertificate.visibility = View.GONE
        }

        // Example: Open InputStream
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        inputStream?.use {
            // Read the content of the document
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DOCUMENT_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            val documentUri = data?.data

            documentUri?.let {
                certificate_uri = documentUri
                AddSaloonData.certificate_uri = certificate_uri
                // Perform operations with the document Uri
                handleDocument(it)
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    if (image_uri != null) {
                        if (isCertificate) {
                            certificate_uri = image_uri
                            binding.imgCertificate.setImageURI(image_uri)
                            binding.cnstCertificateData.visibility = View.VISIBLE
                            binding.imgCertificate.visibility = View.GONE
                        } else {
                            image_uris.add(image_uri!!)
                            AddSaloonData.image_uris = image_uris
                            setImagesAdopter()
                        }


                    }

                }

                REQUEST_GALLERY -> {
                    if (data?.clipData != null) {
                        // Multiple images selected
                        val count = data.clipData!!.itemCount
                        for (i in 0 until count) {
                            val imageUri = data.clipData!!.getItemAt(i).uri
                            image_uris.add(imageUri)
                        }
                        // Handle multiple images (e.g., display or upload them)

                        setImagesAdopter()
                    } else if (data?.data != null) {
                        // Single image selected
                        val imageUri = data.data!!
                        if (isCertificate) {
                            certificate_uri = image_uri
                            binding.imgCertificate.setImageURI(imageUri)
                            binding.cnstCertificateData.visibility = View.VISIBLE
                            binding.imgCertificate.visibility = View.GONE
                        } else {
                            // Handle single image
                            image_uris.add(imageUri)
                            setImagesAdopter()
                        }
                    }

                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setPrivacyText() {
        var privacyText = "By continuing you agree to our\n" + "T&C and Privacy Policy"
        var spannableString = SpannableString(privacyText)

        // Make "here" clickable and change its color
        val tcSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(requireContext(), "TC", Toast.LENGTH_SHORT).show()
            }
        }
        val privacySpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(requireContext(), "Privacy", Toast.LENGTH_SHORT).show()

            }
        }

        spannableString.setSpan(tcSpan, 31, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(
            ForegroundColorSpan(requireActivity().getColor(R.color.colorPrimary)),
            31,
            34,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(privacySpan, 39, 52, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(
            ForegroundColorSpan(requireActivity().getColor(R.color.colorPrimary)),
            39,
            53,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.txtPrivacy.text = spannableString
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData() {
        setWorkingHourAdopter()
        setHolidaysAdopter()
        setAddress()
        setCountryCode()
        if (AddSaloonData.image_uris != null) {
            image_uris = AddSaloonData.image_uris
            setImagesAdopter()
        }
        if (AddSaloonData.certificate_uri != null) {
            certificate_uri = AddSaloonData.certificate_uri
            handleDocument(certificate_uri!!)
        }
    }

    private fun setImagesAdopter() {
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
                image_uris.removeAt(position)
                imagesListAdopter?.notifyDataSetChanged()
            }
        }
        binding.rcyPhotos.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        imagesListAdopter = ImagesListAdopter(
            image_uris,
            clickListener
        )
        binding.rcyPhotos.adapter = imagesListAdopter
    }

    private fun setCountryCode() {
        if (AddSaloonData.country != null) {
            binding.countrySpinner.setCountryForPhoneCode(Integer.parseInt(AddSaloonData.country))
            binding.edtPhoneNumber.hint =
                Editable.Factory.getInstance()
                    .newEditable(InputValidator.getPhoneNumberPlaceholder(binding.countrySpinner.selectedCountryNameCode))

        }
    }

    private fun setWorkingHourAdopter() {
        workingHourList.clear()
        for (day in AddSaloonData.days) {
            workingHourList.add(
                WorkingHourData(
                    1,
                    day,
                    "${AddSaloonData.startTime} - ${AddSaloonData.endTime}"
                )
            )
            workingHourList = Helper.sortWorkingHoursByWeekday(workingHourList)
        }
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
                AddSaloonData.days.removeAt(position)
                workingHourList.removeAt(position)
                workingHoursAdopter?.notifyDataSetChanged()
            }
        }
        binding.rcyWorkingHours.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        workingHoursAdopter = WorkingHoursAdapter(
            workingHourList, requireContext(), clickListener
        )
        binding.rcyWorkingHours.adapter = workingHoursAdopter


    }

    private fun setHolidaysAdopter() {
        if (AddSaloonData.holidays.size > 0) {
            binding.txtHolidaysData.visibility = View.GONE
            binding.rcyHolidays.visibility = View.VISIBLE
        } else {
            binding.txtHolidaysData.visibility = View.VISIBLE
            binding.rcyHolidays.visibility = View.GONE
        }
        holidaysList.clear()
        for (holiday in AddSaloonData.holidays) {
            holidaysList.add(holiday)
        }
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
                AddSaloonData.holidays.removeAt(position)
                holidaysList.removeAt(position)
                holidayListAdapter?.notifyDataSetChanged()
            }
        }
        binding.rcyHolidays.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        holidayListAdapter = HolidayListAdapter(
            holidaysList,
            false,
            clickListener
        )
        binding.rcyHolidays.adapter = holidayListAdapter


    }

    override fun getViewModel(): Class<SaloonViewModel> {
        return SaloonViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater, container: ViewGroup?,
    ) = FragmentAddSaloonBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        SaloonRepository(remoteDataSource.buildApi(SaloonApi::class.java, requireContext()))

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        setData()
        (activity as HomeActivity?)?.hideTabs()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as HomeActivity?)?.setSystemWindow(true)
            setData()
            (activity as HomeActivity?)?.hideTabs()
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.showTabs()
    }

    private fun addSaloon() {
        viewModel.addSaloon.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    if (it.value.status != 0) {
                        selectedSaloon = it.value.data
                        AddSaloonData.clear()
                        requireView().snackbar("Saloon added successfully")
                        getPlans()
//                        (activity as HomeActivity?)?.hideLoadingIndicator()
//                        Appelement.reload=true
//                        (activity as HomeActivity?)?.popFragment()
                        //  }
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
        var images = ArrayList<MultipartBody.Part>()
        if (image_uris != null && image_uris.size > 0) {
            for (uri in image_uris) {
                val imageFile =
                    Helper.getFileFromUri(requireContext(), uri!!) ?: return // Get file from URI
                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
                val imagePart =
                    MultipartBody.Part.createFormData("images", imageFile.name, requestFile)
                images.add(imagePart)
            }

        }
        var certificatePart: MultipartBody.Part? = null
        if (certificate_uri != null) {
            var fileType = Helper.getFileExtension(requireContext(), certificate_uri!!)
            Log.d("fileType", "$fileType")
            val certificateFile =
                Helper.getFileFromUri(requireContext(), certificate_uri!!)
                    ?: return // Get file from URI
            val certificateRequestFile =
                RequestBody.create("application/$fileType".toMediaTypeOrNull(), certificateFile)
            certificatePart =
                MultipartBody.Part.createFormData(
                    "certificate",
                    "${binding.edtName.text}_certificate.$fileType",
                    certificateRequestFile
                )
        }
        var phone =
            binding.txtCountryCode.text.toString() + binding.edtPhoneNumber.text.toString()
                .replace(" ", "")

        // Create text-based request bodies
        val name =
            RequestBody.create("text/plain".toMediaTypeOrNull(), binding.edtName.text.toString())
        val description = RequestBody.create(
            "text/plain".toMediaTypeOrNull(), binding.edtDescription.text.toString()
        )
        val phoneNumber = RequestBody.create(
            "text/plain".toMediaTypeOrNull(), phone
        )
        val address = RequestBody.create(
            "text/plain".toMediaTypeOrNull(), "${AddSaloonData.address}"
        )

        val locationLat =
            RequestBody.create("text/plain".toMediaTypeOrNull(), "${AddSaloonData.addressLat}")
        val locationLong =
            RequestBody.create("text/plain".toMediaTypeOrNull(), "${AddSaloonData.addressLng}")
        var holidays = ArrayList<HolidayData>()
        for (holiday in AddSaloonData.holidays) {
            holidays.add(HolidayData(holiday, holiday))
        }
        var workHours = ArrayList<WorkHourData>()
        for (day in AddSaloonData.days) {
            workHours.add(
                WorkHourData(
                    day,
                    TimeHelper.convertLocalTimeToUtc(AddSaloonData.startTime ?: ""),
                    TimeHelper.convertLocalTimeToUtc(AddSaloonData.endTime ?: "")
                )
            )
        }
        val holidaysParts = mutableMapOf<String, RequestBody>()
        // Convert each user object into separate form-data fields
        holidays.forEachIndexed { index, holiday ->
            var startTime = holiday.startTime
            var endTime = holiday.endTime
            val isArabic = Locale.getDefault().language == "ar"
            if (isArabic) {
                startTime = TimeHelper.convertArabicDigitsToEnglish(startTime)
                endTime = TimeHelper.convertArabicDigitsToEnglish(endTime)
            }
            holidaysParts["SaloonHolidays[$index][startDate]"] =
                RequestBody.create("text/plain".toMediaTypeOrNull(), startTime)
            holidaysParts["SaloonHolidays[$index][endDate]"] =
                RequestBody.create("text/plain".toMediaTypeOrNull(), endTime)
        }
        val hoursParts = mutableMapOf<String, RequestBody>()
        // Convert each user object into separate form-data fields
        workHours.forEachIndexed { index, workHour ->
            hoursParts["SaloonWorkHours[$index][day]"] = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                workHour.day
            )
            hoursParts["SaloonWorkHours[$index][openingTime]"] =
                RequestBody.create("text/plain".toMediaTypeOrNull(), workHour.startTime ?: "")
            hoursParts["SaloonWorkHours[$index][closingTime]"] =
                RequestBody.create("text/plain".toMediaTypeOrNull(), workHour.endTime ?: "")
        }

        viewModel.addSaloon(
            certificatePart,
            images,
            name,
            description,
            locationLat,
            locationLong,
            phoneNumber,
            address,
            hoursParts,
            holidaysParts
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
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
                                nextFragment.subscriptionId = 0
                                var payDto =
                                    PayDto(
                                        selectedSaloon?.id?.toInt() ?: 0,
                                        plan.id.toInt(),
                                        selectedSaloon?.name ?: "",
                                        plan.name,
                                        plan.actualFee.toInt(),
                                        (plan.actualFee - plan.discountedFee).toInt(),
                                        plan.discountedFee.toInt(),
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
        //(activity as HomeActivity?)?.showLoadingIndicator()
    }

}