package com.tt.muzien.ui.auth

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.tt.muzien.R
import com.tt.muzien.constants.Keys
import com.tt.muzien.data.dto.LoggedInInfo
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.data.requests.UpdateUser
import com.tt.muzien.databinding.FragmentSignupBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.snackbar
import com.tt.muzien.ui.startNewActivity
import com.tt.muzien.utilities.InputValidator
import com.tt.muzien.utilities.PreferenceManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class FragmentSignup : BaseFragment<AuthViewModel, FragmentSignupBinding, AuthRepository>() {
    var role: String? = null
    var country: String? = null
    private val REQUEST_CAMERA = 1
    private val REQUEST_GALLERY = 2
    private val REQUEST_PERMISSIONS = 3
    private var image_uri: Uri? = null
    var closeApp: Boolean = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AuthActivity?)?.changeBackground(Color.WHITE)
        binding.countrySpinner.setCountryForNameCode("SA")
        binding.llBack?.setOnClickListener {
            (activity as AuthActivity?)?.popFragment()
            if (closeApp) {
                (activity as AuthActivity?)?.popFragment()
                (activity as AuthActivity?)?.popFragment()
            }
        }
        binding.rdoRole.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.RdoOwner) {
                role = "business-owner"
                binding.llCountry.visibility = View.GONE
                binding.txtCountryLabel.visibility = View.GONE
                binding.RdoOwner.background = resources.getDrawable(R.drawable.blue_rounded)
                binding.RdoOwner.setTextColor(resources.getColor(R.color.white))
                binding.rdoProvider.background =
                    resources.getDrawable(R.drawable.rounded_white_grey)
                binding.rdoProvider.setTextColor(resources.getColor(R.color.colorTextDefault))
            } else {
                role = "service-provider"
                binding.llCountry.visibility = View.VISIBLE
                binding.txtCountryLabel.visibility = View.VISIBLE
                binding.rdoProvider.background = resources.getDrawable(R.drawable.blue_rounded)
                binding.rdoProvider.setTextColor(resources.getColor(R.color.white))
                binding.RdoOwner.background = resources.getDrawable(R.drawable.rounded_white_grey)
                binding.RdoOwner.setTextColor(resources.getColor(R.color.colorTextDefault))
            }
            // checkValidation()
        }
        binding.countrySpinner.setOnCountryChangeListener {
            country = binding.countrySpinner.selectedCountryName
            // checkValidation()
        }
        binding.edtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                //checkValidation()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                // checkValidation()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        binding.imgCamera.setOnClickListener {

            uploadImage()
        }
        binding.imgProfilePic.setOnClickListener {

            uploadImage()
        }
        binding.llSave.setOnClickListener {

            if (checkValidation()) {
                LoggedInInfo.fullName = binding.edtName.text.toString()
                LoggedInInfo.email = binding.edtEmail.text.toString()
                LoggedInInfo.role = role
                LoggedInInfo.nationality = country
                if (image_uri != null) {
                    uploadPhoto()
                } else {
                    updateUser()
                }
//                val userPreferences = PreferenceManager.getInstance(requireActivity())
//                userPreferences.putString(Keys.Access_Token, "Faheem")
//                val activity = HomeActivity::class.java
//                requireActivity().startNewActivity(activity)
//                requireActivity().finish()
            }
        }
        // checkValidation()
    }

    private fun checkValidation(): Boolean {
        var isValid = true
        if (binding.edtName.text.toString() == "") {
            binding.txtNameError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.txtNameError.visibility = View.GONE
        }
        if (binding.edtEmail.text.toString() == "") {
            binding.txtEmailError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.txtEmailError.visibility = View.GONE
        }
        if (!InputValidator.isValidEmail(
                binding.edtEmail.text.toString()
            )
        ) {
            binding.txtEmailError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.txtEmailError.visibility = View.GONE
        }
        if (role == "") {
            binding.txtRoleError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.txtRoleError.visibility = View.GONE
        }

        if (role == "Service Provider" && country == "") {
            binding.txtCountryError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.txtCountryError.visibility = View.GONE
        }

        return isValid
    }

    override fun getViewModel(): Class<AuthViewModel> {
        return AuthViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSignupBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        AuthRepository(
            remoteDataSource.buildApi(AuthApi::class.java, requireContext()),
            userPreferences
        )

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        (activity as AuthActivity?)?.changeStatusBarColor(R.color.colorPrimary)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        (activity as AuthActivity?)?.changeStatusBarColor(R.color.white)
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
                        binding.imgProfilePic.setImageURI(image_uri)


                    }

                }

                REQUEST_GALLERY -> {
                    val selectedImageUri: Uri? = data?.data
                    if (selectedImageUri != null) {
                        image_uri = selectedImageUri
                    }
                    binding.imgProfilePic.setImageURI(selectedImageUri)

                }
            }
        }
    }

    private fun updateUser() {
        viewModel.updateUser.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as AuthActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        LoggedInInfo.user = it.value.data.user
                        val gson = Gson()
                        val userInfo = gson.toJson(it.value.data.user)
                        PreferenceManager.getInstance(requireActivity())
                            .putString(Keys.User, userInfo)
                        if (it.value.data.user.fullName == null) {
                            var nextFragment = FragmentSignup()
                            (activity as AuthActivity?)?.loadFragment(nextFragment)

                        } else {
                            val activity = HomeActivity::class.java
                            requireActivity().startNewActivity(activity)
                            requireActivity().finish()
//                            Toast.makeText(requireContext(), "Login success", Toast.LENGTH_SHORT)
//                                .show()
                        }
                    } else {
                        requireView().snackbar(it.value.message)
                    }
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())

                    (activity as AuthActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        var request = UpdateUser(
            LoggedInInfo.fullName,
            LoggedInInfo.email, LoggedInInfo.nationality, LoggedInInfo.role
        )
        viewModel.updateUser(request)
        (activity as AuthActivity?)?.showLoadingIndicator()
    }

    private fun uploadPhoto() {
        viewModel.uploadPhoto.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as AuthActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        updateUser()
                    } else {
                        requireView().snackbar(it.value.message)
                    }
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())

                    (activity as AuthActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        val imageFile = getFileFromUri(requireContext(), image_uri!!) ?: return // Get file from URI
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        viewModel.uploadImage(imagePart)
        (activity as AuthActivity?)?.showLoadingIndicator()
    }

    fun getFileFromUri(context: Context, uri: Uri): File? {
        val contentResolver: ContentResolver = context.contentResolver
        val fileName = "temp_image_${System.currentTimeMillis()}.jpg" // Change extension as needed
        val tempFile = File(context.cacheDir, fileName)

        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            tempFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}