package c.m.aurainteriorproject.ui.main

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.aurainteriorproject.R
import c.m.aurainteriorproject.model.WallpaperResponse
import c.m.aurainteriorproject.ui.detail.DetailActivity
import c.m.aurainteriorproject.ui.order.OrderActivity
import c.m.aurainteriorproject.ui.signin.SignInActivity
import c.m.aurainteriorproject.util.Constants
import c.m.aurainteriorproject.util.gone
import c.m.aurainteriorproject.util.visible
import com.firebase.ui.auth.AuthUI
import com.github.babedev.dexter.dsl.runtimePermission
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(), MainView {

    private lateinit var presenter: MainPresenter
    private lateinit var mainAdapter: MainAdapter
    private val content: MutableList<WallpaperResponse> = mutableListOf()

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
        mainAdapter = MainAdapter(content) { response ->
            startActivity<DetailActivity>(Constants.UID to response.uid)
        }
        rv_wallpaper.setHasFixedSize(true)
        rv_wallpaper.adapter = mainAdapter
    }

    private fun permissionDevice() {
        runtimePermission {
            permission(Manifest.permission.ACCESS_FINE_LOCATION) {}
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
        content.clear()
        content.addAll(wallpaperData)
        mainAdapter.notifyDataSetChanged()
    }

    override fun returnToSignIn() {
        finish() // finish this activity
        startActivity<SignInActivity>()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_my_order_list -> {
                startActivity<OrderActivity>()
                true
            }
            R.id.menu_sign_out -> {
                // user Sign Out
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Finish this activity
                            finish()

                            // return to sign in activity
                            startActivity<SignInActivity>()
                        }
                    }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
