package com.tt.muzien.ui.auth

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.tt.muzien.R
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.databinding.FragmentOTPBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.enable

class FragmentOTP : BaseFragment<AuthViewModel, FragmentOTPBinding, AuthRepository>() {
    private var countDownTimer: CountDownTimer? = null
    private var timeFormatted: String = ""
    private var resendEnabled: Boolean = false
    private var timeInMillis: Long = 30000
    private var timeLeftInMillis: Long = 30000
    var phone: String = ""
    var isFromSignup: Boolean = false

    @RequiresApi(Build.VERSION_CODES.M)
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.llBack.setOnClickListener {
            (activity as AuthActivity?)?.popFragment()
        }
        setdata()
        binding.edtInput1.requestFocus()
        binding.llNext.setOnClickListener {
            if (isFromSignup) {
                var nextFragment = FragmentSignup()
                (activity as AuthActivity?)?.loadFragment(nextFragment)
            } else {
                Toast.makeText(requireContext(), "Login success", Toast.LENGTH_SHORT).show()
            }
        }
        binding.edtInput1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.isNotEmpty()) {
                    binding.edtInput2.requestFocus()
                }

                checkValidation()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        binding.edtInput2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.isNotEmpty()) {
                    binding.edtInput3.requestFocus()
                } else {
                    binding.edtInput1.requestFocus()
                }
                checkValidation()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        binding.edtInput3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.isNotEmpty()) {
                    binding.edtInput4.requestFocus()
                } else {
                    binding.edtInput2.requestFocus()
                }
                checkValidation()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        binding.edtInput4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.isNotEmpty()) {

                } else {
                    binding.edtInput3.requestFocus()
                }
                checkValidation()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        checkValidation()
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

    private fun checkValidation() {
        if (binding.edtInput1.text.isNotEmpty() && binding.edtInput2.text.isNotEmpty() && binding.edtInput3.text.isNotEmpty() && binding.edtInput4.text.isNotEmpty()) {
            binding.llNext.enable(true)
        } else {
            binding.llNext.enable(false)
        }
    }

    override fun getViewModel(): Class<AuthViewModel> {
        return AuthViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentOTPBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        AuthRepository(remoteDataSource.buildApi(AuthApi::class.java), userPreferences)

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setdata() {
        try {


            var text = "Didn’t receive code? Resend ${
                if (timeFormatted != "") "in $timeFormatted" else {
                    ""
                }
            }"
            var spannableString = SpannableString(text)
            if (resendEnabled) {
                var start = 21
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
                spannableString.setSpan(
                    ForegroundColorSpan(requireActivity().getColor(R.color.colorPrimary)),
                    start, start + 7,
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
}