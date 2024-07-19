package store.msolapps.flamingo.presentation.auth

import androidx.fragment.app.Fragment
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.parcel.Parcelize
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentMapBinding
import java.util.*

private const val TAG = "MapFragment_TAG"

class MapFragment(
    private val latStored: Double? = null,
    private val lngStored: Double? = null,
    private var fromHome: Boolean? = false
) : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private var currentMarker: Marker? = null
    private var defaultLocation: LatLng? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currentLocation: Location? = null
    private var currentLatLng: LatLng? = null
    private var adminArea: String? = ""
    private var subAdminArea: String? = ""
    private var streetName: String? = ""
    private var addressLine: String? = ""
    private var coordinates: String? = ""
    private var goverment: String? = ""

    private var lat: String? = null
    private var lng: String? = null
    private val args: MapFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        lat = latStored.toString()
        lng = lngStored.toString()
        args.let {
            fromHome = it.fromHome
        }
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        if (!Places.isInitialized()) {
            Places.initialize(
                requireContext(),
                getString(R.string.google_maps_key),
            )
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        listenerViews()
        initAutoCompleteSearch()

        return binding.root
    }

    private fun initAutoCompleteSearch() {
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                as AutocompleteSupportFragment?
        autocompleteFragment!!.setHint(getString(R.string.search_edit_text))
            .setPlaceFields(listOf(Place.Field.LAT_LNG, Place.Field.NAME))
        autocompleteFragment.setCountries("EG")
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                drawMarker(LatLng(p0.latLng!!.latitude, p0.latLng!!.longitude))
            }

            override fun onError(p0: Status) {
                Log.d("onError", "onError: $p0")
            }
        })

        autocompleteFragment.requireView()
            .findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_clear_button)
            .setOnClickListener {
                autocompleteFragment.setText("")
                fetchLocation()
            }
    }


    private fun listenerViews() {
        binding.backIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.currentLocationIcon.setOnClickListener {
            fetchLocation()
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )
            return
        }

        val task = fusedLocationProviderClient?.lastLocation
        task!!.addOnSuccessListener { location ->
            if (location != null) {
                currentMarker?.remove()
                this.currentLocation = location
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    fetchLocation()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (currentLocation != null) {
            val latLng = LatLng(currentLocation?.latitude!!, currentLocation?.longitude!!)
            drawMarker(latLng)
        } else {

            // Check for location permission
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    1000
                )
                return
            }

            // Enable location layer on map
            mMap.isMyLocationEnabled = true

            // Get the last known location
            fusedLocationProviderClient!!.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {

                    if (defaultLocation == null) {
                        defaultLocation = LatLng(30.6022089, 32.2815174)
                    }

                    if (lat == null || lng == null) {
                        //defaultLocation = LatLng(lat!!.toDouble(), lng!!.toDouble())
                        defaultLocation = LatLng(30.6022089, 32.2815174)
                    }

                    drawMarker(defaultLocation!!)

                }
            }
        }

        mMap.setOnMapClickListener { p0 ->
            if (currentMarker != null) {
                currentMarker?.remove()
                val newLatLng = LatLng(p0.latitude, p0.longitude)
                drawMarker(newLatLng)
            }
        }
    }

    private fun drawMarker(latLng: LatLng) {
        val markOption = MarkerOptions().position(latLng).draggable(true)
        binding.textLocation.text = getAddress(latLng.latitude, latLng.longitude)
        lat = latLng.latitude.toString()
        lng = latLng.longitude.toString()
        defaultLocation = LatLng(latLng.latitude, latLng.longitude)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        currentMarker = mMap.addMarker(markOption)
        currentMarker?.showInfoWindow()

        binding.btnConfirmLocation.setOnClickListener {

            val addressChoose = getAddressDetails(LatLng(latLng.latitude, latLng.longitude))
            val bundle = Bundle().apply {
                putParcelable("item", addressChoose)
                putBoolean("fromHome", fromHome!!)
            }
            Log.d("address","address: $addressChoose")
            if (fromHome!!) {
                findNavController().navigate(R.id.action_mapFragment2_to_addAddressFragment, bundle)
            } else {
                findNavController().navigate(R.id.action_mapFragment_to_addAddressFragment, bundle)
            }
        }
    }

    private fun getAddress(lat: Double, lng: Double): String {
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, lng, 1)
        return address!![0].getAddressLine(0).toString()
    }

    private fun getAddressDetails(latLng: LatLng): AddressData {
        val addresses: List<Address>
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)!!
        val address = addresses[0].getAddressLine(0)
        Log.d("default","is ${addresses[0]}")
        lat = addresses[0].latitude.toString()
        lng = addresses[0].longitude.toString()
        defaultLocation = LatLng(addresses[0].latitude, addresses[0].longitude)

        addressLine = address
        coordinates = "$lat,$lng"
        adminArea = addresses[0].subAdminArea
        subAdminArea = addresses[0].locality
        streetName = addresses[0].thoroughfare
        goverment = addresses[0].adminArea

        if (adminArea == null) {
            adminArea = ""
        }

        if (subAdminArea == null) {
            subAdminArea = ""
        }

        if (streetName == null) {
            streetName = ""
        }

        if (addressLine == null) {
            addressLine = ""
        }
        if (goverment == null) {
            goverment = ""
        }

        return AddressData(
            addressLine = addressLine,
            coordinates = coordinates,
            adminArea = adminArea,
            subAdminArea = subAdminArea,
            streetName = streetName,
            goverment = goverment
        )
    }


    override fun onResume() {
        super.onResume()
        currentMarker?.remove()
        initAutoCompleteSearch()
    }
}

@Parcelize
data class AddressData(
    val addressLine: String?,
    val coordinates: String?,
    val adminArea: String?,
    val subAdminArea: String?,
    val streetName: String?,
    val goverment:String?
) : Parcelable
