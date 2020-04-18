package c.m.aurainteriorproject.ui.orderdetail

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import c.m.aurainteriorproject.R
import c.m.aurainteriorproject.util.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_order_detail.*

class OrderDetailActivity : AppCompatActivity(), OrderDetailView {

    private lateinit var presenter: OrderDetailPresenter
    private var uid: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        map_location.onCreate(savedInstanceState)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = OrderDetailPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.firebaseInit()

        val intent = intent
        uid = intent.getStringExtra(Constants.UID)

        presenter.getOrder(uid.toString())
    }

    override fun onDetachView() {
        presenter.onDetach()
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
        onDetachView()
        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    override fun getOrder(
        name: String,
        address: String,
        phone: String,
        latitude: Double,
        longitude: Double,
        typeWallpaper: String,
        priceEstimation: String,
        rollEstimation: String,
        statusOrder: Int,
        dateOrder: String
    ) {
        // initial toolbar
        setSupportActionBar(toolbar_order_detail)
        supportActionBar?.apply {
            title = name
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        // initial text
        tv_name.text = name
        tv_address.text = address
        tv_phone.text = phone
        tv_type_wallpaper.text = typeWallpaper
        tv_price_estimation.text = priceEstimation
        tv_roll_estimation.text = "$rollEstimation roll wallpaper"
        tv_order_date.text = dateOrder

        // initial order status
        when (statusOrder) {
            0 -> tv_order_status.text = getString(R.string.waiting_status)
            1 -> tv_order_status.text = getString(R.string.order_accept_status)
            2 -> tv_order_status.text = getString(R.string.order_cancel_status)
        }

        // initial map
        // show map
        map_location.getMapAsync { googleMap ->
            // setup maps type
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            // Control settings
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isCompassEnabled = true

            googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder().target(
                        LatLng(latitude, longitude)
                    ).zoom(16f).build()
                )
            )

            googleMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        latitude,
                        longitude
                    )
                ).draggable(true)
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
