package com.yaelsoriano.uberclonkotlin.activities

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.Listener
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.maps.android.SphericalUtil
import com.yaelsoriano.uberclonkotlin.R
import com.yaelsoriano.uberclonkotlin.databinding.ActivityMapBinding
import com.yaelsoriano.uberclonkotlin.providers.AuthProvider
import com.yaelsoriano.uberclonkotlin.providers.GeoProvider

class MapActivity : AppCompatActivity(), OnMapReadyCallback, Listener {
    private lateinit var binding: ActivityMapBinding
    private var googleMap: GoogleMap? = null
    private var easyWayLocation: EasyWayLocation? = null
    private var myLocationLatLng: LatLng? = null
    private var markerDriver: Marker? = null
    private val geoProvider = GeoProvider()
    private val authProvider = AuthProvider()
    //Google Places
    private var places: PlacesClient? = null
    private var autoCompleteOrigin: AutocompleteSupportFragment? = null
    private var autoCompleteDestination: AutocompleteSupportFragment? = null
    private var originName = ""
    private var destinationName = ""
    private var originLatLng: LatLng? = null
    private var destinationLatLng: LatLng? = null

    private var isLocationEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val locationRequest = LocationRequest.create().apply {
            interval = 0
            fastestInterval = 0
            priority = Priority.PRIORITY_HIGH_ACCURACY
            smallestDisplacement = 1f
        }

        easyWayLocation = EasyWayLocation(this, locationRequest, false, false, this)
        locationPermission.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))

        startGooglePlaces()
    }

    private fun instanceAutocompleteOrigin() {
        autoCompleteOrigin = supportFragmentManager.findFragmentById(R.id.placesAutocompleteOrigin) as AutocompleteSupportFragment
        autoCompleteOrigin?.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS

            )
        )
        autoCompleteOrigin?.setHint("Lugar de recogida")
        autoCompleteOrigin?.setCountry("MX")
        autoCompleteOrigin?.setOnPlaceSelectedListener(
            object: PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    originName = place.name!!
                    originLatLng = place.latLng
                    Log.d("***Origin_Place", "Address: $originName")
                    Log.d("***Origin_Place", "Lat: ${originLatLng?.latitude}")
                    Log.d("***Origin_Place", "Lng: ${originLatLng?.longitude}")
                }
                override fun onError(place: Status) {

                }
            }
        )
    }

    private fun instanceAutocompleteDestination() {
        autoCompleteDestination = supportFragmentManager.findFragmentById(R.id.placesAutocompleteDestination) as AutocompleteSupportFragment
        autoCompleteDestination?.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS

            )
        )
        autoCompleteDestination?.setHint("Destino")
        autoCompleteDestination?.setCountry("MX")
        autoCompleteDestination?.setOnPlaceSelectedListener(
            object: PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    destinationName = place.name!!
                    destinationLatLng = place.latLng
                    Log.d("***Destination_Place", "Address: $destinationName")
                    Log.d("***Destination_Place", "Lat: ${destinationLatLng?.latitude}")
                    Log.d("***Destination_Place", "Lng: ${destinationLatLng?.longitude}")
                }
                override fun onError(place: Status) {

                }
            }
        )
    }

    private fun limitSearch() {
        val northSide = SphericalUtil.computeOffset(myLocationLatLng, 10000.0, 0.0)
        val southSide = SphericalUtil.computeOffset(myLocationLatLng, 10000.0, 180.0)

        autoCompleteOrigin?.setLocationBias(RectangularBounds.newInstance(southSide, northSide))
        autoCompleteDestination?.setLocationBias(RectangularBounds.newInstance(southSide, northSide))
    }

    private fun startGooglePlaces() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
        }

        places = Places.createClient(this)
        instanceAutocompleteOrigin()
        instanceAutocompleteDestination()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        easyWayLocation?.endUpdates();
    }

    val locationPermission = registerForActivityResult(
        ActivityResultContracts
        .RequestMultiplePermissions()) { permission ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when {
                permission.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    Log.d("***Localización", "Permiso concedido")
                    easyWayLocation?.startLocation()
                }
                permission.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Log.d("***Localización", "Permiso concedido con limitación")
                    easyWayLocation?.startLocation()
                }
                else -> {
                    Log.d("***Localización", "Permiso NO concedido")
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        //Desactivar el punto azul de la ubicación que viene por default
        googleMap?.isMyLocationEnabled = true

        try {
            val success = googleMap?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.style)
            )
            if (!success!!) {
                Log.d("***Localización", "No se pudo encontrar el estilo")
            }
        } catch (e: Resources.NotFoundException) {
            Log.d("***Localización", "Error: ${e.toString()}")
        }
    }

    override fun locationOn() {
    }

    override fun currentLocation(location: Location) { //Actualización de la posición en tiempo real
        //Latitud y Longitud de mi posición actual
        myLocationLatLng = LatLng(location.latitude, location.longitude)

        googleMap?.moveCamera(
            CameraUpdateFactory.newCameraPosition(
            CameraPosition.builder().target(myLocationLatLng!!).zoom(17f).build()
        ))

        if (!isLocationEnabled) {
            isLocationEnabled = true
            limitSearch()
        }
    }

    override fun locationCancelled() {
    }
}