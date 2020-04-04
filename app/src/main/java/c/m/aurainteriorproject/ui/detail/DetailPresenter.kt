package c.m.aurainteriorproject.ui.detail

import android.util.Log
import c.m.aurainteriorproject.model.WallpaperResponse
import c.m.aurainteriorproject.util.base.BasePresenter
import com.google.firebase.database.*

class DetailPresenter : BasePresenter<DetailView> {
    private var detailView: DetailView? = null
    private lateinit var databaseReference: DatabaseReference

    override fun onAttach(view: DetailView) {
        detailView = view
    }

    override fun onDetach() {
        detailView = null
    }

    fun firebaseInit() {
        databaseReference = FirebaseDatabase.getInstance().reference
    }

    fun getDetailWallpaper(uid: String) {
        databaseReference.child("wallpapers")
            .orderByChild("uid")
            .equalTo(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Err!!", "Load Error : $databaseError", databaseError.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val detailWallpaperData = dataSnapshot.children.flatMap {
                        mutableListOf(it.getValue(WallpaperResponse::class.java))
                    }

                    detailWallpaperData.forEach { response ->
                        detailView?.getDetailWallpaper(
                            response?.type as String,
                            response.imageWallpaper as String
                        )
                    }
                }
            })
    }
}