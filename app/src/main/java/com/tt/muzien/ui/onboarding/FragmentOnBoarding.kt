package com.tt.muzien.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.tt.muzien.R
import com.tt.muzien.constants.Keys
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.databinding.FragmentOnBoardingBinding
import com.tt.muzien.ui.auth.AuthActivity
import com.tt.muzien.ui.auth.AuthViewModel
import com.tt.muzien.ui.auth.FragmentSignIn
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.utilities.PreferenceManager

class FragmentOnBoarding :
    BaseFragment<AuthViewModel, FragmentOnBoardingBinding, AuthRepository>() {
    @Deprecated("Deprecated in Java")
    private var screenNumber: Int = 1
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Set alpha directly
        binding.imgAbackground.alpha = 0.1f
        binding.imgPrevious.setOnClickListener {
            if (screenNumber == 1) {
                (activity as AuthActivity?)?.popFragment()
            } else {
                screenNumber--
                updateUi()
            }
        }
        binding.imgNext.setOnClickListener {
            if (screenNumber < 2) {
                screenNumber++
                updateUi()
            } else {
                PreferenceManager.getInstance(requireContext())
                    .putBoolean(Keys.Tutorial_Shown, true)
                (activity as AuthActivity?)?.popFragment()
                (activity as AuthActivity?)?.popFragment()
                (activity as AuthActivity?)?.popFragment()
                (activity as AuthActivity?)?.popFragment()
                (activity as AuthActivity?)?.loadFragment(FragmentSignIn())
            }
        }
    }

    private fun updateUi() {
        if (screenNumber == 1) {
            binding.imgNext.setImageDrawable(requireActivity().getDrawable(R.drawable.next_arrow))
            binding.imgPicture.setImageDrawable(requireActivity().getDrawable(R.drawable.second_screen))
            binding.txtText.text =
                "Access your saloon revenue  & continuously enhance your service quality"
        } else {
            binding.imgNext.setImageDrawable(requireActivity().getDrawable(R.drawable.getstarted))
            binding.imgPicture.setImageDrawable(requireActivity().getDrawable(R.drawable.third_screen))
            binding.txtText.text =
                "Effortlessly manage your salon operations through our application, granting you full access from anywhere, anytime."
        }
    }

    override fun getViewModel(): Class<AuthViewModel> {
        return AuthViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentOnBoardingBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        AuthRepository(remoteDataSource.buildApi(AuthApi::class.java), userPreferences)


}