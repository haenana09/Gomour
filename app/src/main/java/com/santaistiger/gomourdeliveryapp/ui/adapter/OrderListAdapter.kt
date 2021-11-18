package com.santaistiger.gomourdeliveryapp.ui.adapter

/**
 * Created by Jieun Park.
 */


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.santaistiger.gomourdeliveryapp.R
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.model.Status
import com.santaistiger.gomourdeliveryapp.databinding.ItemOrderInfoBinding
import com.santaistiger.gomourdeliveryapp.ui.customview.StoreNameView
import com.santaistiger.gomourdeliveryapp.ui.view.OrderListFragmentDirections
import kotlinx.android.synthetic.main.item_list_store.view.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class OrderListAdapter(val context: Context?) : RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {
    var orderList = mutableListOf<Order>()

    override fun getItemCount(): Int = orderList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderId = orderList[position].orderId.toString()
        if (context != null) {
            holder.bind(orderList[position], context)
        }
        holder.itemView.setOnClickListener {
            // 아이템 클릭하면 해당 아이템의 주문 번호를 넘겨주며 주문 상세 화면으로 이동
            it.findNavController()
                    .navigate(
                        OrderListFragmentDirections.actionOrderListFragmentToOrderDetailFragment(
                            orderId
                        )
                    )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder private constructor(val binding: ItemOrderInfoBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(order: Order, context: Context) {
            // 주문 날짜 설정
            val orderDate = order.orderDate
            val dateFormat = SimpleDateFormat("yyyy-MM-dd (EE)  kk:mm", Locale("ko", "KR"))
            binding.orderTimeString.setText(dateFormat.format(orderDate))

            // 배달 상태 설정
            binding.orderStatusString.text = when (order.status) {
                Status.PREPARING -> "상품 준비중"
                Status.PICKUP_COMPLETE -> "픽업 완료"
                Status.DELIVERY_COMPLETE -> "배달 완료"
            }

            // 가게 정보 설정
            binding.stores.removeAllViewsInLayout()
            if (order.stores!!.size >= 1) {
                for (store in order.stores!!) {
                    val view = StoreNameView(context)
                    view.list_store_name_string.text = store.place.placeName
                    binding.stores.addView(view)
                }
            }

            // 도착지 설정
            binding.listDestination.list_store_img.setImageResource(R.drawable.ic_destination)
            binding.listDestination.list_store_name_string.text = order.destination?.placeName

            // 금액 설정
            var totalItemPrice = 0
            var isPickUpDone = true
            val decimalformat = DecimalFormat("#,###")
            for (store in order.stores!!) {
                if (store.cost != null) {
                    totalItemPrice += store.cost!!
                } else {
                    isPickUpDone = false
                    break
                }
            }

            // 모든 장소에서 픽업 완료한 경우의 금액 설정
            if (isPickUpDone == true) {
                val totalPrice = totalItemPrice + order.deliveryCharge!!
                binding.orderPriceString.setText(decimalformat.format(totalPrice) + " 원")
            }

            // 아직 모든 장소에서 픽업이 완료되지 않은 경우의 금액 설정
            else {
                binding.orderPriceString.setText("@ + " + decimalformat.format(order.deliveryCharge) + " (배달료) 원")
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemOrderInfoBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}
