package com.tt.muzien.ui.member

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tt.muzien.R
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.databinding.FragmentMembersBinding
import com.tt.muzien.databinding.FragmentSaloonBinding
import com.tt.muzien.ui.auth.AuthViewModel
import com.tt.muzien.ui.base.BaseFragment

class FragmentMembers : BaseFragment<AuthViewModel, FragmentMembersBinding, AuthRepository>() {
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun getViewModel(): Class<AuthViewModel> {
        return AuthViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMembersBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        AuthRepository(remoteDataSource.buildApi(AuthApi::class.java), userPreferences)


}