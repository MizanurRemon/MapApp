package com.example.mapapp

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mapapp.Utils.Urls
import com.example.mapapp.ViewModel.MapViewModel
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.mapapp.Models.LatLongResponse
import com.example.mapapp.Models.NearbyResponse
import com.example.mapapp.Utils.Helpers
import com.example.mapapp.Utils.Helpers.isLocationEnabled
import com.example.mapapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private lateinit var mapView: MapView

    lateinit var mapViewModel: MapViewModel

    private lateinit var mainBinding: ActivityMainBinding
    private val permissionId = 2
    private lateinit var mapLibreMap: MapboxMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        // getData("90.36668110638857", "23.83723803415923", "bank")

        mapView = findViewById(R.id.mapView)
        mapView.getMapAsync { map ->

            mapLibreMap = map
            mapLibreMap.setStyle(Urls.styleUrls)
            //map.cameraPosition = CameraPosition.Builder().target(LatLng(0.0, 0.0)).zoom(1.0).build()
        }

        getLocation()
    }

    private fun getData(longitude: String, latitude: String, ptype: String) {
        mapViewModel.nearByPlaces(longitude, latitude, ptype)
        mapViewModel.nearByPlaces.observe(this, Observer {

            loadMarkerOnMap(it)
        })

    }

    private fun loadMarkerOnMap(data: NearbyResponse?) {
        Log.d("dataxx", "marker: ${data.toString()}")

        val markerPosition = mutableListOf<LatLng>()

        val infoIconDrawable = ResourcesCompat.getDrawable(
            this.resources,
            R.drawable.baseline_location_on_24,
            null
        )!!
        val bitmapBlue = infoIconDrawable.toBitmap()
        val bitmapRed = infoIconDrawable
            .mutate()
            .apply { setTint(Color.RED) }
            .toBitmap()

        data!!.places.forEach { feature ->

            val latLng = LatLng(feature.latitude, feature.longitude)
            markerPosition.add(latLng)


            val title = feature.name
            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")


            val dateString =
                formatter.format(time)

            val icon = IconFactory.getInstance(this)
                .fromBitmap(bitmapRed)

            // Use MarkerOptions and addMarker() to add a new marker in map
            val markerOptions = MarkerOptions()
                .position(latLng)
                .title(dateString)
                .snippet(title)
                .icon(icon)
            mapLibreMap.addMarker(markerOptions)
        }

        mapLibreMap.getCameraForLatLngBounds(LatLngBounds.fromLatLngs(markerPosition))?.let {
            val newCameraPosition = CameraPosition.Builder()
                .target(it.target)
                .zoom(it.zoom - 0.5)
                .build()
            mapLibreMap.cameraPosition = newCameraPosition
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }


    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)!!

                        mainBinding.apply {
                            getData(
                                list[0].longitude.toString(),
                                list[0].latitude.toString(),
                                "bank"
                            )

                            Log.d(
                                "dataxx",
                                "getLocation: ${list[0].longitude.toString()} ${list[0].latitude.toString()}"
                            )
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

}