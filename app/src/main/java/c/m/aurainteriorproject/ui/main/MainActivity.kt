package c.m.aurainteriorproject.ui.main

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import c.m.aurainteriorproject.R
import c.m.aurainteriorproject.model.WallpaperResponse
import c.m.aurainteriorproject.util.gone
import c.m.aurainteriorproject.util.visible
import com.github.babedev.dexter.dsl.runtimePermission
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), MainView {

    private lateinit var presenter: MainPresenter
    private lateinit var mainAdapter: MainAdapter
    private val contentData: MutableList<WallpaperResponse> = mutableListOf()

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

        // refresh data
        swipe_refresh_main.setOnRefreshListener {
            swipe_refresh_main.isRefreshing = false
            presenter.getWallpaper()
        }

        // setup recycler view
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        mainAdapter = MainAdapter(contentData) {}
        rv_wallpaper.setHasFixedSize(true)
        rv_wallpaper.adapter = mainAdapter
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
        shimmerStart()
        tv_no_data_main.gone()
        rv_wallpaper.gone()
    }

    override fun hideLoading() {
        shimmerStop()
        tv_no_data_main.gone()
        rv_wallpaper.visible()
    }

    override fun showNoDataResult() {
        shimmerStop()
        tv_no_data_main.visible()
        rv_wallpaper.gone()
    }

    override fun getWallpaper(wallpaperData: List<WallpaperResponse>) {
        contentData.clear()
        contentData.addAll(wallpaperData)
        mainAdapter.notifyDataSetChanged()
    }

    // shimmer loading animation start
    private fun shimmerStart() {
        shimmer_frame_main.visible()
        shimmer_frame_main.startShimmer()
    }

    // shimmer loading animation stop
    private fun shimmerStop() {
        shimmer_frame_main.gone()
        shimmer_frame_main.stopShimmer()
    }
}
