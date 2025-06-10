package com.moyasar.android.sdkdriver.customui.creditcard

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.moyasar.android.sdk.core.domain.entities.PaymentResult
import com.moyasar.android.sdk.core.exceptions.InvalidConfigException
import com.moyasar.android.sdk.creditcard.data.models.CreditCardNetwork
import com.moyasar.android.sdk.creditcard.data.models.getNetwork
import com.moyasar.android.sdk.creditcard.data.models.request.PaymentRequest
import com.moyasar.android.sdk.creditcard.presentation.di.MoyasarAppContainer
import com.moyasar.android.sdk.creditcard.presentation.di.MoyasarAppContainer.viewModel
import com.moyasar.android.sdk.creditcard.presentation.model.FieldValidation
import com.moyasar.android.sdk.creditcard.presentation.model.PaymentStatusViewState
import com.moyasar.android.sdk.creditcard.presentation.view.fragments.PaymentAuthFragment
import com.tt.muzien.data.dto.PayDto
import com.tt.muzien.databinding.FragmentCustomUIPaymentBinding
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.utilities.Appelement
import com.moyasar.android.sdk.R as sdkR
import com.tt.muzien.R
class CustomUIPaymentFragment : Fragment() {
    private lateinit var parentActivity: FragmentActivity
    private lateinit var binding: FragmentCustomUIPaymentBinding
    var payDto: PayDto? = null
    var transactionId = ""

