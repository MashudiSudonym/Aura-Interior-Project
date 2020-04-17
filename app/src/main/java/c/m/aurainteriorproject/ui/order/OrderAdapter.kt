package c.m.aurainteriorproject.ui.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import c.m.aurainteriorproject.R
import c.m.aurainteriorproject.model.OrderResponse
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_order.*

class OrderAdapter(
    private val content: List<OrderResponse>,
    private val onClickListener: (OrderResponse) -> Unit
) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder =
        OrderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order, parent, false)
        )

    override fun getItemCount(): Int = content.size
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) =
        holder.bind(content[position], onClickListener)

    class OrderViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(content: OrderResponse, onClickListener: (OrderResponse) -> Unit) {
            item_order_layout.setOnClickListener { onClickListener(content) }
            tv_date.text = content.orderDate
            tv_name.text = content.name
            tv_type_wallpaper.text = content.typeWallpaperOrder
        }
    }
}