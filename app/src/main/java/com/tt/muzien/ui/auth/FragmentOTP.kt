package com.tt.muzien.ui.auth

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
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
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.tt.muzien.R
import com.tt.muzien.constants.Keys
import com.tt.muzien.data.dto.LoggedInInfo
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.data.requests.VerifyOTPRequest
import com.tt.muzien.databinding.FragmentOTPBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.snackbar
import com.tt.muzien.ui.startNewActivity
import com.tt.muzien.utilities.PreferenceManager

class FragmentOTP : BaseFragment<AuthViewModel, FragmentOTPBinding, AuthRepository>() {
    private var countDownTimer: CountDownTimer? = null
    private var timeFormatted: String = ""
    private var resendEnabled: Boolean = false
    private var timeInMillis: Long = 30000
    private var timeLeftInMillis: Long = 30000
    var phone: String = ""
    var otp: String = ""
    var isFromSignup: Boolean = false
    var isFromEdit: Boolean = false
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AuthActivity?)?.changeBackground(Color.WHITE)
        binding.llBack.setOnClickListener {
            (activity as AuthActivity?)?.popFragment()
        }
        setdata()
        binding.edtInput1.requestFocus()
        binding.llNext.setOnClickListener {
            if (checkValidation()) {
                var enteredOTP =
                    binding.edtInput1.text.toString() + binding.edtInput2.text.toString() + binding.edtInput3.text.toString() + binding.edtInput4.text.toString()
                loginVerify(enteredOTP)

            }
        }
        binding.edtInput1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.isNotEmpty()) {
                    binding.edtInput1.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_white_primary))
                    binding.edtInput2.requestFocus()
                } else {
                    binding.edtInput1.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_white_grey))
                }

                // checkValidation()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        binding.edtInput2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.isNotEmpty()) {
                    binding.edtInput2.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_white_primary))
                    binding.edtInput3.requestFocus()
                } else {
                    binding.edtInput2.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_white_grey))
                    binding.edtInput1.requestFocus()
                }
                // checkValidation()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        binding.edtInput3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.isNotEmpty()) {
                    binding.edtInput3.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_white_primary))
                    binding.edtInput4.requestFocus()
                } else {
                    binding.edtInput3.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_white_grey))
                    binding.edtInput2.requestFocus()
                }
                // checkValidation()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        binding.edtInput4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.isNotEmpty()) {
                    binding.edtInput4.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_white_primary))
                } else {
                    binding.edtInput4.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_grey))
                    binding.edtInput3.requestFocus()
                }
                //  checkValidation()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        // checkValidation()
        startTimer()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimer()
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onFinish() {
                resendEnabled = true
                setdata()
            }
        }.start()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun resetTimer() {
        countDownTimer?.cancel()
        timeLeftInMillis = timeInMillis
        startTimer()
        updateTimer()
        resendEnabled = false
        setdata()

    }

    private fun resetBoxes() {

        binding.edtInput1.text = Editable.Factory.getInstance().newEditable("")
        binding.edtInput2.text = Editable.Factory.getInstance().newEditable("")
        binding.edtInput3.text = Editable.Factory.getInstance().newEditable("")
        binding.edtInput4.text = Editable.Factory.getInstance().newEditable("")

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateTimer() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        timeFormatted = String.format("%02d:%02d", minutes, seconds)
        if (seconds > 0) {
            resendEnabled = false
        } else {
            timeFormatted = ""
            resendEnabled = true
        }
        try {
            setdata()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkValidation(): Boolean {
        var isValid = false
        if (binding.edtInput1.text.isNotEmpty() && binding.edtInput2.text.isNotEmpty() && binding.edtInput3.text.isNotEmpty() && binding.edtInput4.text.isNotEmpty()) {
            isValid = true
        }
        if (!isValid) {
            binding.txtError.visibility = View.VISIBLE
        } else {
            binding.txtError.visibility = View.GONE
        }
        return isValid
    }

    override fun getViewModel(): Class<AuthViewModel> {
        return AuthViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentOTPBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        AuthRepository(
            remoteDataSource.buildApi(AuthApi::class.java, requireContext()),
            userPreferences
        )

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setdata() {
        try {


            var text = "Didn’t receive code? Resend ${
                if (timeFormatted != "") "in $timeFormatted" else {
                    ""
                }
            }"
            var spannableString = SpannableString(text)

            var start = 21
            spannableString.setSpan(
                ForegroundColorSpan(requireActivity().getColor(R.color.colorPrimary)),
                start, start + 7,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (resendEnabled) {
                // Make "here" clickable and change its color
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        binding.edtInput1.requestFocus()
                        resetTimer()
                        resetBoxes()
                        resendEnabled = false
                        setdata()
                    }
                }

                spannableString.setSpan(
                    clickableSpan,
                    start,
                    start + 6,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )


            }
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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

    private fun loginVerify(data: String) {
        viewModel.verifyLogin.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    //  (activity as AuthActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        val userPreferences = PreferenceManager.getInstance(requireActivity())
                        userPreferences.putString(Keys.Access_Token, it.value.data.token)
                        userPreferences.putString(
                            Keys.Refresh_Token,
                            it.value.data.refreshToken
                        )
                        getUserData()
                    } else {
                        (activity as AuthActivity?)?.hideLoadingIndicator()
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
        var request = VerifyOTPRequest(phone, data)
        viewModel.verifyLogin(request)
        (activity as AuthActivity?)?.showLoadingIndicator()
    }

    private fun getUserData() {
        viewModel.my.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as AuthActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        LoggedInInfo.user = it.value.data?.user
                        val gson = Gson()
                        val userInfo = gson.toJson(it.value.data?.user)
                        PreferenceManager.getInstance(requireActivity())
                            .putString(Keys.User, userInfo)
                        if (it.value.data?.user?.fullName == null) {
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
        viewModel.my(LoggedInInfo.userId)
        // (activity as AuthActivity?)?.showLoadingIndicator()
    }
}