package c.m.aurainteriorproject.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import c.m.aurainteriorproject.R
import c.m.aurainteriorproject.ui.formorder.FormOrderActivity
import c.m.aurainteriorproject.util.Constants
import c.m.aurainteriorproject.util.Converter
import coil.api.load
import com.ceylonlabs.imageviewpopup.ImagePopup
import kotlinx.android.synthetic.main.activity_detail.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import kotlin.math.ceil

class DetailActivity : AppCompatActivity(), DetailView {

    private lateinit var presenter: DetailPresenter
    private var uid: String? = ""
    private var rollEstimationResult: Double = 0.0
    private var priceEstimationResult: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = DetailPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.firebaseInit()

        val intent = intent
        uid = intent.getStringExtra(Constants.UID)

        setSupportActionBar(toolbar_detail)
        supportActionBar?.apply {
            title = getString(R.string.detail_wallpaper)
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        // get detail wallpaper data
        presenter.getDetailWallpaper(uid.toString())

        // cancel order button
        btn_cancel_order.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onDetachView() {
        presenter.onDetach()
    }

    override fun onDestroy() {
        onDetachView()
        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    override fun getDetailWallpaper(type: String, imageWallpaper: String) {
        // show image wallpaper
        img_wallpaper.load(imageWallpaper) {
            crossfade(true)
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }

        // full screen view for image wallpaper
        ImagePopup(this).apply {
            isImageOnClickClose = true
            isHideCloseIcon = true
            isFullScreen = true
            initiatePopupWithGlide(imageWallpaper)
            img_wallpaper.onClick { viewPopup() }
        }

        // wallpaper type
        tv_type_wallpaper.text = type

        // get edit text value
        edt_spacious_room_value.addTextChangedListener { text ->
            // check edit text value
            val editTextValue = text?.trim()
            val resultEditTextValue = when (editTextValue.isNullOrEmpty()) {
                false -> editTextValue.toString()
                true -> 0.toString()
            }

            // calculate estimation
            rollEstimationResult = ceil(resultEditTextValue.toDouble().div(4.5))
            priceEstimationResult = resultEditTextValue.toDouble().times(70000)

            // result
            tv_roll_estimation.text = "$rollEstimationResult roll"
            tv_price_estimation.text = Converter.rupiah(priceEstimationResult)
        }

        // Order Button
        btn_order.setOnClickListener {
            startActivity<FormOrderActivity>(
                Constants.TYPE to type,
                Constants.RESULT_PRICE_ESTIMATION to priceEstimationResult,
                Constants.RESULT_ROLL_ESTIMATION to rollEstimationResult
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
