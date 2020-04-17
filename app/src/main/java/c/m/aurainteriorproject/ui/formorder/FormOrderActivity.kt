package c.m.aurainteriorproject.ui.formorder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import c.m.aurainteriorproject.R
import c.m.aurainteriorproject.model.OrderResponse
import c.m.aurainteriorproject.ui.main.MainActivity
import c.m.aurainteriorproject.util.Constants
import c.m.aurainteriorproject.util.Converter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_form_order.*
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

class FormOrderActivity : AppCompatActivity() {

    private var typeWallpaper: String? = ""
    private var priceEstimationResult: Double? = 0.0
    private var rollEstimationResult: Double? = 0.0
    private lateinit var locationManager: LocationManager
    private var locLatitude: Double? = 0.0
    private var locLongitude: Double? = 0.0
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var markerArrayList: ArrayList<Marker> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_order)

        // map select location manual
        // map ui initiate
        map_location.onCreate(savedInstanceState)

        val intent = intent
        typeWallpaper = intent.getStringExtra(Constants.TYPE)
        priceEstimationResult = intent.getDoubleExtra(Constants.RESULT_PRICE_ESTIMATION, 0.0)
        rollEstimationResult = intent.getDoubleExtra(Constants.RESULT_ROLL_ESTIMATION, 0.0)

        setSupportActionBar(toolbar_form_order)
        supportActionBar?.apply {
            title = getString(R.string.form_order_title)
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        // get edit text value
        val name = edt_name.text
        val address = edt_address.text
        val phone = edt_phone.text

        // get location latitude and longitude value
        getGPSCoordinate()

        // show map
        map_location.getMapAsync { googleMap ->
            // setup maps type
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            // Control settings
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isCompassEnabled = true

            googleMap.setOnMapClickListener { latLng ->
                if (markerArrayList.size > 0) {
                    val markerToRemove = markerArrayList[0]

                    // remove marker from list
                    markerArrayList.remove(markerToRemove)

                    // remove marker from the map
                    markerToRemove.remove()
                }

                // add marker to clicked point
                val markerOptions = MarkerOptions().position(latLng).draggable(true)
                val currentMarker = googleMap.addMarker(markerOptions)

                // add current marker to array list
                markerArrayList.add(currentMarker)

                // set location manual
                locLatitude = latLng.latitude
                locLongitude = latLng.longitude
            }

            if (markerArrayList.isNullOrEmpty()) {
                googleMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder().target(
                            LatLng(locLatitude as Double, locLongitude as Double)
                        ).zoom(16f).build()
                    )
                )

                // add current marker to array list
                markerArrayList.add(
                    googleMap.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                locLatitude as Double,
                                locLongitude as Double
                            )
                        ).draggable(true)
                    )
                )

            } else {
                googleMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder().target(
                            LatLng(
                                markerArrayList[0].position.latitude,
                                markerArrayList[0].position.longitude
                            )
                        ).zoom(16f).build()
                    )
                )
            }
        }

        // order button
        btn_order.setOnClickListener {
            alert(getString(R.string.alert_order), getString(R.string.alert_order_title)) {
                yesButton {
                    sendOrderData(name, address, phone)
                }
                noButton { }
            }.show()
        }
    }

    private fun sendOrderData(
        name: Editable,
        address: Editable,
        phone: Editable
    ) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val authentication = FirebaseAuth.getInstance()
        val uid = databaseReference.child("customers").push().key
        val customerUID = authentication.currentUser?.uid
        val orderDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val orderData = OrderResponse(
            uid,
            name.toString(),
            address.toString(),
            phone.toString(),
            locLatitude,
            locLongitude,
            typeWallpaper,
            Converter.rupiah(priceEstimationResult as Double),
            rollEstimationResult.toString(),
            customerUID,
            0,
            orderDate
        )

        databaseReference.child("orders")
            .child(uid.toString())
            .setValue(orderData)
            .addOnSuccessListener {
                alert(
                    getString(R.string.alert_message_order_success),
                    getString(R.string.alert_title_order_success)
                ) {
                    okButton {
                        finish()
                        startActivity<MainActivity>()
                    }
                }.apply {
                    isCancelable = false
                    show()
                }
            }
            .addOnFailureListener { e ->
                alert("$e", "ERROR!!") {
                    okButton {}
                }.apply {
                    isCancelable = false
                    show()
                }

                Log.e("ERROR!!", "$e")
            }
    }

    private fun getGPSCoordinate() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (hasGps || hasNetwork) {
                if (hasGps) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 0, 0f, object : LocationListener {
                            override fun onLocationChanged(location: Location?) {
                                if (location != null) {
                                    locationGps = location
                                    locLatitude = locationGps?.latitude
                                    locLongitude = locationGps?.longitude
                                }
                            }

                            override fun onStatusChanged(
                                provider: String?,
                                status: Int,
                                extras: Bundle?
                            ) {
                            }

                            override fun onProviderEnabled(provider: String?) {}

                            override fun onProviderDisabled(provider: String?) {}
                        })

                    val localGpsLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (localGpsLocation != null) locationGps = localGpsLocation
                } else {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                if (hasNetwork) {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 0, 0f, object : LocationListener {
                            override fun onLocationChanged(location: Location?) {
                                if (location != null) {
                                    locationNetwork = location
                                    locLatitude = locationNetwork?.latitude
                                    locLongitude = locationNetwork?.longitude
                                }
                            }

                            override fun onStatusChanged(
                                provider: String?,
                                status: Int,
                                extras: Bundle?
                            ) {
                            }

                            override fun onProviderEnabled(provider: String?) {}

                            override fun onProviderDisabled(provider: String?) {}
                        })

                    val localNetworkLocation =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (localNetworkLocation != null) locationNetwork = localNetworkLocation
                } else {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }

                if (locationGps != null && locationNetwork != null) {
                    if (locationGps?.accuracy as Float > locationNetwork?.accuracy as Float) {
                        locLatitude = locationNetwork?.latitude
                        locLongitude = locationNetwork?.longitude
                    } else {
                        locLatitude = locationNetwork?.latitude
                        locLongitude = locationNetwork?.longitude
                    }
                }
            } else {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, 101)
        }
    }

    @Suppress("SameParameterValue")
    private fun requestPermission(permissionType: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permissionType), requestCode)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        map_location.onResume()
        super.onResume()
    }

    override fun onLowMemory() {
        map_location.onLowMemory()
        super.onLowMemory()
    }

    override fun onDestroy() {
        map_location.onDestroy()
        super.onDestroy()
    }
}
