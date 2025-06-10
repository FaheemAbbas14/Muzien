package com.moyasar.android.sdkdriver.customui.stcpay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.moyasar.android.sdk.creditcard.presentation.di.MoyasarAppContainer.viewModel
import com.moyasar.android.sdk.stcpay.presentation.model.STCPayViewState
import com.tt.muzien.databinding.FragmentEnterOTPCustomUIBinding
import com.tt.muzien.ui.home.HomeActivity

class EnterOTPCustomUIFragment : Fragment() {

    private lateinit var binding: FragmentEnterOTPCustomUIBinding
    private lateinit var parentActivity: FragmentActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        parentActivity = requireActivity()
        binding = FragmentEnterOTPCustomUIBinding.inflate(inflater, container, false)
        initView()
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        return binding.root
    }

    private fun initView() {
        (activity as HomeActivity?)?.hideLoadingIndicator()
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.llPayNow.setOnClickListener {
            val transactionURL = arguments?.getString(TRANSACTION_URL).orEmpty()
            viewModel.submitSTCPayOTP(
                transactionURL = transactionURL,
                otp = binding.edtOtp.text.toString()
            )
        }
        binding.edtOtp.doAfterTextChanged { text ->
            viewModel.stcPayOTPChanged(text)
        }
    }

    private fun setupObservers() {
        viewModel.stcPayStatus.observe(viewLifecycleOwner, ::handleOnStatusChanged)
        viewModel.inputFieldsValidatorLiveData.observe(viewLifecycleOwner) { inputFieldUIModel ->
            showInvalidOTPErrorMsg(inputFieldUIModel?.stcPayUIModel?.otpErrorMsg)
            handleFormValidationState(viewModel.inputFieldsValidatorLiveData.value?.stcPayUIModel?.isOTPValid)
        }
    }

    private fun handleOnStatusChanged(status: STCPayViewState?) {
        parentActivity.runOnUiThread {
            when (status) {
                is STCPayViewState.SubmittingSTCPayOTP -> {
                    (activity as HomeActivity?)?.showLoadingIndicator()
                    binding.llPayNow.isEnabled = false
                    binding.edtOtp.isEnabled = false
                }

                else -> Unit
            }
        }
    }

    private fun handleFormValidationState(isFormValid: Boolean?) {
        binding.llPayNow.isEnabled = isFormValid ?: false
    }

    private fun showInvalidOTPErrorMsg(errorMsg: String?) {
        binding.llPayNow.isEnabled = false
    }

    companion object {
        const val TRANSACTION_URL = "transactionURL"
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.setSystemWindow(true)
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
        (activity as HomeActivity?)?.hideTabs()
    }

    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.setSystemWindow(true)
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, false)
        (activity as HomeActivity?)?.showTabs()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as HomeActivity?)?.setSystemWindow(true)
            (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
            (activity as HomeActivity?)?.hideTabs()
        }
    }
}