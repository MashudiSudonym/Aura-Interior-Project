package c.m.aurainteriorproject.ui.main

import android.util.Log
import c.m.aurainteriorproject.model.WallpaperResponse
import c.m.aurainteriorproject.util.base.BasePresenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging

@Suppress("UNCHECKED_CAST")
class MainPresenter : BasePresenter<MainView> {
    private var mainView: MainView? = null
    private lateinit var databaseReference: DatabaseReference
    private lateinit var authentication: FirebaseAuth

    override fun onAttach(view: MainView) {
        mainView = view
    }

    override fun onDetach() {
        mainView = null
    }

    fun firebaseInit() {
        databaseReference = FirebaseDatabase.getInstance().reference
        authentication = FirebaseAuth.getInstance()

        // get user UID
        val customerUID = authentication.currentUser?.uid

        // subscribe this application for firebase cloud messaging topic
        FirebaseMessaging.getInstance().subscribeToTopic("$customerUID")
            .addOnCompleteListener { task ->
                var msg = "subscribe"
                if (!task.isSuccessful) {
                    msg = "failed"
                }
                Log.d("FCM DEBUG", "$msg $customerUID")
            }
    }

    private fun userAuthentication() = authentication.currentUser != null

    fun getWallpaper() {
        if (userAuthentication()) {
            mainView?.showLoading()
            databaseReference.child("wallpapers")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("Err!!", "Load Error : $databaseError", databaseError.toException())

                        mainView?.showNoDataResult()
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val wallpaperData = dataSnapshot.children.flatMap {
                            mutableListOf(it.getValue(WallpaperResponse::class.java))
                        }

                        when (wallpaperData.isEmpty()) {
                            true -> mainView?.showNoDataResult()
                            false -> {
                                mainView?.hideLoading()
                                mainView?.getWallpaper(wallpaperData as List<WallpaperResponse>)
                            }
                        }
                    }
                })
        } else {
            mainView?.returnToSignIn()
        }
    }
}