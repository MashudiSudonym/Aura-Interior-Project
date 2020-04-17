package c.m.aurainteriorproject.ui.order

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import c.m.aurainteriorproject.R
import c.m.aurainteriorproject.model.OrderResponse
import c.m.aurainteriorproject.ui.signin.SignInActivity
import c.m.aurainteriorproject.util.gone
import c.m.aurainteriorproject.util.visible
import kotlinx.android.synthetic.main.activity_order.*
import org.jetbrains.anko.startActivity

class OrderActivity : AppCompatActivity(), OrderView {

    private lateinit var presenter: OrderPresenter
    private lateinit var orderAdapter: OrderAdapter
    private var content: MutableList<OrderResponse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = OrderPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.firebaseInit()

        setSupportActionBar(toolbar_order)
        supportActionBar?.apply {
            title = getString(R.string.my_order_list)
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        // get order data
        presenter.getOrder()

        // refresh data
        swipe_refresh_order.setOnRefreshListener {
            swipe_refresh_order.isRefreshing = false
            presenter.getOrder()
        }

        // setup recycler view
        setupRecyclerView()
    }

    override fun onDetachView() {
        presenter.onDetach()
    }

    override fun onDestroy() {
        onDetachView()
        super.onDestroy()
    }

    override fun showLoading() {
        shimmerStart()
        tv_no_data_order.gone()
        rv_order.gone()
    }

    override fun hideLoading() {
        shimmerStop()
        tv_no_data_order.gone()
        rv_order.visible()
    }

    override fun showNoDataResult() {
        shimmerStop()
        tv_no_data_order.visible()
        rv_order.gone()
    }

    override fun getOrder(orderData: List<OrderResponse>) {
        content.clear()
        content.addAll(orderData)
        orderAdapter.notifyDataSetChanged()
    }

    override fun returnToSignIn() {
        finish() // finish this activity
        startActivity<SignInActivity>()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(content) { response ->
            //startActivity<DetailActivity>(Constants.UID to response.uid)
        }
        rv_order.setHasFixedSize(true)
        rv_order.adapter = orderAdapter
    }

    // shimmer loading animation start
    private fun shimmerStart() {
        shimmer_frame_order.visible()
        shimmer_frame_order.startShimmer()
    }

    // shimmer loading animation stop
    private fun shimmerStop() {
        shimmer_frame_order.gone()
        shimmer_frame_order.stopShimmer()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
