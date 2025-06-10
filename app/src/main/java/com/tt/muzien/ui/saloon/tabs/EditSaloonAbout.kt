package com.tt.muzien.ui.saloon.tabs

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.setFragmentResult
import com.tt.muzien.R
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.SaloonApi
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.data.requests.UpdateSaloonRequest
import com.tt.muzien.databinding.FragmentEditSaloonAboutBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.saloon.SaloonViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.Appelement


class EditSaloonAbout :
    BaseFragment<SaloonViewModel, FragmentEditSaloonAboutBinding, SaloonRepository>() {
    var about: String = ""
    var saloonId: Int = 0
    var phone: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.edtAbout.text = Editable.Factory.getInstance().newEditable("+$about")
        binding.llSave.setOnClickListener {
            if (binding.edtAbout.text.toString() != "") {
                updateSaloon()
            }

        }
    }

    private fun updateSaloon() {
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
            UpdateSaloonRequest(description = binding.edtAbout.text.toString())
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    override fun getViewModel(): Class<SaloonViewModel> {
        return SaloonViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEditSaloonAboutBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        SaloonRepository(
            remoteDataSource.buildApi(SaloonApi::class.java, requireContext())
        )

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
}