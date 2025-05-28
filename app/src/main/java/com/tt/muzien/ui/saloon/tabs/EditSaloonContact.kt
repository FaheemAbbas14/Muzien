package com.tt.muzien.ui.saloon.tabs

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.setFragmentResult
import com.tt.muzien.R
import com.tt.muzien.data.dto.LoggedInInfo
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.SaloonApi
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.data.requests.UpdateSaloonRequest
import com.tt.muzien.databinding.FragmentEditSaloonContactBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.saloon.SaloonViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.Appelement
import com.tt.muzien.utilities.InputValidator


class EditSaloonContact :
    BaseFragment<SaloonViewModel, FragmentEditSaloonContactBinding, SaloonRepository>() {
    private lateinit var selectedCountry: String
    var phone: String = ""
    var saloonId: Int = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }

        binding.llSave.setOnClickListener {
            if (checkValidation()) {
                var phone =
                    binding.txtCountryCode.text.toString() + binding.edtPhoneNumber.text.toString()
                updateSaloon(phone)
            }
        }
        var (countryCode, phoneNumber) = splitString(LoggedInInfo.user?.phoneNumber!!)
        binding.txtCountryCode.text = countryCode
        countryCode = countryCode.replace("+", "")
        binding.edtPhoneNumber.text =
            Editable.Factory.getInstance().newEditable(phoneNumber)
        binding.countrySpinner.setCountryForPhoneCode(Integer.parseInt(countryCode))
        selectedCountry = binding.countrySpinner.selectedCountryName
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


                //checkValidation()


            }
        })
    }

    private fun updateSaloon(phone: String) {
        viewModel.addSaloon.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    if (it.value.status != 0) {
                        id
//                        if (AddSaloonData.days.size > 0) {
//                            addWorkHour()
//                        } else {
                        requireView().snackbar("Saloon updated successfully")
                        (activity as HomeActivity?)?.hideLoadingIndicator()
                        Appelement.reload=true
                        (activity as HomeActivity?)?.popFragment()
                        //  }
                    } else {
                        requireView().snackbar(it.value.message)
                        (activity as HomeActivity?)?.hideLoadingIndicator()
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

        viewModel.updateSaloon(
            saloonId,
            UpdateSaloonRequest(phoneNumber = phone)
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
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
            binding.txtPhoneError.visibility = View.VISIBLE
        } else {
            binding.txtPhoneError.visibility = View.GONE
        }
        return isValid

    }

    override fun getViewModel(): Class<SaloonViewModel> {
        return SaloonViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEditSaloonContactBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        SaloonRepository(remoteDataSource.buildApi(SaloonApi::class.java, requireContext()))

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.setSystemWindow(true)
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
        (activity as HomeActivity?)?.hideTabs()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as HomeActivity?)?.setSystemWindow(true)
            (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
            (activity as HomeActivity?)?.hideTabs()
        }
    }
    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.setSystemWindow(false)
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, false)
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
}