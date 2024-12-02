package com.tt.muzien.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.databinding.FragmentTutorialBinding
import com.tt.muzien.ui.auth.AuthActivity
import com.tt.muzien.ui.auth.AuthViewModel
import com.tt.muzien.ui.base.BaseFragment


class FragmentWelcome : BaseFragment<AuthViewModel, FragmentTutorialBinding, AuthRepository>() {
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.imgNext.setOnClickListener {
            (activity as AuthActivity?)?.loadFragment(FragmentOnBoarding())
        }
    }

    override fun getViewModel(): Class<AuthViewModel> {
        return AuthViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTutorialBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        AuthRepository(remoteDataSource.buildApi(AuthApi::class.java), userPreferences)


}