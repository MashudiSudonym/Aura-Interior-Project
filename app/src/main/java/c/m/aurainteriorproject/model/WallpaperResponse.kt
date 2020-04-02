package c.m.aurainteriorproject.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class WallpaperResponse(
    var uid: String? = "",
    var type: String? = "",
    var imageWallpaper: String? = "",
    var price: Int? = 0
) {
    @Exclude
    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "type" to type,
        "imageWallpaper" to imageWallpaper,
        "price" to price
    )
}