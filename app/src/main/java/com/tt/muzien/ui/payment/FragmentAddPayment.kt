package com.tt.muzien.ui.payment

import android.app.DatePickerDialog
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.moyasar.android.sdk.core.customviews.button.MoyasarButtonType
import com.moyasar.android.sdk.core.data.response.PaymentResponse
import com.moyasar.android.sdk.core.domain.entities.PaymentResult
import com.moyasar.android.sdk.core.exceptions.InvalidConfigException
import com.moyasar.android.sdk.creditcard.data.models.CreditCardNetwork
import com.moyasar.android.sdk.creditcard.data.models.request.PaymentRequest
import com.moyasar.android.sdk.creditcard.presentation.di.MoyasarAppContainer
import com.moyasar.android.sdk.creditcard.presentation.view.fragments.PaymentFragment
import com.moyasar.android.sdk.stcpay.presentation.view.fragments.EnterMobileNumberFragment
import com.tt.muzien.Application
import com.tt.muzien.BuildConfig
import com.tt.muzien.R
import com.tt.muzien.data.dto.LoggedInInfo
import com.tt.muzien.data.dto.PayDto
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.SaloonApi
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.data.requests.AddSubscriptionRequest
import com.tt.muzien.databinding.FragmentAddPaymentBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.enable
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.saloon.SaloonViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.Appelement
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.UUID

