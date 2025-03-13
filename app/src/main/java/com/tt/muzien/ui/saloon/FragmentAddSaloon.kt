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
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.R
import com.tt.muzien.data.dto.WorkingHourData
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentAddSaloonBinding
import com.tt.muzien.ui.adapters.WorkingHoursAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.ui.payment.FragmentAddPayment
import com.tt.muzien.ui.payment.FragmentPayNow
import com.tt.muzien.ui.saloon.tabs.FragmentAddHoliday
import com.tt.muzien.ui.saloon.tabs.FragmentAddWorkingDay
import com.tt.muzien.utilities.TimeHelper
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentAddSaloon : BaseFragment<HomeViewModel, FragmentAddSaloonBinding, HomeRepository>() {
    private val workingHourrList = arrayListOf<WorkingHourData>()
    private var workingHoursAdopter: WorkingHoursAdapter? = null
    private val REQUEST_CAMERA = 1
    private val REQUEST_GALLERY = 2
    private val REQUEST_PERMISSIONS = 3
    private var image_uri: Uri? = null
    private var isCertificate: Boolean = false
    private var selectedAddress: String? = ""
    private val DOCUMENT_PICKER_REQUEST_CODE = 1001
    var fileName: String = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
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
            var nextFragment = FragmentPayNow()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        setFragmentResultListener("requestKey") { key, bundle ->
            selectedAddress = bundle.getString("address")
            if (selectedAddress != null && selectedAddress != "") {
                binding.llSelectLocation.visibility = View.GONE
                binding.txtAddress.visibility = View.VISIBLE
                binding.imgEditLocation.visibility = View.VISIBLE
                binding.txtAddress.text = selectedAddress
            }

        }
        setData()
        setPrivacyText()
    }

    private fun uploadImage() {
        if (checkPermissions()) {
            // showImagePickerDialog()
            openGallery()
        }
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
                requireActivity(),
                listPermissionsNeeded.toTypedArray(),
                REQUEST_PERMISSIONS
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
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, REQUEST_CAMERA)
    }


    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
            binding.txtDocTiming.text="Uploaded on $currentTime"
            binding.imgCertificate.setImageURI(image_uri)
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
                // Perform operations with the document Uri
                handleDocument(it)
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    if (image_uri != null) {
                        if (isCertificate) {
                            binding.imgCertificate.setImageURI(image_uri)
                            binding.cnstCertificateData.visibility = View.VISIBLE
                            binding.imgCertificate.visibility = View.GONE
                        } else {
                            binding.imgPhoto.setImageURI(image_uri)
                        }


                    }

                }

                REQUEST_GALLERY -> {
                    val selectedImageUri: Uri? = data?.data
                    if (selectedImageUri != null) {
                        //UserInfo.profilePic = selectedImageUri
                    }
                    if (isCertificate) {
                        binding.imgCertificate.setImageURI(selectedImageUri)
                        binding.cnstCertificateData.visibility = View.VISIBLE
                        binding.imgCertificate.visibility = View.GONE
                    } else {
                        binding.imgPhoto.setImageURI(selectedImageUri)
                    }

                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setPrivacyText() {
        var privacyText = "By continuing you agree to our\n" +
                "T&C and Privacy Policy"
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
            31, 34,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(privacySpan, 39, 52, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(
            ForegroundColorSpan(requireActivity().getColor(R.color.colorPrimary)),
            39, 53,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.txtPrivacy.text = spannableString
    }

    private fun setData() {
        setWorkingHourAdopter()
    }

    private fun setWorkingHourAdopter() {
        workingHourrList.clear()
        workingHourrList.add(WorkingHourData("1", "Saturday - Thursday", "10:00 AM - 11:00 PM"))
        workingHourrList.add(WorkingHourData("2", "Friday", "02:00 AM - 11:00 PM"))
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
                workingHourrList.removeAt(position)
                workingHoursAdopter?.notifyDataSetChanged()
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

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddSaloonBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java,requireContext()), userPreferences)

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.hideTabs()
    }

    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.showTabs()
    }
}