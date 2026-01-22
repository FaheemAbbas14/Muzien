package com.tt.muzien.ui.profile

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.tt.muzien.R
import com.tt.muzien.data.dto.LoggedInInfo
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.data.requests.LoginRequest
import com.tt.muzien.data.requests.UpdateUser
import com.tt.muzien.databinding.FragmentMyAccountBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.Helper.closeKeyboard
import com.tt.muzien.utilities.InputValidator


class FragmentMyAccount : BaseFragment<HomeViewModel, FragmentMyAccountBinding, HomeRepository>() {
    var country: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setUserRepo((activity as HomeActivity?)?.getUserRepo()!!)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        if (LoggedInInfo.user?.phoneNumber != null) {
            val result = splitCountryCode(LoggedInInfo.user?.phoneNumber!!)

            result?.let {
                var countryCode = it.first   // "+92"
                val localNumber = it.second  // "3451234567"
                binding.txtCountryCode.text = countryCode

                countryCode = countryCode.replace("+", "")
                binding.edtPhoneNumber.text =
                    Editable.Factory.getInstance().newEditable(localNumber)
                binding.countrySpinner.setCountryForPhoneCode(Integer.parseInt(countryCode))
            }

            binding.edtPhoneNumber.hint =
                Editable.Factory.getInstance()
                    .newEditable(InputValidator.getPhoneNumberPlaceholder(binding.countrySpinner.selectedCountryNameCode))
        }
        binding.edtEmail.text =
            Editable.Factory.getInstance().newEditable("${LoggedInInfo.user?.email}")
        binding.edtName.text =
            Editable.Factory.getInstance().newEditable("${LoggedInInfo.user?.fullName}")
        binding.countrySpinner.setOnCountryChangeListener {
            country = binding.countrySpinner.selectedCountryName
            val countryCode = binding.countrySpinner.selectedCountryCode
            binding.txtCountryCode.text = "+$countryCode"
            binding.edtPhoneNumber.hint =
                Editable.Factory.getInstance()
                    .newEditable(InputValidator.getPhoneNumberPlaceholder(binding.countrySpinner.selectedCountryNameCode))

            checkValidation()
        }
        binding.edtName.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val inputText = v.text.toString().trim()

                if (inputText.isNotEmpty() && inputText != LoggedInInfo.user?.fullName) {
                    // callSearchApi(inputText)  // Replace with your API function
                    updateProfile(inputText, LoggedInInfo.user?.email?:"")
                }
                // ✅ Hide keyboard
                closeKeyboard(requireContext())
                true  // consume action
            } else {
                false
            }
        }
        binding.edtEmail.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val inputText = v.text.toString().trim()

                if (inputText.isNotEmpty() && inputText != LoggedInInfo.user?.email && InputValidator.isValidEmail(
                        inputText
                    )
                ) {
                    updateProfile(LoggedInInfo.user?.fullName?:"", inputText)
                }
                // ✅ Hide keyboard
                closeKeyboard(requireContext())
                true  // consume action
            } else {
                false
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


                checkValidation()


            }
        })
        binding.txtUpdate.setOnClickListener {
            if (checkValidation()) {
                var phone =
                    binding.txtCountryCode.text.toString() + binding.edtPhoneNumber.text.toString()
                        .replace(" ", "")

                sendOtpNormal(phone)

            }
        }
    }

    fun checkValidation(): Boolean {
        var isValid = false

        if (binding.edtPhoneNumber.text.isNotEmpty() && InputValidator.isValidPhoneNumber(
                binding.txtCountryCode.text.toString(), binding.edtPhoneNumber.text.toString().replace(" ", "")
            ) && LoggedInInfo.user?.phoneNumber != binding.txtCountryCode.text.toString() + binding.edtPhoneNumber.text.toString()
                .replace(" ", "")
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
        container: ViewGroup?,
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as HomeActivity?)?.hideTabs()
        }
    }

    override fun onPause() {
        super.onPause()
        //  (activity as HomeActivity?)?.showTabs()
    }

    fun splitCountryCode(phone: String): Pair<String, String>? {
        if (!phone.startsWith("+") || phone.length < 5) return null

        val normalized = phone.replace(" ", "").replace("-", "")

        // Try 4-digit country code first
        val code4 = normalized.substring(1, minOf(5, normalized.length))
        if (isValidCountryCode(code4)) {
            return "+$code4" to normalized.substring(5)
        }

        // Fallback to 3-digit country code
        val code3 = normalized.substring(1, minOf(4, normalized.length))
        if (isValidCountryCode(code3)) {
            return "+$code3" to normalized.substring(4)
        }

        return null
    }
    private fun isValidCountryCode(code: String): Boolean {
        val knownCodes = setOf(
            "91", "92", "971", "966", "1", "44", "49", "33",
            "234", "880", "977", "852", "965", "964", "974",
            "965", "973", "962", "968", "970", "972", "971"
        )

        return knownCodes.contains(code)
    }

    private fun sendOtpNormal(data: String) {
        LoggedInInfo.phoneNumber = data
        viewModel.sendOtp.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {

                        LoggedInInfo.userId = it.value.data?.otp?.userId!!
                        var nextFragment = FragmentVerifyOTP()
                        nextFragment.isFromEdit = true
                        nextFragment.phone = LoggedInInfo.phoneNumber ?: ""
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

    private fun updateProfile(name: String, email: String) {
        LoggedInInfo.user?.fullName=name
        LoggedInInfo.user?.email=email
        viewModel.updateUser.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.profile_updated), Toast.LENGTH_SHORT
                        ).show()
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
        var request = UpdateUser(name, email)
        viewModel.updateUser(request)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
}