    companion object {
        fun newInstance(
            application: Application,
            paymentRequest: PaymentRequest,
            payInfo: PayDto?,
            callback: (PaymentResult) -> Unit,
        ): CustomUIPaymentFragment {
            Appelement.payDto = payInfo
            val configError = paymentRequest.validate()
            if (configError.any()) {
                throw InvalidConfigException(configError)
            }
            MoyasarAppContainer.initialize(application, paymentRequest, callback)
            return CustomUIPaymentFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        payDto = Appelement.payDto
        parentActivity = requireActivity()
        binding = FragmentCustomUIPaymentBinding.inflate(inflater, container, false)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        setupObservers()
        binding.setupListeners()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.creditCardStatus.observe(viewLifecycleOwner, ::handleOnStatusChanged)
        viewModel.inputFieldsValidatorLiveData.observe(viewLifecycleOwner) { inputFieldUIModel ->
            showInvalidNameErrorMsg(inputFieldUIModel.errorMessage?.nameErrorMsg)
            showInvalidCardNumberErrorMsg(inputFieldUIModel.errorMessage?.numberErrorMsg)
            showInvalidExpiryErrorMsg(inputFieldUIModel.errorMessage?.expiryDateErrorMsg)
            showInvalidCVVErrorMsg(inputFieldUIModel.errorMessage?.cvcErrorMsg)
            handleFormValidationState(inputFieldUIModel.isFormValid)
            handleShowAllowedCardTypesIcons(inputFieldUIModel.cardNumber)
        }
    }

    private fun handleShowAllowedCardTypesIcons(text: String?) {
        if (text.orEmpty().isEmpty()) {
            MoyasarAppContainer.allowedNetworks.forEach {
                when (it) {
                    CreditCardNetwork.Visa -> binding.imgVisa.isVisible = true
                    CreditCardNetwork.Mastercard -> binding.imgMaster.isVisible = true
                    CreditCardNetwork.Mada -> binding.imgMada.isVisible = true
                    CreditCardNetwork.Amex -> binding.imgAmex.isVisible = true
                    else -> Unit
                }
            }
        } else {
            when (getNetwork(text.orEmpty())) {
                CreditCardNetwork.Visa -> {
                    binding.imgVisa.isVisible = true
                    binding.imgMaster.isVisible = false
                    binding.imgMada.isVisible = false
                    binding.imgAmex.isVisible = false
                }

                CreditCardNetwork.Mastercard -> {
                    binding.imgMaster.isVisible = true
                    binding.imgVisa.isVisible = false
                    binding.imgMada.isVisible = false
                    binding.imgAmex.isVisible = false
                }

                CreditCardNetwork.Mada -> {
                    binding.imgMada.isVisible = true
                    binding.imgVisa.isVisible = false
                    binding.imgMaster.isVisible = false
                    binding.imgAmex.isVisible = false
                }

                CreditCardNetwork.Amex -> {
                    binding.imgAmex.isVisible = true
                    binding.imgVisa.isVisible = false
                    binding.imgMaster.isVisible = false
                    binding.imgMada.isVisible = false
                }

                else -> {
                    binding.imgVisa.isVisible = false
                    binding.imgMaster.isVisible = false
                    binding.imgMada.isVisible = false
                    binding.imgAmex.isVisible = false
                }
            }
        }
    }

    private fun handleFormValidationState(formValid: Boolean) {
        binding.llPayNow.isEnabled = formValid
        // for test purpose only
//        if (formValid) binding.txtPay.setTextColor(
//            ContextCompat.getColor(
//                requireContext(), R.color.white
//            )
//        )
//        else binding.txtPay.setTextColor(
//            ContextCompat.getColor(
//                requireContext(), sdkR.color.red
//            )
//        )
    }

    private fun showInvalidCVVErrorMsg(cvvErrorMsg: String?) {
      //  Toast.makeText(requireContext(),"Invalid cvv", Toast.LENGTH_SHORT).show()
        binding.llPayNow.isEnabled = false
    }

    private fun showInvalidExpiryErrorMsg(expiryErrorMsg: String?) {
       // Toast.makeText(requireContext(),"Invalid expiry date", Toast.LENGTH_SHORT).show()
        binding.llPayNow.isEnabled = false
    }

    private fun showInvalidCardNumberErrorMsg(numberErrorMsg: String?) {
      //  Toast.makeText(requireContext(),"Invalid card number", Toast.LENGTH_SHORT).show()
        binding.llPayNow.isEnabled = false
    }

    private fun showInvalidNameErrorMsg(nameErrorMsg: String?) {
        binding.llPayNow.isEnabled = false
       // Toast.makeText(requireContext(),"Invalid card holder name", Toast.LENGTH_SHORT).show()
        //binding.holderNameInputLayout.error = nameErrorMsg.takeIf { it?.isNotEmpty() == true }
    }

    private fun FragmentCustomUIPaymentBinding.setupListeners() {
        edtCardName.doAfterTextChanged { text ->
            viewModel.creditCardNameChanged(text?.toString().orEmpty())
        }
        edtCardNumber.doAfterTextChanged { text ->
            viewModel.creditCardNumberChanged(text) { s ->
                // update formating
                edtCardNumber.setText(s)
                // Move cursor to the end of the text
                try {
                    edtCardNumber.setSelection(s.length)
                }
                catch (e:Exception){

                }

            }
        }
        edtExp.doAfterTextChanged { text ->
            viewModel.creditCardExpiryChanged(text) { s ->
                // update formating
                edtExp.setText(s)
                // Move cursor to the end of the text
                try {
                    edtExp.setSelection(s.length)
                }
                catch (e:Exception){

                }
            }
        }

        edtCVV.doAfterTextChanged { text ->
            viewModel.creditCardCvcChanged(text)
        }
        llPayNow.setOnClickListener {
            viewModel.submit()
        }
        edtCardName.setOnFocusChangeListener { _, hf ->
            viewModel.validateField(
                fieldType = FieldValidation.Name,
                value = edtCardName.text?.toString().orEmpty(),
                cardNumber = edtCardNumber.text?.toString().orEmpty(),
                hasFocus = hf,
            )
        }
        edtCardNumber.setOnFocusChangeListener { _, hf ->
            viewModel.validateField(
                fieldType = FieldValidation.Number,
                value = edtCardNumber.text?.toString().orEmpty(),
                cardNumber = edtCardNumber.text?.toString().orEmpty(),
                hasFocus = hf,
            )
        }
        edtExp.setOnFocusChangeListener { _, hf ->
            viewModel.validateField(
                fieldType = FieldValidation.Expiry,
                value = edtExp.text?.toString().orEmpty(),
                cardNumber = edtExp.text?.toString().orEmpty(),
                hasFocus = hf,
            )
        }
        edtCVV.setOnFocusChangeListener { _, hf ->
            viewModel.validateField(
                fieldType = FieldValidation.Cvc,
                value = edtCVV.text?.toString().orEmpty(),
                cardNumber = edtCardNumber.text?.toString().orEmpty(),
                hasFocus = hf,
            )
        }
    }

    private fun showLoading() {
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun hideLoading() {
        (activity as HomeActivity?)?.hideLoadingIndicator()
    }

    private fun handleOnStatusChanged(status: PaymentStatusViewState?) {
        parentActivity.runOnUiThread {
            when (status) {
                is PaymentStatusViewState.PaymentAuth3dSecure -> {
                    hideLoading()
                    hideScreenViews()
                    (activity as HomeActivity?)?.loadFragment(PaymentAuthFragment())

                }

                is PaymentStatusViewState.Reset -> {
                    hideLoading()
                    showScreenViews()
                }

                is PaymentStatusViewState.SubmittingPayment -> {
                    showLoading()
                    hideScreenViews()
                }

                else -> Unit
            }
        }
    }


    private fun showScreenViews() = with(binding) {
        constMainView.isVisible = true
    }

    private fun hideScreenViews() = with(binding) {
        constMainView.isVisible = false
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