package com.tt.muzien.ui.auth

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tt.muzien.R
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.databinding.FragmentSignupBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.enable
import com.tt.muzien.utilities.InputValidator


class FragmentSignup : BaseFragment<AuthViewModel, FragmentSignupBinding, AuthRepository>() {
    var role: String = ""
    var country: String = ""
    private val REQUEST_CAMERA = 1
    private val REQUEST_GALLERY = 2
    private val REQUEST_PERMISSIONS = 3
    private var image_uri: Uri? = null

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.rdoRole.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.RdoOwner) {
                role = "Owner"
                binding.llCountry.visibility = View.GONE
                binding.txtCountryLabel.visibility = View.GONE
                binding.RdoOwner.background = resources.getDrawable(R.drawable.blue_rounded)
                binding.RdoOwner.setTextColor(resources.getColor(R.color.white))
                binding.rdoProvider.background =
                    resources.getDrawable(R.drawable.rounded_white_grey)
                binding.rdoProvider.setTextColor(resources.getColor(R.color.colorTextDefault))
            } else {
                role = "Service Provider"
                binding.llCountry.visibility = View.VISIBLE
                binding.txtCountryLabel.visibility = View.VISIBLE
                binding.rdoProvider.background = resources.getDrawable(R.drawable.blue_rounded)
                binding.rdoProvider.setTextColor(resources.getColor(R.color.white))
                binding.RdoOwner.background = resources.getDrawable(R.drawable.rounded_white_grey)
                binding.RdoOwner.setTextColor(resources.getColor(R.color.colorTextDefault))
            }
            checkValidation()
        }
        binding.countrySpinner.setOnCountryChangeListener {
            country = binding.countrySpinner.selectedCountryName
            checkValidation()
        }
        binding.edtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                checkValidation()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                checkValidation()
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
        checkValidation()
    }

    private fun checkValidation() {
        var isValid = false
        if (binding.edtName.text.toString() != "" && binding.edtEmail.text.toString() != "" && InputValidator.isValidEmail(
                binding.edtEmail.text.toString()
            ) && role != ""
        ) {
            isValid = true
        }
        if (role == "Service Provider" && country == "") {
            isValid = false
        }
        if (isValid) {
            binding.llSave.enable(true)
        } else {
            binding.llSave.enable(false)
        }
    }

    override fun getViewModel(): Class<AuthViewModel> {
        return AuthViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSignupBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        AuthRepository(remoteDataSource.buildApi(AuthApi::class.java), userPreferences)

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
            showImagePickerDialog()
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
                        //UserInfo.profilePic = selectedImageUri
                    }
                    binding.imgProfilePic.setImageURI(selectedImageUri)

                }
            }
        }
    }

}