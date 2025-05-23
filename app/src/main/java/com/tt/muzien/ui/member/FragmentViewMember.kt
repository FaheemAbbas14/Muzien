package com.tt.muzien.ui.member

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tt.muzien.R
import com.tt.muzien.data.dto.MemberDto
import com.tt.muzien.data.dto.WorkingHourData
import com.tt.muzien.data.network.MemberApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.MemberRepository
import com.tt.muzien.data.responses.MemberDetails
import com.tt.muzien.databinding.FragmentViewMemberBinding
import com.tt.muzien.ui.adapters.HolidayListAdapter
import com.tt.muzien.ui.adapters.ServiceGridviewAdapter
import com.tt.muzien.ui.adapters.WorkingHoursAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.bookings.FragmentMemberBookings
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.saloon.tabs.FragmentAddHoliday
import com.tt.muzien.ui.saloon.tabs.FragmentAddWorkingDay
import com.tt.muzien.ui.snackbar
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentViewMember :
    BaseFragment<MemberViewModel, FragmentViewMemberBinding, MemberRepository>() {
    var member: MemberDto? = null
    private val workingHourList = arrayListOf<WorkingHourData>()
    private val servicesList = arrayListOf<String>()
    private var workingHoursAdopter: WorkingHoursAdapter? = null
    var memberDetails: MemberDetails? = null
    private val holidaysList = arrayListOf<String>()
    private var holidayListAdapter: HolidayListAdapter? = null
    private val holidaysMap = HashMap<String, Long>()
    var isFromSaloon = false
    var position: Int = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefresh.setOnRefreshListener {
           // binding.swipeRefresh.isRefreshing = false
            //page=1
            getMemberDetails(true)
        }
        setFragmentResultListener("requestKey") { key, bundle ->
            getMemberDetails(false)

        }
//        if (LoggedInInfo.user?.role == "business-owner") {
//            binding.imgAddWorkingHour.visibility=View.GONE
//            binding.imgAddHoliday.visibility=View.GONE
//        }
        binding.imgBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
        binding.imgMenu.setOnClickListener {
            showCustomMenu(binding.imgMenu)
        }
        binding.llRatings.setOnClickListener {
            var nextFragment = FragmentViewMemberReviews()
            nextFragment.selectedSaloon = 7
            nextFragment.selectedMember = member?.userId ?: 0
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.llBookings.setOnClickListener {
            var nextFragment = FragmentMemberBookings()
            nextFragment.selectedSaloon = 7
            nextFragment.selectedMember = member?.userId ?: 0
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.imgAddHoliday.setOnClickListener {
            var nextFragment = FragmentAddHoliday()
            nextFragment.userId = member?.userId ?: 0
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.imgAddWorkingHour.setOnClickListener {
            var nextFragment = FragmentAddWorkingDay()
            nextFragment.userId = member?.userId ?: 0
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.imgAddService.setOnClickListener {
            var nextFragment = FragmentAddService()
            nextFragment.userId = member?.userId!!
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        getMemberDetails(false)

    }

    private fun showCustomMenu(anchor: View) {
        // Inflate the custom menu layout
        val inflater = LayoutInflater.from(requireActivity())
        val menuView = inflater.inflate(R.layout.invite_layout, null)

        // Initialize the PopupWindow
        val popupWindow = PopupWindow(
            menuView,
            ViewGroup.LayoutParams.WRAP_CONTENT, // Width matches the anchor view width
            ViewGroup.LayoutParams.WRAP_CONTENT, // Height wraps the content
            true // Focusable to handle clicks outside the menu
        )

        // Set click listeners for menu options
        val invite_salon: TextView = menuView.findViewById(R.id.invite_salon)
        val invite_user: TextView = menuView.findViewById(R.id.invite_user)
        val delete_user: TextView = menuView.findViewById(R.id.delete_user)

        invite_salon.setOnClickListener {
            var nextFragment = FragmentInvite()
            nextFragment.type = "Saloon"
            nextFragment.memberId = member?.userId ?: 0
            (activity as HomeActivity?)?.loadFragment(nextFragment)
            // Handle Option 1 click
            popupWindow.dismiss()
        }

        invite_user.setOnClickListener {
            inActiveMember()
            // Handle Option 2 click
            popupWindow.dismiss()
        }

        delete_user.setOnClickListener {
            deleteMember()
            // Handle Option 3 click
            popupWindow.dismiss()
        }

        // Show the PopupWindow below the anchor view
        popupWindow.showAsDropDown(anchor, 0, 10) // Adjust offset as needed
    }

    private fun setData() {
        // Implement the RequestListener here
        val iconRequestListener = object : RequestListener<Drawable> {

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                Log.d("imageLoaded", "success ${memberDetails?.fullName}")

                binding.imgProfilePic.scaleType = ImageView.ScaleType.CENTER_CROP
                return false
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                binding.imgProfilePic.scaleType = ImageView.ScaleType.CENTER_INSIDE
                Log.d("imageLoaded", "failed ${memberDetails?.fullName}")
                return false
            }


        }
        Glide.with(binding.imgProfilePic)
            .load(memberDetails?.picture)
            .circleCrop()
            .placeholder(R.drawable.topperformer)
            .listener(iconRequestListener)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // Cache both original & transformed image
            .skipMemoryCache(false)  // Cache in memory
            .into(binding.imgProfilePic)
        binding.txtName.text = memberDetails?.fullName
        binding.txtCountry.text = memberDetails?.nationality
        binding.txtStyle.text = "Missing style"
        binding.txtBookingsCount.text =
            "${memberDetails?.totalBookings} ${if (memberDetails?.totalBookings?.toInt() == 1) "booking" else "bookings"}"
        binding.txtRatings.text = "${memberDetails?.tRating} (${memberDetails?.numReviews} ${
            if (memberDetails?.numReviews?.toInt() == 1) "review" else "reviews"
        })"
        if (memberDetails?.workingToday == true) {
            binding.llStatus.setBackgroundDrawable(
                ResourcesCompat.getDrawable(
                    requireContext().resources,
                    R.drawable.green_70_rounded,
                    requireContext().theme
                )
            )
            binding.txtStatusTexts.text = "working today"
        } else {
            binding.llStatus.setBackgroundDrawable(
                ResourcesCompat.getDrawable(
                    requireContext().resources,
                    R.drawable.red_70_rounded,
                    requireContext().theme
                )
            )
            binding.txtStatusTexts.text = "on leave today"
        }
        setWorkingHourAdopter()
        setServicesAdapter()
        setHolidaysAdopter()
    }

    private fun setHolidaysAdopter() {
        if (memberDetails?.UserHolidays != null) {
            memberDetails?.UserHolidays?.size?.let {
                if (it > 0) {
                    binding.txtHolidaysData.visibility = View.GONE
                    binding.rcyHolidays.visibility = View.VISIBLE
                } else {
                    binding.txtHolidaysData.visibility = View.VISIBLE
                    binding.rcyHolidays.visibility = View.GONE
                }
            }
            holidaysList.clear()
            holidaysMap.clear()
            for (holiday in memberDetails?.UserHolidays!!) {
                holidaysMap.put(holiday?.startDate ?: "", holiday?.id ?: 0)
                holidaysList.add(holiday?.startDate ?: "")
            }
            val clickListener = object : OnItemClickListner {
                override fun onItemClick(pos: Int) {
                    position = pos
                    deleteHoliday(holidaysMap[holidaysList[pos]]?.toInt() ?: 0)
                }
            }
            binding.rcyHolidays.layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            holidayListAdapter = HolidayListAdapter(
                holidaysList,
                false,
                clickListener
            )
            binding.rcyHolidays.adapter = holidayListAdapter
        }

    }

    private fun setServicesAdapter() {
        // Set adapter
        binding.gridView.adapter = ServiceGridviewAdapter(servicesList, requireContext())
    }

    private fun setWorkingHourAdopter() {
        workingHourList.clear()
        if (memberDetails?.UserWorkHours != null) {
            for (hour in memberDetails?.UserWorkHours!!) {
                workingHourList.add(
                    WorkingHourData(
                        1,
                        hour?.day ?: "",
                        "${hour?.openingTime} - ${hour?.closingTime}"
                    )
                )

            }
        }
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(pos: Int) {
                position = pos
                deleteWorkingHour(workingHourList[pos].title)
            }
        }
        binding.rcyWorkingHours.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        workingHoursAdopter = WorkingHoursAdapter(
            workingHourList,
            requireContext(),
            clickListener
        )
        binding.rcyWorkingHours.adapter = workingHoursAdopter


    }


    override fun getViewModel(): Class<MemberViewModel> {
        return MemberViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentViewMemberBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        MemberRepository(remoteDataSource.buildApi(MemberApi::class.java, requireContext()))

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.setSystemWindow(true)
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, false)
        (activity as HomeActivity?)?.changeStatusBarColor(requireActivity().resources.getColor(R.color.colorPrimary))
        (activity as HomeActivity?)?.hideTabs()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as HomeActivity?)?.setSystemWindow(true)
            (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, false)
            (activity as HomeActivity?)?.changeStatusBarColor(requireActivity().resources.getColor(R.color.colorPrimary))
            (activity as HomeActivity?)?.hideTabs()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
        (activity as HomeActivity?)?.changeStatusBarColor(requireActivity().resources.getColor(R.color.white))
        (activity as HomeActivity?)?.showTabs()
    }

    private fun getMemberDetails(reload: Boolean) {
        viewModel.getMemberDetails.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status == 1) {
                        servicesList.clear()
                        memberDetails = it.value.data
                        Log.d("response", "success " + it.toString())
                        for (service in it.value.data.userServices) {
                            servicesList.add(service!!.service.name)
                        }
                        setData()
                    }
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())
                    binding.swipeRefresh.isRefreshing = false
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.getMemberDetails(member?.userId ?: 0)
        if (!reload) {
            (activity as HomeActivity?)?.showLoadingIndicator()
        }
    }

    private fun inActiveMember() {
        viewModel.inActiveMember.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    requireView().snackbar("Member inactive successfully")
                    (activity as HomeActivity?)?.popFragment()
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())

                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.inActiveMember(member?.id ?: 0)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun deleteMember() {
        viewModel.deleteMember.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    requireView().snackbar("Member inactive successfully")
                    (activity as HomeActivity?)?.popFragment()
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())

                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.deleteMember(member?.id ?: 0)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun deleteHoliday(holidayId: Int) {
        viewModel.removeHoliday.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        holidaysList.removeAt(position)
                        holidayListAdapter?.notifyDataSetChanged()
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
        viewModel.removeHoliday(member?.userId ?: 0, holidayId)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    private fun deleteWorkingHour(day: String) {
        viewModel.removeWorkingHour.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        workingHourList.removeAt(position)
                        workingHoursAdopter?.notifyDataSetChanged()
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
        viewModel.removeWorkingHour(member?.userId ?: 0, day)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

}