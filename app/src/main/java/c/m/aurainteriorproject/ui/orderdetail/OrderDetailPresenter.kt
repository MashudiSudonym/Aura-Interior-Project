package c.m.aurainteriorproject.ui.orderdetail

import android.util.Log
import c.m.aurainteriorproject.model.OrderResponse
import c.m.aurainteriorproject.util.base.BasePresenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrderDetailPresenter : BasePresenter<OrderDetailView> {
    private var orderDetailView: OrderDetailView? = null
    private lateinit var databaseReference: DatabaseReference
    private lateinit var authentication: FirebaseAuth

    override fun onAttach(view: OrderDetailView) {
        orderDetailView = view
    }

    override fun onDetach() {
        orderDetailView = null
    }

    fun firebaseInit() {
        databaseReference = FirebaseDatabase.getInstance().reference
        authentication = FirebaseAuth.getInstance()
    }

    private fun userAuthentication() = authentication.currentUser != null

    fun getOrder(uid: String) {
        if (userAuthentication()) {
            databaseReference.child("orders")
                .orderByChild("uid")
                .equalTo(uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("Err!!", "Load Error : $databaseError", databaseError.toException())
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val orderData = dataSnapshot.children.flatMap {
                            mutableListOf(it.getValue(OrderResponse::class.java))
                        }

                        orderData.forEach { result ->
                            orderDetailView?.getOrder(
                                result?.name.toString(),
                                result?.address.toString(),
                                result?.phone.toString(),
                                result?.latitude as Double,
                                result.longitude as Double,
                                result.typeWallpaperOrder.toString(),
                                result.priceEstimation.toString(),
                                result.rollEstimation.toString(),
                                result.orderStatus as Int,
                                result.orderDate.toString()
                            )
                        }
                    }
                })
        }
    }
}