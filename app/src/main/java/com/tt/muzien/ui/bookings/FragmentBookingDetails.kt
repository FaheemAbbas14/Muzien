package com.tt.muzien.ui.bookings

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tt.muzien.R
import com.tt.muzien.data.SaloonBookingData
import com.tt.muzien.data.dto.BookingServiceDto
import com.tt.muzien.data.network.BookingApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.BookingRepository
import com.tt.muzien.data.requests.UpdateBookingRequest
import com.tt.muzien.data.responses.BookingDetailsData
import com.tt.muzien.databinding.FragmentBookingDetailsBinding
import com.tt.muzien.ui.adapters.BookingServiceListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.FilterSelection
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentBookingDetails :
    BaseFragment<BookingViewModel, FragmentBookingDetailsBinding, BookingRepository>() {
    var bookingDto: SaloonBookingData? = null
    var bookingId: String? = null
    var isFromNotifications=false
    private val bookingServices = arrayListOf<BookingServiceDto>()
    var getBookingDetailsResponse: BookingDetailsData? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgBack.setOnClickListener {
            FilterSelection.filterData = null
            (activity as HomeActivity?)?.popFragment()
        }
//        binding.cnstBookingInfo.setOnClickListener {
//            showPopupDialog()
//        }
        binding.imgBarcode.setOnClickListener {
            showPopupDialog()
        }
        bookingDto?.let { bookingId= it.bookingId }
        getBookingDetails()
    }

    private fun getBookingDetails() {
        viewModel.bookingDetails.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()

                    if (it.value.status != 0) {
                        getBookingDetailsResponse = it.value.data
                        setData()

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
        viewModel.getBookingDetails(bookingId?:"")
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

    override fun getViewModel(): Class<BookingViewModel> {
        return BookingViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentBookingDetailsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        BookingRepository(remoteDataSource.buildApi(BookingApi::class.java, requireContext()))

    @RequiresApi(Build.VERSION_CODES.M)
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        if (!isFromNotifications) {
            (activity as HomeActivity?)?.showTabs()
        }
    }

    private fun setData() {
        binding.txtBookingId.text = "Booking # ${getBookingDetailsResponse?.id}"
        setCustomerData()
        setServiceProvider()
        setBookingServicesAdopter()
    }

    private fun setServiceProvider() {
        binding.txtServiceProviderName.text = getBookingDetailsResponse?.serviceProvider?.fullName
        binding.txtServiceProviderCountry.text =
            getBookingDetailsResponse?.serviceProvider?.nationality
        binding.txtStyle.text = getBookingDetailsResponse?.saloon?.name
        binding.txtAddress.text = getBookingDetailsResponse?.saloon?.address
        // Implement the RequestListener here
        val iconRequestListener = object : RequestListener<Drawable> {

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean,
            ): Boolean {


                binding.imgServiceProviderPic.scaleType = ImageView.ScaleType.CENTER_CROP
                return false
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean,
            ): Boolean {
                binding.imgServiceProviderPic.scaleType = ImageView.ScaleType.CENTER_INSIDE

                return false
            }


        }
        Glide.with(binding.imgServiceProviderPic)
            .load(getBookingDetailsResponse?.serviceProvider?.picture)
            .circleCrop()
            .placeholder(R.drawable.topperformer)
            .listener(iconRequestListener)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // Cache both original & transformed image
            .skipMemoryCache(false)  // Cache in memory
            .into(binding.imgServiceProviderPic)
    }

    private fun setCustomerData() {
        binding.txtName.text = getBookingDetailsResponse?.customer?.fullName
        binding.txtPhone.text = getBookingDetailsResponse?.customer?.phoneNumber
        binding.txtTiming.text =
            "${getBookingDetailsResponse?.date}-${getBookingDetailsResponse?.time}-${getBookingDetailsResponse?.duration} mins"
        // Implement the RequestListener here
        val iconRequestListener = object : RequestListener<Drawable> {

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean,
            ): Boolean {


                binding.imgProfilePic.scaleType = ImageView.ScaleType.CENTER_CROP
                return false
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean,
            ): Boolean {
                binding.imgProfilePic.scaleType = ImageView.ScaleType.CENTER_INSIDE

                return false
            }


        }
        Glide.with(binding.imgProfilePic)
            .load(getBookingDetailsResponse?.customer?.picture)
            .circleCrop()
            .placeholder(R.drawable.topperformer)
            .listener(iconRequestListener)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // Cache both original & transformed image
            .skipMemoryCache(false)  // Cache in memory
            .into(binding.imgProfilePic)
    }

    private fun setBookingServicesAdopter() {
        if (getBookingDetailsResponse != null && getBookingDetailsResponse!!.bookingServices != null) {
            bookingServices.clear()
            var totalServicesDuration = 0
            var totalServicesamount = 0
            for (service in getBookingDetailsResponse!!.bookingServices) {
                totalServicesDuration += service.serviceDetails.duration.toInt()
                totalServicesamount += service.serviceDetails.price.toInt()
                bookingServices.add(
                    BookingServiceDto(
                        service.serviceDetails.name,
                        "${service.serviceDetails.duration} mins",
                        "SAR${service.serviceDetails.price}"
                    )
                )

            }
            if (totalServicesamount > 0) {
                bookingServices.add(
                    BookingServiceDto(
                        "Total",
                        "${totalServicesDuration} mins",
                        "SAR${totalServicesamount}"
                    )
                )
            }
        }

        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
                //    showPopupDialog()

            }
        }
        binding.rcyServices.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rcyServices.adapter =
            BookingServiceListAdapter(
                bookingServices,
                requireContext(),
                clickListener
            )
    }

    private fun showPopupDialog() {
        // Create Dialog
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.complete_booking, null)
        dialog.setContentView(view)

        // Make dialog background transparent
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Find buttons and handle click events
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val proceedButton = view.findViewById<Button>(R.id.proceed_button)

        btnCancel.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog
        }

        proceedButton.setOnClickListener {
            // Add your logic here (e.g., enable the service)
            dialog.dismiss()
            updateBooking(bookingDto?.bookingId!!, "", "completed")
        }

        // Show the dialog
        dialog.show()
    }

    private fun showCompletedPopupDialog() {
        // Create Dialog
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.completed_success_popup, null)
        dialog.setContentView(view)

        // Make dialog background transparent
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Show the dialog
        dialog.show()
    }

    private fun updateBooking(bookingId: String, reason: String, status: String) {

        viewModel.updateBooking.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    // (activity as HomeActivity?)?.hideLoadingIndicator()
                    showCompletedPopupDialog()
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.updateBooking(bookingId, UpdateBookingRequest(status, reason))
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
}