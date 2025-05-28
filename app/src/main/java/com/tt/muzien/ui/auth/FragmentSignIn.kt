package com.tt.muzien.ui.auth

import android.graphics.Color
import android.os.Build
import android.os.Bundle
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
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import com.tt.muzien.R
import com.tt.muzien.data.dto.LoggedInInfo
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.data.requests.LoginRequest
import com.tt.muzien.data.requests.RegisterRequest
import com.tt.muzien.databinding.FragmentSignInBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.InputValidator


class FragmentSignIn : BaseFragment<AuthViewModel, FragmentSignInBinding, AuthRepository>() {
    var isFromSignup: Boolean = false
    private lateinit var selectedCountry: String

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AuthActivity?)?.changeBackground(Color.WHITE)
        setdata()
        binding.llLogin.setOnClickListener {
            if (checkValidation()) {
                var phone =
                    binding.txtCountryCode.text.toString() + binding.edtPhoneNumber.text.toString()
                if (isFromSignup) {
                    register(phone)
                } else {
                    sendOtpNormal(phone)
                }
            }
        }
        if (LoggedInInfo.phoneNumber != null && LoggedInInfo.phoneNumber != "") {
            var (countryCode, phoneNumber) = splitString(LoggedInInfo.phoneNumber!!)
            binding.txtCountryCode.text = countryCode
            countryCode = countryCode.replace("+", "")
            binding.edtPhoneNumber.text =
                Editable.Factory.getInstance().newEditable(phoneNumber)
            binding.countrySpinner.setCountryForPhoneCode(Integer.parseInt(countryCode))
            selectedCountry = binding.countrySpinner.selectedCountryName
        } else {
            binding.countrySpinner.setCountryForNameCode("SA")
            selectedCountry = binding.countrySpinner.selectedCountryName
            binding.txtCountryCode.text =
                Editable.Factory.getInstance().newEditable("+966")
        }
        binding.countrySpinner.setOnCountryChangeListener {
            selectedCountry = binding.countrySpinner.selectedCountryName
            val countryCode = binding.countrySpinner.selectedCountryCode
            binding.txtCountryCode.text =
                Editable.Factory.getInstance().newEditable("+$countryCode")
        }
        binding.edtPhoneNumber.addTextChangedListener(object : TextWatcher {
            var length_before = 0
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                length_before = s.length
            }

            override fun afterTextChanged(s: Editable) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                binding.txtError.visibility = View.GONE
                //checkValidation()


            }
        })
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val fragmentCount = requireActivity().supportFragmentManager.backStackEntryCount
                    (activity as AuthActivity?)?.popFragment()
                    if (fragmentCount == 1) {
                        requireActivity().finish()
                    }

                }
            })
    }

    private fun checkValidation(): Boolean {
        var isValid = false

        if (binding.edtPhoneNumber.text.isNotEmpty() && InputValidator.isValidPhoneNumber(
                selectedCountry,
                binding.txtCountryCode.text.toString() + binding.edtPhoneNumber.text.toString()
            )
        ) {
            isValid = true
        }

        if (!isValid) {
            binding.txtError.visibility = View.VISIBLE
        } else {
            binding.txtError.visibility = View.GONE
        }
        return isValid

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setdata() {
        var text = "Don’t have any account? Sign up"
        var start = 24
        if (isFromSignup) {
            start = 24
            text = "Already have an account? Sign in"
            binding.txtAction.text = "Verify Your Number"
            binding.txtLabel.text = "Sign up"
            binding.txtText.text = "We will use your phone number to\n" +
                    "register and log into the app."

        } else {
            binding.txtAction.text = "Login"
            binding.txtLabel.text = "Login"
            binding.txtText.text = "Enter your phone number to login"
        }
        var spannableString = SpannableString(text)

        // Make "here" clickable and change its color
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                isFromSignup = !isFromSignup
                setdata()
            }
        }

        spannableString.setSpan(clickableSpan, start, start + 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(
            ForegroundColorSpan(requireActivity().getColor(R.color.colorPrimary)),
            start, start + 7,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.txtType.text = spannableString
        binding.txtType.movementMethod = android.text.method.LinkMovementMethod.getInstance()

        var privacyText = "By continuing you agree to our\n" +
                "T&C and Privacy Policy"
        spannableString = SpannableString(privacyText)

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
        binding.txtPrivacy.movementMethod = android.text.method.LinkMovementMethod.getInstance()
    }

    override fun getViewModel(): Class<AuthViewModel> {
        return AuthViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSignInBinding.inflate(inflater, container, false)

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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as AuthActivity?)?.changeStatusBarColor(R.color.white)
        }
    }
    private fun sendOtpNormal(data: String) {

        LoggedInInfo.phoneNumber = data
        viewModel.login.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as AuthActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        LoggedInInfo.userId = it.value.data?.otp?.userId!!
                        var nextFragment = FragmentOTP()
                        nextFragment.isFromSignup = isFromSignup
                        nextFragment.phone = data
                        nextFragment.expiryTime = it.value.data.otp.expiryDate
                        (activity as AuthActivity?)?.loadFragment(nextFragment)
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
        var request = LoginRequest(data)
        viewModel.sendOTP(request)
        (activity as AuthActivity?)?.showLoadingIndicator()
    }

    private fun register(data: String) {

        LoggedInInfo.phoneNumber = data
        viewModel.register.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as AuthActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        LoggedInInfo.userId = it.value.data.otp.userId
                        var nextFragment = FragmentOTP()
                        nextFragment.isFromSignup = isFromSignup
                        nextFragment.phone = data
                        nextFragment.expiryTime = it.value.data.otp.expiryDate
                        (activity as AuthActivity?)?.loadFragment(nextFragment)
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
        var request = RegisterRequest(data, "service-provider")
        viewModel.register(request)
        (activity as AuthActivity?)?.showLoadingIndicator()
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
}