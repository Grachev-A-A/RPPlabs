package ru.mirea.ikbo1218.grachev.lab8

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.gms.common.api.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private val TAG = MainActivity::class.simpleName
    private lateinit var mapFragment: SupportMapFragment

    private var selectedPlaceLL: LatLng = LatLng(55.751244, 37.618423)
    private var selectedPlaceName: String = "Placeholder"

    private var originLat: Double = 0.0
    private var originLong: Double = 0.0
    private lateinit var originName: String
    private var destinationLat: Double = 0.0
    private var destinationLong: Double = 0.0
    private lateinit var destinationName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.pointsMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupAutoCompleteFragment()
    }

    private fun setupAutoCompleteFragment() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }

        val autocompleteFragmentOrigin =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment_or)
                    as AutocompleteSupportFragment?

        val autocompleteFragmentDestination =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment_dest)
                    as AutocompleteSupportFragment?

        autocompleteFragmentOrigin!!.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )

        autocompleteFragmentDestination!!.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )

        autocompleteFragmentOrigin.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                if(intent.hasExtra("originName")) {
                    intent.removeExtra("originName")
                    intent.removeExtra("originLat")
                    intent.removeExtra("originLong")
                }

                selectedPlaceLL = place.latLng!!
                selectedPlaceName = place.name!!
                originLat = place.latLng!!.latitude
                originLong = place.latLng!!.longitude
                originName = place.name!!

                mapFragment.getMapAsync(this@MainActivity)
                Log.i(TAG, "Place: " + place.name + ", " + place.id)
            }

            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
            }
        })

        autocompleteFragmentDestination.setOnPlaceSelectedListener(object :
            PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                if(intent.hasExtra("destinationName")) {
                    intent.removeExtra("destinationName")
                    intent.removeExtra("destinationLat")
                    intent.removeExtra("destinationLong")
                }

                selectedPlaceLL = place.latLng!!
                selectedPlaceName = place.name!!
                destinationLat = place.latLng!!.latitude
                destinationLong = place.latLng!!.longitude
                destinationName = place.name!!

                mapFragment.getMapAsync(this@MainActivity)
                Log.i(TAG, "Place: " + place.name + ", " + place.id
                )
            }

            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedPlaceLL, 8.5f))
        mMap!!.addMarker(
            MarkerOptions()
                .position(selectedPlaceLL)
                .title(selectedPlaceName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )
    }

    override fun onResume() {
        super.onResume()
        if (mMap != null) {
            mMap!!.clear()
        }
    }

    override fun onRestart() {
        super.onRestart()
        intent.removeExtra("originLat")
        intent.removeExtra("originLong")
        intent.removeExtra("originName")
        intent.removeExtra("originLat")
        intent.removeExtra("originLat")
        intent.removeExtra("originLat")

    }

    fun onClickGoToRoutes(view: View) {
        if(originLat != 0.0 && originLong != 0.0 &&
            destinationLat != 0.0 && destinationLong != 0.0) {
            val goToRoutes = Intent(this, RouteActivity::class.java)
            goToRoutes.putExtra("originLat", originLat)
            goToRoutes.putExtra("originLong", originLong)
            goToRoutes.putExtra("originName", originName)

            goToRoutes.putExtra("destinationLat", destinationLat)
            goToRoutes.putExtra("destinationLong", destinationLong)
            goToRoutes.putExtra("destinationName", destinationName)
            startActivity(goToRoutes)
        }
        else
            Toast.makeText(this, "Enter start location and destination previously"
                , Toast.LENGTH_SHORT).show()
    }
}
