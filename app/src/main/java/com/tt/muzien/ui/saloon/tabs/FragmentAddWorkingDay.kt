package com.tt.muzien.ui.saloon.tabs

import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.setFragmentResult
import com.tt.muzien.R
import com.tt.muzien.data.dto.AddSaloonData
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.SaloonApi
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.data.requests.AddWorkingHourRequest
import com.tt.muzien.data.requests.WorkHour
import com.tt.muzien.databinding.FragmentAddWorkingDayBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.saloon.SaloonViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.Appelement
import com.tt.muzien.utilities.TimeHelper
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


class FragmentAddWorkingDay :
    BaseFragment<SaloonViewModel, FragmentAddWorkingDayBinding, SaloonRepository>() {
    var selectedDays = arrayListOf<String>()
    var startTime: String = ""
    var endTime: String = ""
    var isEdit = false
    var saloonId: Int = 0
    var userId: Int = 0
    var systemWindow: Boolean = false
    var validTimes: Boolean = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setMemberRepo((activity as HomeActivity?)?.getMemberRepo()!!)
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        if (userId != 0) {
            binding.txtText.text = "Add user work hours"
        }
        binding.llSave.setOnClickListener {
            if (checkValidation(true)) {
                if (userId != 0) {
                    addMemberWorkHour()

                } else if (isEdit) {
                    addWorkHour()
                } else {
                    AddSaloonData.startTime = startTime
                    AddSaloonData.endTime = endTime
                    for (day in selectedDays) {
                        if (!AddSaloonData.days.contains(day)) {
                            AddSaloonData.days.add(day)
                        }
                    }
                    val resultBundle = Bundle().apply {
                        putBoolean("reload", true) // Replace with your data
                    }
                    setFragmentResult("requestKey", resultBundle)
                    (activity as HomeActivity?)?.popFragment()
                }
            }

        }
        binding.edtOpening.setOnClickListener {
            showTimePicker(requireContext(), binding.edtOpening, true)
            checkValidation()

        }
        binding.ddlOpening.setOnClickListener {
            showTimePicker(requireContext(), binding.edtOpening, true)
            checkValidation()

        }
        binding.edtClose.setOnClickListener {
            showTimePicker(requireContext(), binding.edtClose, false)
            checkValidation()

        }
        binding.ddlClosing.setOnClickListener {
            showTimePicker(requireContext(), binding.edtClose, false)
            checkValidation()

        }

        binding.txtMon.setOnClickListener {
            if (selectedDays.contains("Monday")) {
                selectedDays.remove("Monday")
                binding.txtMon.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_white_blue))
                binding.txtMon.setTextColor(resources.getColor(R.color.colorPrimary))

            } else {
                selectedDays.add("Monday")
                binding.txtMon.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_blue))
                binding.txtMon.setTextColor(resources.getColor(R.color.white))
            }

        }
        binding.txtTue.setOnClickListener {
            if (selectedDays.contains("Tuesday")) {
                selectedDays.remove("Tuesday")
                binding.txtTue.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_white_blue))
                binding.txtTue.setTextColor(resources.getColor(R.color.colorPrimary))

            } else {
                selectedDays.add("Tuesday")
                binding.txtTue.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_blue))
                binding.txtTue.setTextColor(resources.getColor(R.color.white))
            }

        }
        binding.txtWed.setOnClickListener {
            if (selectedDays.contains("Wednesday")) {
                selectedDays.remove("Wednesday")
                binding.txtWed.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_white_blue))
                binding.txtWed.setTextColor(resources.getColor(R.color.colorPrimary))

            } else {
                selectedDays.add("Wednesday")
                binding.txtWed.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_blue))
                binding.txtWed.setTextColor(resources.getColor(R.color.white))
            }

        }
        binding.txtThu.setOnClickListener {
            if (selectedDays.contains("Thursday")) {
                selectedDays.remove("Thursday")
                binding.txtThu.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_white_blue))
                binding.txtThu.setTextColor(resources.getColor(R.color.colorPrimary))

            } else {
                selectedDays.add("Thursday")
                binding.txtThu.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_blue))
                binding.txtThu.setTextColor(resources.getColor(R.color.white))
            }

        }
        binding.txtFri.setOnClickListener {
            if (selectedDays.contains("Friday")) {
                selectedDays.remove("Friday")
                binding.txtFri.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_white_blue))
                binding.txtFri.setTextColor(resources.getColor(R.color.colorPrimary))

            } else {
                selectedDays.add("Friday")
                binding.txtFri.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_blue))
                binding.txtFri.setTextColor(resources.getColor(R.color.white))
            }

        }
        binding.txtSat.setOnClickListener {
            if (selectedDays.contains("Saturday")) {
                selectedDays.remove("Saturday")
                binding.txtSat.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_white_blue))
                binding.txtSat.setTextColor(resources.getColor(R.color.colorPrimary))

            } else {
                selectedDays.add("Saturday")
                binding.txtSat.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_blue))
                binding.txtSat.setTextColor(resources.getColor(R.color.white))
            }

        }
        binding.txtSun.setOnClickListener {
            if (selectedDays.contains("Sunday")) {
                selectedDays.remove("Sunday")
                binding.txtSun.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_white_blue))
                binding.txtSun.setTextColor(resources.getColor(R.color.colorPrimary))

            } else {
                selectedDays.add("Sunday")
                binding.txtSun.setBackgroundDrawable(resources.getDrawable(R.drawable.circular_blue))
                binding.txtSun.setTextColor(resources.getColor(R.color.white))
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showTimePicker(context: Context, textView: TextView, isStart: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                var formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)

                if (isStart) {
                    startTime = formattedTime
                } else {
                    endTime = formattedTime
                }
                textView.text = TimeHelper.convertTo12Hours(formattedTime)
                if (startTime != "" && endTime != "") {
                    val inputFormatter = DateTimeFormatter.ofPattern("HH:mm")
                    val startTime = LocalTime.parse(startTime, inputFormatter)
                    val endTime = LocalTime.parse(endTime, inputFormatter)

                    if (startTime < endTime) {
                        validTimes = true
                        binding.txtError.visibility = View.GONE
                        println("Start time is before end time")
                    } else {
                        validTimes = false
                        binding.txtError.visibility = View.VISIBLE
                        println("Start time is after or equal to end time")
                    }
                }
            },
            hour,
            minute,
            false // true for 24-hour format, false for 12-hour
        )

        timePickerDialog.show()
    }

    private fun formatTime12Hour(hour: Int, minute: Int): String {
        val amPm = if (hour >= 12) "PM" else "AM"
        val hour12 = if (hour % 12 == 0) 12 else hour % 12  // Convert 24-hour to 12-hour format
        return String.format("%02d:%02d %s", hour12, minute, amPm)
    }

    private fun checkValidation(show: Boolean = false): Boolean {
        var isValid = false
        if (startTime != "" && endTime != "" && selectedDays.size > 0 && validTimes) {
            isValid = true
        }
        if (startTime != "" && endTime == "" && show) {
            binding.txtError.visibility = View.VISIBLE
            binding.txtError.text = "Closing Time is required"
        }
        if (startTime == "" && endTime != "" && show) {
            binding.txtError.visibility = View.VISIBLE
            binding.txtError.text = "Opening Time is required"
        }
        return isValid
    }

    override fun getViewModel(): Class<SaloonViewModel> {
        return SaloonViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentAddWorkingDayBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        SaloonRepository(
            remoteDataSource.buildApi(SaloonApi::class.java, requireContext())
        )

    override fun onResume() {
        super.onResume()
        if (systemWindow) {
            (activity as HomeActivity?)?.setSystemWindow(true)
        }
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
        (activity as HomeActivity?)?.changeStatusBarColor(requireActivity().resources.getColor(R.color.white))
        (activity as HomeActivity?)?.hideTabs()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            if (systemWindow) {
                (activity as HomeActivity?)?.setSystemWindow(true)
            }
            (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
            (activity as HomeActivity?)?.changeStatusBarColor(requireActivity().resources.getColor(R.color.white))
            (activity as HomeActivity?)?.hideTabs()
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.setSystemWindow(false)
        // (activity as HomeActivity?)?.showTabs()
    }

    private fun addWorkHour() {
        viewModel.addHour.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        Appelement.reload = true
                        (activity as HomeActivity?)?.popFragment()
                        requireView().snackbar("Working hour added successfully")

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
        var workingHours = ArrayList<WorkHour>()
        for (day in selectedDays) {
            workingHours.add(
                WorkHour(
                    day,
                    TimeHelper.convertLocalTimeToUtc(startTime),
                    TimeHelper.convertLocalTimeToUtc(endTime)
                )
            )
        }
        viewModel.addHour(saloonId, AddWorkingHourRequest(workingHours))
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun addMemberWorkHour() {
        viewModel.addMemberData.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        Appelement.reload = true
                        (activity as HomeActivity?)?.popFragment()
                        requireView().snackbar("Working hour added successfully")

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
        var workingHours = ArrayList<WorkHour>()
        for (day in selectedDays) {
            workingHours.add(
                WorkHour(
                    day,
                    TimeHelper.convertLocalTimeToUtc(startTime),
                    TimeHelper.convertLocalTimeToUtc(endTime)
                )
            )
        }
        viewModel.addMemberWorkingHour(userId, AddWorkingHourRequest(workingHours))
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

}