package c.m.aurainteriorproject.ui.order

import android.util.Log
import c.m.aurainteriorproject.model.OrderResponse
import c.m.aurainteriorproject.util.base.BasePresenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@Suppress("UNCHECKED_CAST")
class OrderPresenter : BasePresenter<OrderView> {
    private var orderView: OrderView? = null
    private lateinit var databaseReference: DatabaseReference
    private lateinit var authentication: FirebaseAuth

    override fun onAttach(view: OrderView) {
        orderView = view
    }

    override fun onDetach() {
        orderView = null
    }

    fun firebaseInit() {
        databaseReference = FirebaseDatabase.getInstance().reference
        authentication = FirebaseAuth.getInstance()
    }

    private fun userAuthentication() = authentication.currentUser != null

    fun getOrder() {
        if (userAuthentication()) {
            val customerUID = authentication.currentUser?.uid

            orderView?.showLoading()

            databaseReference.child("orders")
                .orderByChild("customerUID")
                .equalTo(customerUID)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("Err!!", "Load Error : $databaseError", databaseError.toException())

                        orderView?.showNoDataResult()
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val orderData = dataSnapshot.children.flatMap {
                            mutableListOf(it.getValue(OrderResponse::class.java))
                        }

                        when (orderData.isEmpty()) {
                            true -> orderView?.showNoDataResult()
                            false -> {
                                orderView?.hideLoading()
                                orderView?.getOrder(orderData as List<OrderResponse>)
                            }
                        }
                    }
                })
        } else {
            orderView?.returnToSignIn()
        }
    }
}