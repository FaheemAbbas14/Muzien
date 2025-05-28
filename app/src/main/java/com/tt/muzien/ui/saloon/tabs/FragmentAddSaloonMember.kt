package com.tt.muzien.ui.saloon.tabs

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
import com.tt.muzien.databinding.FragmentAddSaloonMemberBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.member.MemberViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.Appelement
import com.tt.muzien.utilities.InputValidator


class FragmentAddSaloonMember :
    BaseFragment<MemberViewModel, FragmentAddSaloonMemberBinding, MemberRepository>() {
    var fromMain: Boolean = false
    var saloonId = 0
    private val saloonsList = arrayListOf<String>()
    private val saloonMap = HashMap<String, SaloonDto>()
    private val emailList = arrayListOf<String>()
    private val userMap = HashMap<String, SaloonDto>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setUserRepo((activity as HomeActivity?)?.getUserRepo()!!)
        viewModel.setSaloonRepo((activity as HomeActivity?)?.getSaloonRepo()!!)
        if (!fromMain) {
            binding.llSaloon.visibility = View.GONE
            binding.txtSaloonLabel.visibility = View.GONE

        }
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.llSave.setOnClickListener {

            if (checkValidation()) {
                if (saloonId == 0) {
                    saloonId = saloonMap[binding.edtSaloon.text.toString()]?.id ?: 0
                }
                addMember()

            }
        }
        // checkValidation()
        getSaloons()
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

    override fun getViewModel(): Class<MemberViewModel> {
        return MemberViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddSaloonMemberBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        MemberRepository(remoteDataSource.buildApi(MemberApi::class.java, requireContext()))

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.hideTabs()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as HomeActivity?)?.hideTabs()
        }
    }
    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.showTabs()
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
                                    saloon.status,
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
        // (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun getUsers() {
        viewModel.getUsers.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        getSaloons()
//                        saloonsList.clear()
//                        for (saloon in it.value.data.saloons) {
//                            if (!saloonsList.contains(saloon.name)) {
//                                saloonsList.add(saloon.name)
//                                var saloonData = SaloonDto(
//                                    saloon.id.toInt(),
//                                    saloon.saloonImages,
//                                    saloon.name,
//                                    saloon.isActive,
//                                    saloon.address ?: "",
//                                    "${saloon.tRating} (${saloon.numReviews} ${
//                                        if (saloon.numReviews.toInt() == 1) "review" else "reviews"
//                                    })",
//                                    "10:00 AM - 11:00 PM",
//                                    saloon.locationLat.toDouble(), saloon.locationLong.toDouble()
//                                )
//                                saloonMap.put(saloon.name, saloonData)
//                            }
//
//
//                        }
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
        viewModel.getUsers()
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun setSaloonAdopter() {
        // Adapter to link the list with AutoCompleteTextView
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, saloonsList)

        // Set adapter to AutoCompleteTextView
        binding.edtSaloon.setAdapter(adapter)

        // Optional: Set the threshold (number of characters before suggestions appear)
        binding.edtSaloon.threshold = 1
    }

    private fun setUsersAdopter() {
        // Adapter to link the list with AutoCompleteTextView
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, saloonsList)

        // Set adapter to AutoCompleteTextView
        binding.edtEmail.setAdapter(adapter)

        // Optional: Set the threshold (number of characters before suggestions appear)
        binding.edtEmail.threshold = 1
    }

    private fun addMember() {
        viewModel.sendInvite.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        Appelement.reload=true
                        (activity as HomeActivity?)?.popFragment()
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
                binding.edtEmail.text.toString()
            )
        )
        // (activity as HomeActivity?)?.showLoadingIndicator()
    }
}