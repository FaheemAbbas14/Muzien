package com.tt.muzien.ui.saloon.tabs

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tt.muzien.R
import com.tt.muzien.data.dto.SaloonDto
import com.tt.muzien.data.dto.ServiceInfo
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.ServiceApi
import com.tt.muzien.data.repository.ServiceRepository
import com.tt.muzien.data.requests.AddSaloonService
import com.tt.muzien.data.requests.AddServiceSaloonRequest
import com.tt.muzien.databinding.FragmentAddSaloonServicesBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.service.FragmentUpdateService
import com.tt.muzien.ui.service.ServiceViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.Appelement
import com.tt.muzien.utilities.Helper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody


class FragmentAddSaloonServices :
    BaseFragment<ServiceViewModel, FragmentAddSaloonServicesBinding, ServiceRepository>() {
    private val REQUEST_CAMERA = 1
    private val REQUEST_GALLERY = 2
    private val REQUEST_PERMISSIONS = 3
    private var image_uri: Uri? = null
    private var categoryId: Int? = null
    private var category: String? = null
    var saloonId: Int? = 0
    var serviceId: Int? = 0
    private val servicesMap = HashMap<String, Int>()
    private val services = arrayListOf<String>()
    private val saloonsList = arrayListOf<String>()
    private val saloonMap = HashMap<String, SaloonDto>()
    var service: ServiceInfo? = null
    var saveAdd = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setSaloonRepo((activity as HomeActivity?)?.getSaloonRepo()!!)
        getSaloons()
        binding.llBack.setOnClickListener {
            // Appelement.reload=true
            (activity as HomeActivity?)?.popFragment()
        }
        binding.imgPhoto.setOnClickListener {
            uploadImage()
        }
        binding.llSave.setOnClickListener {

            if (checkValidation()) {
                if (saloonId == 0) {
                    //  saloonId = saloonMap[binding.edtSaloon.text.toString()]?.id ?: 0
                }
                saveAdd = false
                addService()
            }
        }
        binding.llSaveAdd.setOnClickListener {

            if (checkValidation()) {
                if (saloonId == 0) {
                    saloonId = saloonMap[binding.edtSaloon.text.toString()]?.id ?: 0
                }
                saveAdd = true
                addService()
            }
        }
        // checkValidation()
        getCategories()
    }

    private fun showPopupDialog() {
        // Create Dialog
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.add_service_popup, null)
        dialog.setContentView(view)

        // Make dialog background transparent
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Find buttons and handle click events
        val notNowButton = view.findViewById<Button>(R.id.not_now_button)
        val proceedButton = view.findViewById<Button>(R.id.proceed_button)

        notNowButton.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog
        }

        proceedButton.setOnClickListener {
//            var nextFragment = FragmentAddSaloonService()
//            nextFragment.serviceId = serviceId ?: 0
//            (activity as HomeActivity?)?.loadFragment(nextFragment)

            var nextFragment = FragmentUpdateService()
            nextFragment.service = service
            nextFragment.category = category
            nextFragment.saloonId = saloonId.toString()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
            // Add your logic here (e.g., enable the service)
            dialog.dismiss()

        }

        // Show the dialog
        dialog.show()
    }

    private fun checkValidation(): Boolean {
        var isValid = true
//        if (binding.edtEmail.text.toString() == "") {
//            binding.txtEmailError.visibility = View.VISIBLE
//            isValid = false
//        } else {
//            binding.txtEmailError.visibility = View.GONE
//        }
//        if (!InputValidator.isValidEmail(
//                binding.edtEmail.text.toString()
//            )
//        ) {
//            binding.txtEmailError.visibility = View.VISIBLE
//            isValid = false
//        } else {
//            binding.txtEmailError.visibility = View.GONE
//        }
        return isValid
    }

    override fun getViewModel(): Class<ServiceViewModel> {
        return ServiceViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddSaloonServicesBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        ServiceRepository(remoteDataSource.buildApi(ServiceApi::class.java, requireContext()))

    override fun onResume() {
        super.onResume()
        if (saloonId != null && saloonId != 0) {
            (activity as HomeActivity?)?.setSystemWindow(true)
            (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
        }
        //  (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
        (activity as HomeActivity?)?.hideTabs()
    }

    override fun onPause() {
        super.onPause()
        if (saloonId != null && saloonId != 0) {
            (activity as HomeActivity?)?.setSystemWindow(false)
            (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, false)
        }
        // (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, false)
        (activity as HomeActivity?)?.showTabs()
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    if (image_uri != null) {
                        binding.imgPhoto.setImageURI(image_uri)


                    }

                }

                REQUEST_GALLERY -> {
                    val selectedImageUri: Uri? = data?.data
                    if (selectedImageUri != null) {
                        image_uri = selectedImageUri
                    }
                    binding.imgPhoto.setImageURI(selectedImageUri)

                }
            }
        }
    }

    private fun getCategories() {
        viewModel.getCategories.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        servicesMap.clear()
                        services.clear()
                        for (category in it.value.data) {
                            if (categoryId == null) {
                                categoryId = category?.id
                            }
                            servicesMap.put(category!!.name, category.id)
                            services.add(category.name)
                        }
                    }
                    setData()
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())

                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.getAllCategories()
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun setData() {
        setCategoryAdopter()
    }

    private fun setCategoryAdopter() {
        val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, services)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnCategory.adapter = adapter
        binding.spnCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                category = services[position]
                categoryId = servicesMap.get(category)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle when no item is selected (optional)
            }
        }
    }

    private fun addService() {
        viewModel.addService.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        serviceId = it.value.data.id.toInt()
                        service = ServiceInfo(
                            it.value.data.id.toInt(),
                            it.value.data.image ?: "",
                            it.value.data.name,
                            "Duration: ${it.value.data.duration} ${
                                if (it.value.data.duration.toInt() == 1) "min" else "mins"
                            }",
                            "SAR ${it.value.data.price / 100}",
                            null
                        )
                        if (saveAdd) {
                            if (saloonId != null && saloonId != 0) {
                                addSaloonService()
                            } else {
                                var nextFragment = FragmentUpdateService()
                                nextFragment.service = service
                                nextFragment.category = category
                                nextFragment.saloonId = saloonId.toString()
                                (activity as HomeActivity?)?.loadFragment(nextFragment)
                            }

                        } else {
                            if (saloonId != null && saloonId != 0) {
                                addSaloonService()
                            } else {
                                showPopupDialog()
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
        var adjustedPrice = Integer.parseInt(binding.edtPrice.text.toString()) * 100
        var imagePart: MultipartBody.Part? = null
        if (image_uri != null) {
            val imageFile =
                Helper.getFileFromUri(requireContext(), image_uri!!) ?: return // Get file from URI
            val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
            imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
        }
        // Create text-based request bodies
        val categoryId = RequestBody.create("text/plain".toMediaTypeOrNull(), "$categoryId")
        val saloonId = RequestBody.create("text/plain".toMediaTypeOrNull(), "$saloonId")
        val name = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            binding.edtServiceName.text.toString()
        )
        val duration = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            binding.edtDuration.text.toString()
        )
        val price =
            RequestBody.create("text/plain".toMediaTypeOrNull(), "$adjustedPrice")
        viewModel.addService(
            imagePart, categoryId, saloonId, name, duration, price
        )
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

    private fun getSaloons() {
        viewModel.getSaloon.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
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
                        setSaloonAdopter()
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
        // (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun addSaloonService() {
        viewModel.addSaloonService.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        if (saveAdd) {
                            binding.edtServiceName.text =
                                Editable.Factory.getInstance().newEditable("")
                            binding.edtPrice.text = Editable.Factory.getInstance().newEditable("")
                            binding.edtDuration.text =
                                Editable.Factory.getInstance().newEditable("")
                            binding.edtSaloon.text = Editable.Factory.getInstance().newEditable("")
                            requireView().snackbar("Service added successfully")
                        } else {
                            Appelement.reload = true
                            (activity as HomeActivity?)?.popFragment()
                            requireView().snackbar("Service enabled successfully")
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
        var saloonData = arrayListOf<AddSaloonService>()
        saloonData.add(
            AddSaloonService(
                saloonId.toString(),
                serviceId.toString(),
                true.toString(),
                binding.edtDuration.text.toString().toInt(),
                binding.edtPrice.text.toString().toInt() * 100
            )
        )

        viewModel.addSaloonService(
            AddServiceSaloonRequest(saloonData)
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
}