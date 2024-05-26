package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var myMap: GoogleMap //nepieciešams, lai izmantotu Google Map karti
    private lateinit var userLocation: FusedLocationProviderClient //nepieciešams atrašanās vietas atjaunināšanai
    private val locationPermissionCode = 1 //nepieciešams atrašanās vietas atļaujas pieprasīšanai priekšplānā
    private lateinit var locationCallback: LocationCallback //nepieciešams atrašanās vietas atjaunināšanai

    // parāda Google karti izmantojot .xml resursa datni
    // izmanto Android Location API klasi FusedLocationClient, lai parādītu pēdējo atgriezto lietotāja atrašanās vietu
    // pārbauda, vai lietotnei ir atrašanās vietas atļaujas un ja tā ir taisnība tad izsauc initializeMap()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)
        userLocation = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        } else {
            initializeMap()
        }
    }

    //kartes ielādei izmanto fragmentu ar id "mapFragment" no activity_google_map.xml datnes
    //ielādē kartes datus asinhroni (ielādē karti, nebloķējot lietotāja saskarni)
    private fun initializeMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    //kad karte ir pilnīgi ielādēta, pārbauda atrašanās vietas atļaujas un izsauc setUpLocationListener()
    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            myMap.isMyLocationEnabled = true
            setUpLocationListener()
        }
    }
    //klausās lietotāja atrašanās vietas atjauninājumus
    private fun setUpLocationListener() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000) //lietotne pieprasa visprecīzāku atrašanās vietu, kas ir pieejama
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(10000)
            .setMaxUpdateDelayMillis(5000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    //pārbauda, vai lietotāja atrašanās vieta ir imitēta un parāda lietotājam "MOCKED LOCATION" paziņojumu ja tā ir taisnība
                    if (location.isMock)
                        Toast.makeText(applicationContext, "MOCKED LOCATION", Toast.LENGTH_LONG).show()
                }
            }
        }
        //pirms pieprasīt atrašanās vietas atjauninājumus, lietotne pārbauda, vai tai ir atļauja to izmantot
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            userLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }
    //ja lietotnei ir atļauja izmantot atrašanās vietu - tā parāda karti un atjaunina lietotāja atrašanās vietu, ja atļaujas nav - izvada paziņojumu
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeMap()
            setUpLocationListener()
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}
