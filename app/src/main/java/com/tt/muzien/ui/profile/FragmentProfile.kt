package com.tt.muzien.ui.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tt.muzien.R
import com.tt.muzien.constants.Keys
import com.tt.muzien.data.dto.LoggedInInfo
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentProfileBinding
import com.tt.muzien.ui.auth.AuthActivity
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.ui.startNewActivity
import com.tt.muzien.utilities.Helper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.Locale


class FragmentProfile : BaseFragment<HomeViewModel, FragmentProfileBinding, HomeRepository>() {
    private val REQUEST_CAMERA = 1
    private val REQUEST_GALLERY = 2
    private val REQUEST_PERMISSIONS = 3
    private var image_uri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setUserRepo((activity as HomeActivity?)?.getUserRepo()!!)
        setUserData()
        val isArabic = Locale.getDefault().language == "ar"
        if (isArabic) {
            binding.txtSelectedLanguage.text = resources.getString(R.string.arabic)
        } else {
            binding.txtSelectedLanguage.text = resources.getString(R.string.english)
        }
        if (LoggedInInfo.user?.role == "super-admin") {
            binding.llSubscribtion.visibility = View.GONE
        } else if (LoggedInInfo.user?.role == "service-provider") {
            binding.llSubscribtion.visibility = View.GONE
            if (LoggedInInfo.user?.status == "member") {
                binding.llServices.visibility = View.VISIBLE
                binding.llHolidays.visibility = View.VISIBLE
            }
            else{
                binding.llServices.visibility = View.GONE
                binding.llHolidays.visibility = View.GONE
            }
        } else {
            binding.llServices.visibility = View.GONE
            binding.llHolidays.visibility = View.GONE
        }
        binding.imgCamera.setOnClickListener {

            uploadImage()
        }
        binding.imgProfilePic.setOnClickListener {

            uploadImage()
        }
        binding.llMyAccount.setOnClickListener {

            var nextFragment = FragmentMyAccount()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.llNotification.setOnClickListener {

            var nextFragment = FragmentNotificationSettings()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.llSubscribtion.setOnClickListener {

            var nextFragment = FragmentManageSubscription()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.llServices.setOnClickListener {

            var nextFragment = FragmentAddAdminServices()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.llHolidays.setOnClickListener {

            var nextFragment = FragmentAddAdminHolidays()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.showTabs()
            (activity as HomeActivity?)?.popFragment()
        }
        binding.llLogout.setOnClickListener {
            logout()
        }
        binding.llChangeLanguage.setOnClickListener {
            val bottomSheet = FragmentChangeLanguage()
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }
    }

    fun setUserData() {
        binding.txtName.text = "${LoggedInInfo.user?.fullName}"
        binding.txtProfession.text = "${LoggedInInfo.user?.role}"
        Glide.with(binding.imgProfilePic)
            .load(LoggedInInfo.user?.picture)
            .circleCrop()
            .placeholder(R.drawable.user_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // Cache both original & transformed image
            .skipMemoryCache(false)  // Cache in memory
            .into(binding.imgProfilePic)
    }


    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentProfileBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(
            remoteDataSource.buildApi(HomeApi::class.java, requireContext()),
            userPreferences
        )

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.hideTabs()
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
                    uploadPhoto()
                }

                REQUEST_GALLERY -> {
                    val selectedImageUri: Uri? = data?.data
                    if (selectedImageUri != null) {
                        image_uri = selectedImageUri
                    }
                    binding.imgProfilePic.setImageURI(selectedImageUri)
                    uploadPhoto()
                }
            }
        }
    }

    private fun uploadPhoto() {
        viewModel.uploadPhoto.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    // (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        // LoggedInInfo.user?.picture= it.value.data?.user?.picture!!
                        (activity as HomeActivity?)?.getUserData()
                        requireView().snackbar("Profile photo updated")
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
        val imageFile =
            Helper.getFileFromUri(requireContext(), image_uri!!) ?: return // Get file from URI
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        viewModel.uploadImage(imagePart)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun logout() {
        viewModel.logout.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        userPreferences.putString(Keys.Access_Token, "")
                        requireActivity().startNewActivity(AuthActivity::class.java)
                        requireActivity().finish()
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
        viewModel.logout()
        // (activity as HomeActivity?)?.showLoadingIndicator()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as HomeActivity?)?.hideTabs()
        }
    }
}