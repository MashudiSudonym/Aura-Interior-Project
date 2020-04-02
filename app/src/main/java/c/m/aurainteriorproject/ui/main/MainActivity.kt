package c.m.aurainteriorproject.ui.main

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import c.m.aurainteriorproject.R
import c.m.aurainteriorproject.model.WallpaperResponse
import com.github.babedev.dexter.dsl.runtimePermission
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), MainView {

    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = MainPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.firebaseInit()

        setSupportActionBar(toolbar_main)
        supportActionBar?.apply { title = getString(R.string.app_name) }

        // get permission device
        permissionDevice()

        // get wallpaper data
        presenter.getWallpaper()
    }

    private fun permissionDevice() {
        runtimePermission {
            permission(Manifest.permission.ACCESS_FINE_LOCATION) {
                granted {
                    toast(getString(R.string.permission_granted))
                }

                denied {
                    toast(getString(R.string.permission_denied))
                }
            }
        }
    }

    override fun onDetachView() {
        presenter.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDetachView()
    }

    override fun showLoading() {
        TODO("Not yet implemented")
    }

    override fun hideLoading() {
        TODO("Not yet implemented")
    }

    override fun getWallpaper(wallpaperData: List<WallpaperResponse?>) {
        TODO("Not yet implemented")
    }
}
