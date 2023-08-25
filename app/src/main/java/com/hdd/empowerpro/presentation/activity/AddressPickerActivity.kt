package com.hdd.empowerpro.presentation.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.hdd.empowerpro.R
import java.io.Serializable
import java.text.DateFormat
import java.util.*

class AddressPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var toolbar: Toolbar
    private lateinit var pin_map: ImageView
    private lateinit var fab_current_location: FloatingActionButton
    private lateinit var selected_address: TextView
    private lateinit var selected_cordinates: TextView
    private lateinit var use_this_location: TextView

    companion object {
        private val TAG = AddressPickerActivity::class.java.simpleName

        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
        private val AUTOCOMPLETE_REQUEST_CODE = 12
        private val REQUEST_CHECK_SETTINGS = 0x1

        private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 60 * 1000   //1 min
        private val UPDATE_INTERVAL_IN_MINUTE: Long = 5 * 60 * 1000     //5 min
        private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2

        // Keys for storing activity state in the Bundle.
        private val KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates"
        private val KEY_LOCATION = "location"
        private val KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string"

        val RESULT_ADDRESS = "address"
        val ARG_LAT_LNG = "arg_lat_lng"
        val ARG_LIST_PIN = "list_pins"
        val ARG_ZOOM_LEVEL = "level_zoom"
    }

    private var mMap: GoogleMap? = null
    private var mAddress: Address? = null
    private var mZoomLevel = 10.0f
    private var mDefaultLocation: LatLng? = null
    private var mPinList: ArrayList<Pin>? = null

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var mSettingsClient: SettingsClient? = null

    private var mLocationRequest: LocationRequest? = null

    private var mLocationSettingsRequest: LocationSettingsRequest? = null

    private var mLocationCallback: LocationCallback? = null

    private var mCurrentLocation: Location? = null

    private var mRequestingLocationUpdates: Boolean? = null

    private var mLastUpdateTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPlacesApi()
        setContentView(R.layout.activity_address_picker)

        toolbar = findViewById(R.id.toolbar)
        pin_map = findViewById(R.id.pin_map)
        fab_current_location = findViewById(R.id.fab_current_location)
        selected_address = findViewById(R.id.selected_address)
        selected_cordinates = findViewById(R.id.selected_cordinates)
        use_this_location = findViewById(R.id.use_this_location)

        if (intent.hasExtra(ARG_LAT_LNG)) {
            val latLng = intent.getSerializableExtra(ARG_LAT_LNG) as MyLatLng
            mDefaultLocation = LatLng(latLng.latitude, latLng.longitude)
        }
        if (intent.hasExtra(ARG_LIST_PIN)) {
            mPinList = intent.getSerializableExtra(ARG_LIST_PIN) as ArrayList<Pin>
        }
        mZoomLevel = intent.getFloatExtra(ARG_ZOOM_LEVEL, 20.0f)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(toolbar)
        mRequestingLocationUpdates = true
        mLastUpdateTime = ""

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)

        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()

        val mapFragment: SupportMapFragment = this.supportFragmentManager?.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        use_this_location.setOnClickListener {
            val intent = Intent()
            intent.putExtra(RESULT_ADDRESS, mAddress)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        fab_current_location.setOnClickListener {
            if (mRequestingLocationUpdates!! && checkPermissions()) {
                if (mCurrentLocation != null && mMap != null) {
                    val newLatLong=LatLng(mCurrentLocation?.latitude!!,mCurrentLocation?.longitude!!)
                    mMap?.animateCamera(CameraUpdateFactory.newLatLng(newLatLong))
                    setLocationFromGeoCoder(newLatLong)
                } else {
                    startLocationUpdates()
                }
            } else if (!checkPermissions()) {
                requestPermissions()
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        if (mRequestingLocationUpdates!! && checkPermissions()) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        // Remove location updates to save battery.
        stopLocationUpdates()
    }

    private fun initPlacesApi() {
        try {
            val applicationInfo = packageManager.getApplicationInfo(packageName,PackageManager.GET_META_DATA)
            val bundle = applicationInfo.metaData
            val apiKey = bundle.getString("com.google.android.geo.API_KEY")
            if (!apiKey.isNullOrEmpty()) {
                // Initialize the SDK
                Places.initialize(applicationContext, apiKey)
            }

        } catch (e: java.lang.Exception) {
            //Resolve error for not existing meta-tag, inform the developer about adding his api key
        }
    }

    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                    KEY_REQUESTING_LOCATION_UPDATES
                )
            }

            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                mCurrentLocation = savedInstanceState.getParcelable<Location>(KEY_LOCATION)
                moveMapToLocation(
                    LatLng(mCurrentLocation?.latitude!!,mCurrentLocation?.longitude!!)
                )
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        startLocationUpdates()
        mMap = googleMap

        if (!mPinList?.isNullOrEmpty()!!) {
            for (pin in mPinList!!) {
                val options = MarkerOptions().position(LatLng(pin.latLng.latitude, pin.latLng.longitude))
                if (pin.title?.isNotEmpty()!!) {
                    options.title(pin.title)
                    mMap?.addMarker(options)
                }
            }
        }

        if (mDefaultLocation != null) {
            moveMapToLocation(
                LatLng(
                    mDefaultLocation?.latitude!!,
                    mDefaultLocation?.longitude!!
                )
            )
        } else if (mCurrentLocation != null) {
            mMap?.animateCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(
                        mCurrentLocation?.latitude!!,
                        mCurrentLocation?.longitude!!
                    )
                )
            )
        }
        mMap?.setOnCameraIdleListener {
            val midLatLng = mMap?.cameraPosition?.target
            setLocationFromGeoCoder(midLatLng)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setLocationFromGeoCoder(midLatLng: LatLng?) {
        try {
            val geo = Geocoder(
                applicationContext,
                Locale.getDefault()
            )
            selected_cordinates.text =
                "(" + midLatLng?.latitude!!.toString() + "," + midLatLng.longitude + ")"

            val addresses =
                geo.getFromLocation(midLatLng.latitude, midLatLng.longitude, 1)
            if (addresses!!.isEmpty()) {
                selected_address.text = "Waiting for Location"
            } else {
                if (addresses.size > 0) {
                    mAddress = addresses[0]
                    selected_address.text = addresses[0].getAddressLine(0)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace() // getFromLocation() may sometimes fail
        }
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()

        mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MINUTE

        mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS

        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
//                super.onLocationResult(locationResult)
                val isFoundFirstTime = mCurrentLocation == null
                mCurrentLocation = locationResult.lastLocation
                if (isFoundFirstTime && mCurrentLocation != null && mDefaultLocation == null) {
                    moveMapToLocation(
                        LatLng(
                            mCurrentLocation?.latitude!!,
                            mCurrentLocation?.longitude!!
                        )
                    )
                }
                mLastUpdateTime = DateFormat.getTimeInstance().format(Date())
            }
        }
    }

    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> Log.i(TAG,"User agreed to make required location settings changes.")
                Activity.RESULT_CANCELED -> {
                    Log.i(TAG, "User chose not to make required location settings changes.")
                    mRequestingLocationUpdates = false
                }
            }

            AUTOCOMPLETE_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        var place = Autocomplete.getPlaceFromIntent(data!!)
                        moveMapToLocation(place.latLng!!)
                        selected_address?.text = place.address
                        Log.i(TAG, "Place: " + place.name + ", " + place.id)
                    }
                    AutocompleteActivity.RESULT_ERROR -> {
                        // TODO: Handle the error.
                        var status = Autocomplete.getStatusFromIntent(data!!)
                        Log.i(TAG, status.statusMessage!!)
                    }
                    RESULT_CANCELED -> {
                        // The user canceled the operation.
                    }
                }
            }

        }
    }

    private fun moveMapToLocation(latLng: LatLng) {
        val center = CameraUpdateFactory.newLatLng(latLng)
        val zoom = CameraUpdateFactory.zoomTo(mZoomLevel)
        mMap?.moveCamera(zoom)
        mMap?.moveCamera(center)
        mMap?.uiSettings?.isZoomControlsEnabled = true
    }

    private fun startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient!!.checkLocationSettings(mLocationSettingsRequest)

            .addOnSuccessListener(this, object : OnSuccessListener<LocationSettingsResponse> {
                override fun onSuccess(locationSettingsResponse: LocationSettingsResponse) {
                    val requiredFineLocationPermission =ActivityCompat.checkSelfPermission(applicationContext,Manifest.permission.ACCESS_FINE_LOCATION)
                    val requiredCoarseLocationPermission =ActivityCompat.checkSelfPermission(applicationContext,Manifest.permission.ACCESS_COARSE_LOCATION)
                    val permissionGranted =PackageManager.PERMISSION_GRANTED

                    if ( requiredFineLocationPermission != permissionGranted && requiredCoarseLocationPermission != permissionGranted) {
                        return
                    }
                    mFusedLocationClient!!.requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback, Looper.myLooper()
                    )
                    mRequestingLocationUpdates = true
                }
            })

            .addOnFailureListener(this) { e ->
                when ((e as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i(TAG,"Location settings are not satisfied. Attempting to upgrade " + "location settings ")
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(
                                this@AddressPickerActivity,
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (sie: IntentSender.SendIntentException) {
                            Log.i(TAG, "PendingIntent unable to execute request.")
                        }

                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage = "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                        Toast.makeText(this@AddressPickerActivity,errorMessage,Toast.LENGTH_LONG).show()
                        mRequestingLocationUpdates = false
                    }
                }
            }
    }

    private fun stopLocationUpdates() {
        if (!mRequestingLocationUpdates!!) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.")
            return
        }
        mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
            .addOnCompleteListener(this) {
                mRequestingLocationUpdates = false }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == R.id.action_search) {

            val fields: List<Place.Field> = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )

            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
        return super.onOptionsItemSelected(item!!)
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates!!)
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation)
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime)
        super.onSaveInstanceState(savedInstanceState)
    }

    /**
     * Shows a [Snackbar].
     *
     * @param mainTextStringId The id for the string resource for the SnackBar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the SnackBar action.
     */

    private fun showSnackBar(mainTextStringId: Int, actionStringId: Int,listener: View.OnClickListener) {
        Snackbar.make(findViewById<View>(android.R.id.content),getString(mainTextStringId),
            Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(actionStringId), listener).show()
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this, Manifest.permission.ACCESS_FINE_LOCATION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showSnackBar(R.string.permission_rationale,
                android.R.string.ok, View.OnClickListener {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this@AddressPickerActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE
                    )
                })
        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(
                this@AddressPickerActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> {
                    // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    Log.i(TAG, "User interaction was cancelled.")
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates")
                    startLocationUpdates()
                }
                else -> {
                    showSnackBar(R.string.permission_denied_explanation,
                        R.string.settings, View.OnClickListener {
                            openAppSystemSettings()
                        })
                }
            }
        }
    }

    private fun Context.openAppSystemSettings() {
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        })
    }
}

class MyLatLng(var latitude: Double, var longitude: Double) : Serializable
class Pin(var latLng: MyLatLng, var title: String?) : Serializable