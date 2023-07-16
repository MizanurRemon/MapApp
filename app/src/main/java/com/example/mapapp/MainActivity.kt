package com.example.mapapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mapapp.Models.NearbyResponse
import com.example.mapapp.Utils.Urls
import com.example.mapapp.ViewModel.MapViewModel
import com.example.mapapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    //private lateinit var mapView: MapView

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


        //mapView = findViewById(R.id.mapView)
        mainBinding.mapView.getMapAsync { map ->

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

    @SuppressLint("SimpleDateFormat")
    private fun loadMarkerOnMap(data: NearbyResponse?) {
        //Log.d("dataxx", "marker: ${data.toString()}")

        val markerPosition = mutableListOf<LatLng>()

        val infoIconDrawable = ResourcesCompat.getDrawable(
            this.resources,
            R.drawable.baseline_location_on_24,
            null
        )!!

        val bitmapRed = infoIconDrawable
            .mutate()
            .apply { setTint(Color.RED) }
            .toBitmap()

        data!!.places.forEach { feature ->

            val latLng = LatLng(feature.latitude, feature.longitude)
            markerPosition.add(latLng)

            val icon = IconFactory.getInstance(this)
                .fromBitmap(bitmapRed)

            val markerOptions = MarkerOptions()
                .position(latLng)
                .title(feature.name)
                .snippet("${feature.Address}, ${feature.area}, ${feature.city}\n${feature.subType}")
                .icon(icon)
            mapLibreMap.addMarker(markerOptions)

            //mapLibreMap.setOnMarkerClickListener(n)
        }

        mapLibreMap.getCameraForLatLngBounds(LatLngBounds.fromLatLngs(markerPosition))?.let {
            val newCameraPosition = CameraPosition.Builder()
                .target(it.target)
                .zoom(it.zoom - 0.5)
                .build()
            mapLibreMap.cameraPosition = newCameraPosition
        }

//        var infoWindow : InfoWindow

        mapLibreMap.setInfoWindowAdapter { marker ->

            val infoLayout = LinearLayout(this)
            infoLayout.orientation = LinearLayout.VERTICAL
            infoLayout.setPadding(10, 10, 10, 10)
            infoLayout.background = ContextCompat.getDrawable(this, R.drawable.round_corner)
            //infoLayout.setBackgroundColor(Color.BLACK)
            infoLayout.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )

            val title = TextView(this)
            title.text = marker.title
            title.setTextColor(Color.WHITE)
            title.textSize = 16F
            title.typeface = Typeface.DEFAULT_BOLD
            infoLayout.addView(title)

            val type = TextView(this)
            type.text = marker.snippet
            type.setTextColor(Color.LTGRAY)
            type.textSize = 12F
            infoLayout.addView(type)

            infoLayout
        }

        // mapLibreMap.setOnPolygonClickListener(Ma)

    }

    override fun onStart() {
        super.onStart()
        mainBinding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mainBinding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mainBinding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mainBinding.mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mainBinding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mainBinding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainBinding.mapView.onDestroy()
    }


    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
//                        val list: List<Address> =
//                            geocoder.getFromLocation(location.latitude, location.longitude, 1)!!

                        Log.d(
                            "dataxx",
                            "getLocation: ${location.latitude} ${location.longitude}"
                        )

                        mainBinding.apply {
                            getData(
                                location.longitude.toString(),
                                location.latitude.toString(),
                                "bank"
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