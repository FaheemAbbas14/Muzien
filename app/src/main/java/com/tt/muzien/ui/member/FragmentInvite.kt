package com.tt.muzien.ui.member

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.tt.muzien.R
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentInviteBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel


class FragmentInvite : BaseFragment<HomeViewModel, FragmentInviteBinding, HomeRepository>() {
    var type: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.llInvite.setOnClickListener {


        }
        if (type == "User") {
            binding.txtLabel.text="Invite to a User"
            binding.txtText.text="Select a invite user"
            binding.edtInput.setText("Username")
            binding.txtInputLabel.text="Select a User"
        }
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentInviteBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java,requireContext()), userPreferences)
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.changeStatusBarColor(requireActivity().resources.getColor(R.color.white))
        (activity as HomeActivity?)?.hideTabs()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.changeStatusBarColor(requireActivity().resources.getColor(R.color.white))
        (activity as HomeActivity?)?.showTabs()
    }
}