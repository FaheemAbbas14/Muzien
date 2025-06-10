package com.moyasar.android.sdkdriver.customui.stcpay


import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.moyasar.android.sdk.core.domain.entities.PaymentResult
import com.moyasar.android.sdk.core.exceptions.InvalidConfigException
import com.moyasar.android.sdk.creditcard.data.models.request.PaymentRequest
import com.moyasar.android.sdk.creditcard.presentation.di.MoyasarAppContainer
import com.moyasar.android.sdk.creditcard.presentation.di.MoyasarAppContainer.viewModel
import com.moyasar.android.sdk.stcpay.presentation.model.STCPayViewState
import com.moyasar.android.sdk.stcpay.presentation.view.fragments.EnterOTPFragment
import com.tt.muzien.R
import com.tt.muzien.databinding.FragmentEnterMobileNumberCustomUIBinding
import com.tt.muzien.ui.home.HomeActivity

class EnterMobileNumberCustomUIFragment : Fragment() {

    private lateinit var binding: FragmentEnterMobileNumberCustomUIBinding
    private lateinit var parentActivity: FragmentActivity

    companion object {
        fun newInstance(
            application: Application,
            paymentRequest: PaymentRequest,
            callback: (PaymentResult) -> Unit,
        ): EnterMobileNumberCustomUIFragment {
            val configError = paymentRequest.validate()
            if (configError.any()) {
                throw InvalidConfigException(configError)
            }
            MoyasarAppContainer.initialize(application, paymentRequest, callback)
            return EnterMobileNumberCustomUIFragment()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        parentActivity = requireActivity()
        binding = FragmentEnterMobileNumberCustomUIBinding.inflate(inflater, container, false)
        initView()
        setupObservers()
        binding.setupListeners()
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        return binding.root
    }

    private fun setupObservers() {
        viewModel.stcPayStatus.observe(
            viewLifecycleOwner,
            ::handleOnStatusChanged
        )
        viewModel.inputFieldsValidatorLiveData.observe(viewLifecycleOwner) { inputFieldUIModel ->
            showInvalidPhoneErrorMsg(inputFieldUIModel?.stcPayUIModel?.mobileNumberErrorMsg)
            handleFormValidationState(viewModel.inputFieldsValidatorLiveData.value?.stcPayUIModel?.isMobileValid)
        }
    }

    private fun showInvalidPhoneErrorMsg(errorMsg: String?) {
        binding.llPayNow.isEnabled = false
    }

    private fun handleFormValidationState(isFormValid: Boolean?) {
        binding.llPayNow.isEnabled = isFormValid ?: false
    }

    private fun handleOnStatusChanged(status: STCPayViewState?) {
        parentActivity.runOnUiThread {
            when (status) {
                is STCPayViewState.SubmittingSTCPayMobileNumber -> {
                    (activity as HomeActivity?)?.showLoadingIndicator()
                    binding.llPayNow.isEnabled = false
                    binding.edtPhoneNumber.isEnabled = false
                }

                is STCPayViewState.STCPayOTPAuth -> {
                    binding.constMainView.isVisible = false
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    val fragment = EnterOTPCustomUIFragment()
                    val args = Bundle()
                    args.putString(EnterOTPFragment.TRANSACTION_URL, status.url)
                    fragment.arguments = args
                    (activity as HomeActivity?)?.loadFragment(fragment)

                }

                else -> Unit
            }
        }
    }

    private fun initView() {
        (activity as HomeActivity?)?.hideLoadingIndicator()
        binding.llPayNow.setOnClickListener {
            viewModel.submitSTC()
        }

    }

    private fun FragmentEnterMobileNumberCustomUIBinding.setupListeners() {
        edtPhoneNumber.doAfterTextChanged { text ->
            viewModel.mobileNumberChanged(text) { s ->
                edtPhoneNumber.setText(s)
                // Move cursor to the end of the text
                edtPhoneNumber.setSelection(s.length)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.setSystemWindow(true)
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
        (activity as HomeActivity?)?.hideTabs()
    }

    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.setSystemWindow(false)
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