package c.m.aurainteriorproject.ui.main

import android.util.Log
import c.m.aurainteriorproject.model.WallpaperResponse
import c.m.aurainteriorproject.util.base.BasePresenter
import com.google.firebase.database.*

class MainPresenter : BasePresenter<MainView> {
    private var mainView: MainView? = null
    private lateinit var databaseReference: DatabaseReference

    override fun onAttach(view: MainView) {
        mainView = view
    }

    override fun onDetach() {
        mainView = null
    }

    fun firebaseInit() {
        databaseReference = FirebaseDatabase.getInstance().reference
    }

    fun getWallpaper() {
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
    }
}