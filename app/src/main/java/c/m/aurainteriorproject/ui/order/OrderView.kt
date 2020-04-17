package c.m.aurainteriorproject.ui.order

import c.m.aurainteriorproject.model.OrderResponse
import c.m.aurainteriorproject.util.base.BaseView

interface OrderView : BaseView {
    fun showLoading()
    fun hideLoading()
    fun showNoDataResult()
    fun getOrder(orderData: List<OrderResponse>)
    fun returnToSignIn()
}