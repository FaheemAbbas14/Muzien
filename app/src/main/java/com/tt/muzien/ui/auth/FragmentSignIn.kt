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
                        .replace(" ", "")
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
            binding.edtPhoneNumber.hint =
                Editable.Factory.getInstance()
                    .newEditable(InputValidator.getPhoneNumberPlaceholder(binding.countrySpinner.selectedCountryNameCode))


        } else {
            binding.countrySpinner.setCountryForNameCode("SA")
            selectedCountry = binding.countrySpinner.selectedCountryName
            binding.txtCountryCode.text =
                Editable.Factory.getInstance().newEditable("+966")
            binding.edtPhoneNumber.hint =
                Editable.Factory.getInstance()
                    .newEditable(InputValidator.getPhoneNumberPlaceholder(binding.countrySpinner.selectedCountryNameCode))

        }
        binding.countrySpinner.setOnCountryChangeListener {
            if (selectedCountry != binding.countrySpinner.selectedCountryName) {
                selectedCountry = binding.countrySpinner.selectedCountryName
                val countryCode = binding.countrySpinner.selectedCountryCode
                binding.txtCountryCode.text =
                    Editable.Factory.getInstance().newEditable("+$countryCode")
                binding.edtPhoneNumber.text = Editable.Factory.getInstance()
                    .newEditable("")

                binding.edtPhoneNumber.hint =
                    Editable.Factory.getInstance()
                        .newEditable(InputValidator.getPhoneNumberPlaceholder(binding.countrySpinner.selectedCountryNameCode))
            }
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
                selectedCountry, binding.edtPhoneNumber.text.toString().replace(" ", "")
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
        var text = resources.getString(R.string.don_t_have_any_account_sign_up)

        if (isFromSignup) {
            text = getString(R.string.already_have_an_account_sign_in)
            binding.txtAction.text = resources.getString(R.string.verify_your_number)
            binding.txtLabel.text = getString(R.string.sign_up)
            binding.txtText.text = getString(R.string.we_will_use_your_phone_number_to)

        } else {
            binding.txtAction.text = resources.getString(R.string.login)
            binding.txtLabel.text = resources.getString(R.string.login)
            binding.txtText.text = resources.getString(R.string.enter_your_phone_number_to_login)
        }
        var spannableString = SpannableString(text)

        // Make "here" clickable and change its color
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                isFromSignup = !isFromSignup
                setdata()
            }
        }

        spannableString.setSpan(
            clickableSpan,
            text.length - 8,
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(requireActivity().getColor(R.color.colorPrimary)),
            text.length - 8, text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.txtType.text = spannableString
        binding.txtType.movementMethod = android.text.method.LinkMovementMethod.getInstance()

        var privacyText = getString(R.string.by_continuing_you_agree_to_our_t_c_and_privacy_policy)
        spannableString = SpannableString(privacyText)

        // Make "here" clickable and change its color
        val tcSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(requireContext(), getString(R.string.tc), Toast.LENGTH_SHORT).show()
            }
        }
        val privacySpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(requireContext(), getString(R.string.privacy), Toast.LENGTH_SHORT)
                    .show()

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
        container: ViewGroup?,
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
                        nextFragment.phone = LoggedInInfo.phoneNumber ?: ""
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
                        nextFragment.phone = LoggedInInfo.phoneNumber ?: ""
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