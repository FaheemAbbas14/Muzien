package com.tt.muzien.ui.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tt.muzien.data.dto.LoggedInInfo
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.data.requests.LoginRequest
import com.tt.muzien.databinding.FragmentMyAccountBinding
import com.tt.muzien.ui.auth.FragmentOTP
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.InputValidator


class FragmentMyAccount : BaseFragment<HomeViewModel, FragmentMyAccountBinding, HomeRepository>() {
    var country: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setUserRepo((activity as HomeActivity?)?.getUserRepo()!!)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        var (countryCode, phoneNumber) = splitString(LoggedInInfo.user?.phoneNumber!!)
        binding.txtCountryCode.text = countryCode
        countryCode = countryCode.replace("+", "")
        binding.edtPhoneNumber.text =
            Editable.Factory.getInstance().newEditable(phoneNumber)
        binding.edtEmail.text =
            Editable.Factory.getInstance().newEditable("${LoggedInInfo.user?.email}")
        binding.edtName.text =
            Editable.Factory.getInstance().newEditable("${LoggedInInfo.user?.fullName}")
        binding.countrySpinner.setCountryForPhoneCode(Integer.parseInt(countryCode))
        binding.countrySpinner.setOnCountryChangeListener {
            country = binding.countrySpinner.selectedCountryName
            binding.txtCountryCode.text = binding.countrySpinner.selectedCountryCode
            checkValidation()
        }
        binding.edtPhoneNumber.addTextChangedListener(object : TextWatcher {
            var length_before = 0
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                length_before = s.length
            }

            override fun afterTextChanged(s: Editable) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {


                checkValidation()


            }
        })
        binding.txtUpdate.setOnClickListener {
            if (checkValidation()) {
                var phone =
                    binding.txtCountryCode.text.toString() + binding.edtPhoneNumber.text.toString()

                sendOtpNormal(phone)

            }
        }
    }

    fun checkValidation(): Boolean {
        var isValid = false

        if (binding.edtPhoneNumber.text.isNotEmpty() && InputValidator.isValidPhoneNumber(
                country,
                binding.txtCountryCode.text.toString() + binding.edtPhoneNumber.text.toString()
            ) && LoggedInInfo.user?.phoneNumber != binding.txtCountryCode.text.toString() + binding.edtPhoneNumber.text.toString()
        ) {
            isValid = true
        }

        if (isValid) {
            binding.txtUpdate.visibility = View.VISIBLE
        } else {
            binding.txtUpdate.visibility = View.GONE
        }
        return isValid
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMyAccountBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(
            remoteDataSource.buildApi(HomeApi::class.java, requireContext()),
            userPreferences
        )

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.hideTabs()
    }

    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.showTabs()
    }

    fun splitString(input: String): Pair<String, String> {
        return if (input.length >= 3) {
            val firstPart = input.substring(0, 3)  // Get first 3 characters
            val remainingPart = input.drop(3)      // Get the rest of the string
            Pair(firstPart, remainingPart)
        } else {
            Pair(
                input,
                ""
            )  // If input has less than 3 characters, return the full string and empty
        }
    }

    private fun sendOtpNormal(data: String) {
        viewModel.sendOtp.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {

                        LoggedInInfo.userId = it.value.data?.otp?.userId!!
                        var nextFragment = FragmentVerifyOTP()
                        nextFragment.isFromEdit = true
                        nextFragment.phone = data
                        (activity as HomeActivity?)?.loadFragment(nextFragment)
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
        var request = LoginRequest(data)
        viewModel.sendOTP(request)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

}