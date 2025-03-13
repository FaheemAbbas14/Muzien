package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tt.muzien.R
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentBookingsBinding
import com.tt.muzien.databinding.FragmentEditSaloonContactBinding
import com.tt.muzien.ui.auth.AuthActivity
import com.tt.muzien.ui.auth.FragmentOTP
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.utilities.InputValidator


class EditSaloonContact  : BaseFragment<HomeViewModel, FragmentEditSaloonContactBinding, HomeRepository>() {
    private lateinit var selectedCountry: String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }

        binding.llSave.setOnClickListener {
            if (checkValidation()) {
                var phone =
                    binding.txtCountryCode.text.toString() + binding.edtPhoneNumber.text.toString()

            }
        }
        binding.countrySpinner.setCountryForNameCode("SA")
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
    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEditSaloonContactBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java,requireContext()), userPreferences)
    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.hideTabs()
    }

    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.showTabs()
    }
}