class FragmentAddPayment :
    BaseFragment<SaloonViewModel, FragmentAddPaymentBinding, SaloonRepository>() {
    var expMonth = 0
    var expYear = 0
    var type = "Visa"
    var payDto: PayDto? = null
    var transactionId = ""

    companion object {
        fun newInstance(
            application: Application,
            paymentRequest: PaymentRequest,
            payInfo: PayDto?,
            callback: (PaymentResult) -> Unit,
        ): FragmentAddPayment {
            Appelement.payDto = payInfo
            val configError = paymentRequest.validate()
            if (configError.any()) {
                throw InvalidConfigException(configError)
            }
            MoyasarAppContainer.initialize(application, paymentRequest, callback)
            return FragmentAddPayment()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        payDto = Appelement.payDto
        binding.edtCardName.text=Editable.Factory.getInstance().newEditable("Faheem Abbas")
        binding.edtCardNumber.text=Editable.Factory.getInstance().newEditable("4111114005765430")
        binding.edtExp.text=Editable.Factory.getInstance().newEditable("11/26")
        binding.edtCVV.text=Editable.Factory.getInstance().newEditable("1234")
        checkValidation()
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.imgVisa.setOnClickListener {
            type = "Visa"
            checkValidation()
        }
        binding.imgMaster.setOnClickListener {
            type = "Master"
            checkValidation()
        }
        binding.llPayNow.setOnClickListener {
            makePayment(false)
            // Toast.makeText(requireContext(), "All data valid", Toast.LENGTH_SHORT).show()
        }
        binding.edtExp.setOnClickListener {
            showMonthYearPicker()
        }
        binding.imgCalender.setOnClickListener {
            showMonthYearPicker()
        }
        binding.edtCardName.addTextChangedListener(object : TextWatcher {
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
        binding.edtCardNumber.addTextChangedListener(object : TextWatcher {
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
        binding.edtExp.addTextChangedListener(object : TextWatcher {
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
        binding.edtCVV.addTextChangedListener(object : TextWatcher {
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makePayment(isSTCPayment: Boolean) {
        val uuid = UUID.randomUUID().toString()
//        val cardSource = Source.CreditCard(
//            name = name,
//            number = number,
//            month = month,
//            year = year,
//            cvc = cvc
//        )
        val paymentRequest = PaymentRequest(
            apiKey = BuildConfig.moyasarKey,
            amount = payDto?.discountedPrice
                ?: 0, // Amount in the smallest currency unit For example: 10 SAR = 10 * 100 Halalas
            currency = payDto?.currency?.lowercase() ?: "",
            description = "${payDto?.saloonName ?: ""} ${payDto?.planName ?: ""}",
            manual = false,
            metadata = mapOf(
                "order_id" to uuid,
                "customer_id" to Integer.parseInt("${LoggedInInfo.user?.id}"),
                "customer_name" to "${LoggedInInfo.user?.fullName}",
                "actual_price" to Integer.parseInt("${payDto?.actualPrice}"),
                "discounted_price" to Integer.parseInt("${payDto?.discountedPrice}"),
                "platform" to "Android"
            ),
            saveCard = false,
            buttonType = MoyasarButtonType.PAY, // [determine button title: optional]; by default, it's set to `MoyasarButtonType.PAY`.
            allowedNetworks = listOf(
                CreditCardNetwork.Visa,
                CreditCardNetwork.Mastercard,
                CreditCardNetwork.Mada,
                CreditCardNetwork.Amex,
            ) // Optional set your supported networks

        )
        if (!isSTCPayment) {
            // Create the payment fragment
            val paymentFragment =
                PaymentFragment.newInstance(requireActivity().application, paymentRequest) {
                    handlePaymentResult(it)
                }
            (activity as HomeActivity?)?.loadFragment(paymentFragment)
        } else {
            // Create the payment fragment
            val enterMobileNumberFragment =
                EnterMobileNumberFragment.newInstance(
                    requireActivity().application,
                    paymentRequest
                ) {
                    handlePaymentResult(it)
                }
            (activity as HomeActivity?)?.loadFragment(enterMobileNumberFragment)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handlePaymentResult(result: PaymentResult) {
        when (result) {
            is PaymentResult.Completed -> {
                handleCompletedPayment(result.payment)
            }

            is PaymentResult.Failed -> {
                // Show error
                val error = result.error
                // Handle the error (e.g., Toast or dialog)
                Toast.makeText(requireContext(), "Payment failed with ${error}", Toast.LENGTH_SHORT)
                    .show()
            }

            PaymentResult.Canceled -> {
                Toast.makeText(requireContext(), "Payment cancelled", Toast.LENGTH_SHORT).show()
            }

            is PaymentResult.CompletedToken -> {

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleCompletedPayment(payment: PaymentResponse) {
        when (payment.status) {
            "paid" -> {
                transactionId = payment.invoiceId ?: ""
                addSubscriptions()
                Toast.makeText(
                    requireContext(),
                    "Payment success ${payment.invoiceId}",
                    Toast.LENGTH_SHORT
                ).show()

            }

            "failed" -> {
                val errorMessage = payment.source["message"]
                /* Handle failed payment */
                Toast.makeText(requireContext(), "Payment failed $errorMessage", Toast.LENGTH_SHORT)
                    .show()
                (activity as HomeActivity?)?.popFragment()
            }

            else -> { /* Handle other statuses */
            }
        }
    }


    private fun testCard() {
        binding.edtCardName.text = Editable.Factory.getInstance().newEditable("")
        binding.edtCardNumber.text = Editable.Factory.getInstance().newEditable("4111118250252531")
        binding.edtCVV.text = Editable.Factory.getInstance().newEditable("")
        binding.edtExp.text = Editable.Factory.getInstance().newEditable("")
    }

    override fun getViewModel(): Class<SaloonViewModel> {
        return SaloonViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentAddPaymentBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        SaloonRepository(
            remoteDataSource.buildApi(SaloonApi::class.java, requireContext())
        )

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.hideTabs()
    }

    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.showTabs()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as HomeActivity?)?.hideTabs()
        }
    }

    fun checkValidation(): Boolean {
        var isValid = false
        if (type != "" && isValidCardNumber(binding.edtCardNumber.text.toString()) && isValidCardHolder(
                binding.edtCardName.text.toString()
            ) && isValidCVV(
                binding.edtCVV.text.toString()
            ) && isValidExpiryDate(expMonth, expYear)
        ) {
            isValid = true
        }
        binding.llPayNow.enable(isValid)
        return isValid
    }

    fun isValidCardNumber(cardNumber: String): Boolean {
        val cleaned = cardNumber.replace("\\s".toRegex(), "")
        if (!cleaned.matches("\\d{13,19}".toRegex())) return false

        // Luhn check
        var sum = 0
        var alternate = false
        for (i in cleaned.length - 1 downTo 0) {
            var n = cleaned[i].digitToInt()
            if (alternate) {
                n *= 2
                if (n > 9) n -= 9
            }
            sum += n
            alternate = !alternate
        }
        return sum % 10 == 0
    }

    fun isValidExpiryDate(month: Int, year: Int): Boolean {
        if (month !in 1..12) return false
        val now = Calendar.getInstance()
        val currentYear = now.get(Calendar.YEAR) % 100 // YY
        val currentMonth = now.get(Calendar.MONTH) + 1
        return (year > currentYear) || (year == currentYear && month >= currentMonth)
    }

    fun isValidCVV(cvv: String): Boolean {
        return cvv.matches("\\d{3,4}".toRegex())
    }

    fun isValidCardHolder(name: String): Boolean {
        return name.matches("[a-zA-Z ,.'-]+".toRegex()) && name.length >= 2
    }

    private fun showMonthYearPicker() {
        val calendar = Calendar.getInstance()

        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, _ ->
                val formattedMonth = String.format("%02d", month + 1)
                expMonth = month + 1
                expYear = year
                binding.edtExp.setText(
                    "$formattedMonth/${
                        year.toString().takeLast(2)
                    }"
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Hide the day spinner
        dialog.datePicker.findViewById<View>(
            Resources.getSystem().getIdentifier("day", "id", "android")
        )?.visibility = View.GONE

        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addSubscriptions() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

// Current date
        val currentDate = LocalDate.now()
        val startDate = currentDate.format(formatter)

// Add one year to current date
        val nextYearDate = currentDate.plusYears(1)
        val endDate = nextYearDate.format(formatter)
        viewModel.addSubscriptions.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    Appelement.reload = true
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        requireView().snackbar("Subscription added successfully")
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


        viewModel.addSubscriptions(
            payDto?.saloonId ?: 0,
            AddSubscriptionRequest(
                transactionId,
                payDto?.discountedPrice ?: 0,
                startDate,
                endDate,
                payDto?.planId ?: 0
            )
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

}