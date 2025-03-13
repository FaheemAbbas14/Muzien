package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentAddSaloonMemberBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.utilities.InputValidator


class FragmentAddSaloonMember :
    BaseFragment<HomeViewModel, FragmentAddSaloonMemberBinding, HomeRepository>() {
    var fromMain: Boolean = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!fromMain){
            binding.llSaloon.visibility=View.GONE
            binding.txtSaloonLabel.visibility=View.GONE

        }
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.llSave.setOnClickListener {

            if (checkValidation()) {
//                val userPreferences = PreferenceManager.getInstance(requireActivity())
//                userPreferences.putString(Keys.Access_Token, "Faheem")
//                val activity = HomeActivity::class.java
//                requireActivity().startNewActivity(activity)
//                requireActivity().finish()
            }
        }
        // checkValidation()

    }

    private fun checkValidation(): Boolean {
        var isValid = true
        if (binding.edtEmail.text.toString() == "") {
            binding.txtEmailError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.txtEmailError.visibility = View.GONE
        }
        if (!InputValidator.isValidEmail(
                binding.edtEmail.text.toString()
            )
        ) {
            binding.txtEmailError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.txtEmailError.visibility = View.GONE
        }
        return isValid
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddSaloonMemberBinding.inflate(inflater, container, false)

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