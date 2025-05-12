package com.tt.muzien.ui.saloon

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.tt.muzien.R
import com.tt.muzien.data.dto.AddSaloonData
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.SaloonApi
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.data.requests.UpdateSaloonRequest
import com.tt.muzien.databinding.FragmentSearchAddressBinding
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.Helper.closeKeyboard


class FragmentSearchAddress :
    BaseFragment<SaloonViewModel, FragmentSearchAddressBinding, SaloonRepository>(),
    OnMapReadyCallback {
    private lateinit var placesClient: PlacesClient
    private lateinit var mMap: GoogleMap
    var selectedAddress: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var isEdit = false
    var saloonId: Int = 0
    var isUserTyping = true

    // Adapter for suggestions
    val placeIds = mutableListOf<String>()
    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val apiKey = getApiKey()
        // Initialize the Places SDK
        Places.initialize(requireContext(), apiKey)
        placesClient = Places.createClient(requireContext())
        //setup google maps
        initGoogleMaps()
        binding.llBack.setOnClickListener {
            (activity as HomeActivity?)?.popFragment()
        }
//        binding.txtSearch.setOnClickListener {
//            startAutocompleteActivity()
//        }
        binding.cnstSave.setOnClickListener {
            if (selectedAddress != "") {
                if (isEdit) {
                    updateSaloon()
                } else {
                    AddSaloonData.address = selectedAddress
                    AddSaloonData.addressLat = latitude
                    AddSaloonData.addressLng = longitude
                    // Inside your current fragment before popping
                    val resultBundle = Bundle().apply {
                        putString("address", selectedAddress) // Replace with your data
                    }

                    setFragmentResult("requestKey", resultBundle)
                    (activity as HomeActivity?)?.popFragment()
                }
            }
        }
        setAddressAutoComplete()
    }

    private fun initGoogleMaps() {
        // Initialize the SupportMapFragment and request the map.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Set the map type
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN  // Change to desired map type
        if (latitude != 0.0 && longitude != 0.0) {
            updateMarkerLocation(latitude, longitude)
            setAddressData()
        }
    }

    private fun getApiKey(): String {
        val appInfo = requireActivity().packageManager.getApplicationInfo(
            requireActivity().packageName,
            PackageManager.GET_META_DATA
        )
        return appInfo.metaData.getString("com.google.android.geo.API_KEY")
            ?: throw IllegalStateException("API_KEY not found in manifest")
    }

    // Add function to update marker location
    private fun updateMarkerLocation(latitude: Double, longitude: Double) {
        moveMarker(LatLng(latitude, longitude)!!)
        setAddressData()
    }

    private fun setAddressData() {
        binding.txtAddress.text = selectedAddress
        binding.txtSearch.setText(selectedAddress)
    }

    private fun moveMarker(latLng: LatLng) {
        val bitmapDescriptor = vectorToBitmapDescriptor(requireContext(), R.drawable.location_icon)

        // Move the marker to the new location

        mMap.addMarker(
            MarkerOptions().position(latLng).title(selectedAddress)
                .icon(bitmapDescriptor)
        )

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    fun vectorToBitmapDescriptor(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(context, vectorResId)
        if (vectorDrawable == null) {
            return null
        }

        val width = vectorDrawable.intrinsicWidth
        val height = vectorDrawable.intrinsicHeight
        vectorDrawable.setBounds(0, 0, width, height)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    private fun setAddressAutoComplete() {
        val popupWindow = PopupWindow(requireContext())
        val suggestionList = mutableListOf<String>() // To store the suggestions
        val adapter = ArrayAdapter(requireContext(), R.layout.places_list_item, suggestionList)
        val listView = ListView(requireContext())
        listView.setBackgroundColor(Color.WHITE)
        listView.adapter = adapter
        popupWindow.contentView = listView
        popupWindow.isFocusable = false
        popupWindow.isOutsideTouchable = true
        popupWindow.width = ViewGroup.LayoutParams.MATCH_PARENT
        popupWindow.height = ViewGroup.LayoutParams.WRAP_CONTENT
        listView.setOnItemClickListener { _, _, position, _ ->
            closeKeyboard(requireActivity())
            val selectedPlace = suggestionList[position]
            fetchPlaceDetails(placeIds[position]) { placeDetails ->
                selectedAddress = placeDetails.address
                latitude = placeDetails.latLng.latitude
                longitude = placeDetails.latLng.longitude
                updateMarkerLocation(placeDetails.latLng.latitude, placeDetails.latLng.longitude)
            }
            popupWindow.dismiss() // Close the dropdown
        }
        // Fetch suggestions as the user types
        binding.txtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUserTyping) { // Only fetch suggestions if the user is typing
                    val query = s.toString()
                    if (query.isNotEmpty()) {
                        fetchPlaces(query) { places ->

                            suggestionList.clear()
                            placeIds.clear()
                            for (prediction in places) {
                                placeIds.add(prediction.placeId)
                                suggestionList.add(prediction.getPrimaryText(null).toString())
                            }

                            adapter.notifyDataSetChanged()

                            // Show dropdown below EditText
                            if (!popupWindow.isShowing) {
                                popupWindow.showAsDropDown(binding.llSearch)
                            }
                        }
                    } else {
                        suggestionList.clear()
                        adapter.notifyDataSetChanged()
                        popupWindow.dismiss()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun fetchPlaces(query: String, callback: (List<AutocompletePrediction>) -> Unit) {
        val placesClient = Places.createClient(requireContext())
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val places = response.autocompletePredictions
                callback(places)
            }
            .addOnFailureListener { exception ->
                Log.e("Places API", "Error fetching predictions", exception)
            }


    }
    private fun fetchPlaceDetails(placeId: String, callback: (Place) -> Unit) {
        val placesClient = Places.createClient(context)
        val placeFields = listOf(
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LOCATION,
        )

        val fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build()
        placesClient.fetchPlace(fetchPlaceRequest)
            .addOnSuccessListener { response ->
                val place = response.place
                callback(place)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }
    private fun startAutocompleteActivity() {
        // Set the fields to specify which types of place data to return.
        val fields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(requireContext())
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        if (place.latLng != null) {
                            selectedAddress = place.address
                            latitude = place.latLng.latitude
                            longitude = place.latLng.longitude
                            updateMarkerLocation(place.latLng.latitude, place.latLng.longitude)
                        }
                    }
                }

                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        // Handle the error
                        println(status.statusMessage)
                    }
                }

                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation
                }
            }
        }
    }

    override fun getViewModel(): Class<SaloonViewModel> {
        return SaloonViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSearchAddressBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        SaloonRepository(
            remoteDataSource.buildApi(SaloonApi::class.java, requireContext())
        )

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.hideTabs()
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
                        val resultBundle = Bundle().apply {
                            putBoolean("reload", true) // Replace with your data
                        }
                        setFragmentResult("requestKey", resultBundle)
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
            UpdateSaloonRequest(
                address = selectedAddress,
                locationLat = latitude.toString(),
                locationLong = longitude.toString()
            )
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
    }


}