package c.m.aurainteriorproject.ui.orderdetail

import c.m.aurainteriorproject.util.base.BaseView

interface OrderDetailView : BaseView {
    fun getOrder(
        name: String,
        address: String,
        phone: String,
        latitude: Double,
        longitude: Double,
        typeWallpaper: String,
        priceEstimation: String,
        rollEstimation: String,
        statusOrder: Int,
        dateOrder: String
    )
}