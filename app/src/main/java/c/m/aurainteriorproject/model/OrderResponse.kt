package c.m.aurainteriorproject.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class OrderResponse(
    var uid: String? = "",
    var name: String? = "",
    var address: String? = "",
    var phone: String? = "0",
    var latitude: Double? = 0.0,
    var longitude: Double? = 0.0,
    var typeWallpaperOrder: String? = "",
    var priceEstimation: String? = "",
    var rollEstimation: String? = "",
    var customerUID: String? = "",
    var orderStatus: Int? = 0
) {
    @Exclude
    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "name" to name,
        "address" to address,
        "phone" to phone,
        "latitude" to latitude,
        "longitude" to longitude,
        "typeWallpaperOrder" to typeWallpaperOrder,
        "priceEstimation" to priceEstimation,
        "rollEstimation" to rollEstimation,
        "customerUID" to customerUID,
        "orderStatus" to orderStatus
    )
}