package com.tt.muzien.ui.member

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import com.tt.muzien.R
import com.tt.muzien.data.dto.SaloonDto
import com.tt.muzien.data.network.MemberApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.MemberRepository
import com.tt.muzien.data.requests.AddMemberRequest
import com.tt.muzien.databinding.FragmentInviteBinding
import com.tt.muzien.ui.adapters.ServiceSpinnerAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.snackbar


class FragmentInvite : BaseFragment<MemberViewModel, FragmentInviteBinding, MemberRepository>() {
    var type: String = ""
    var saloonId = 0
    var memberId = 0
    private val saloonsList = arrayListOf<String>()
    private val saloonMap = HashMap<String, SaloonDto>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setSaloonRepo((activity as HomeActivity?)?.getSaloonRepo()!!)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.llInvite.setOnClickListener {
            if (saloonId == 0) {
                saloonId = saloonMap[binding.edtSaloon.text.toString()]?.id ?: 0
            }
            sendInvite()

        }
        getSaloons()
        if (type == "User") {
            binding.txtLabel.text = "Invite to a User"
            binding.txtText.text = "Select a invite user"
            binding.edtSaloon.setText("Username")
            binding.txtInputLabel.text = "Select a User"
        }
    }

    override fun getViewModel(): Class<MemberViewModel> {
        return MemberViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentInviteBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        MemberRepository(remoteDataSource.buildApi(MemberApi::class.java, requireContext()))

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
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as HomeActivity?)?.changeStatusBarColor(requireActivity().resources.getColor(R.color.white))
            (activity as HomeActivity?)?.hideTabs()
        }
    }
    private fun getSaloons() {
        viewModel.getSaloon.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        saloonsList.clear()
                        for (saloon in it.value.data.items) {
                            if (!saloonsList.contains(saloon.name)) {
                                saloonsList.add(saloon.name)
                                var saloonData = SaloonDto(
                                    saloon.id.toInt(),
                                    saloon.SaloonImages,
                                    saloon.name,
                                    if (saloon.status=="open") true else false,
                                    saloon.isActive,
                                    saloon.address ?: "",
                                    "${saloon.tRating} (${saloon.numReviews} ${
                                        if (saloon.numReviews.toInt() == 1) "review" else "reviews"
                                    })",
                                    saloon.SaloonWorkHours,
                                    saloon.locationLat.toDouble(), saloon.locationLong.toDouble()
                                )
                                saloonMap.put(saloon.name, saloonData)
                            }


                        }
                        setSaloonAdopter()
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
        viewModel.getSaloons()
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun setSaloonAdopter() {
        // Adapter to link the list with AutoCompleteTextView
       val adapter = ServiceSpinnerAdapter(requireContext(), saloonsList,false)
        // Set adapter to AutoCompleteTextView
        binding.edtSaloon.setAdapter(adapter)

        // Optional: Set the threshold (number of characters before suggestions appear)
        binding.edtSaloon.threshold = 1
    }

    private fun sendInvite() {
        viewModel.sendInvite.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        requireView().snackbar("Invitation sent successfully")
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
        viewModel.sendInvite(
            AddMemberRequest(
                saloonId.toString(),
                userId = memberId.toString()
            )
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
}