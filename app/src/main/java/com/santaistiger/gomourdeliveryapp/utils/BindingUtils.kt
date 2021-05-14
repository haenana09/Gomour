package com.santaistiger.gomourdeliveryapp.utils

import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.model.Status
import com.santaistiger.gomourdeliveryapp.ui.customview.DestinationView
import com.santaistiger.gomourdeliveryapp.ui.customview.MessageView
import com.santaistiger.gomourdeliveryapp.ui.customview.PriceView
import com.santaistiger.gomourdeliveryapp.ui.orderdetail.StoreAdapter
import java.text.NumberFormat
import java.text.SimpleDateFormat

val TAG = "BindingUtils"

object BindingUtils {
    val numberFormat = NumberFormat.getInstance()

    @BindingAdapter("bind_store_list")
    @JvmStatic
    fun bindStoreList(recyclerView: RecyclerView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            val order = item.value!!
            val stores = order.stores

            if (recyclerView.adapter == null) {
                recyclerView.layoutManager =
                    LinearLayoutManager(recyclerView.context)
                recyclerView.adapter = StoreAdapter()
            }
            (recyclerView.adapter as StoreAdapter).items = stores ?: ArrayList()
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    @BindingAdapter("bind_destination")
    @JvmStatic
    fun bindDestination(view: DestinationView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            val order = item.value!!
            view.binding.tvStoreAddress.text = order.destination?.getDisplayName()

            when (order.status) {
                Status.PICKUP_COMPLETE -> {
                    setUnClickable(view.binding.btnPickupComplete)
                }

                Status.DELIVERY_COMPLETE -> {
                    setUnClickable(view.binding.btnPickupComplete)
                    setUnClickable(view.binding.btnDeliveryComplete)
                }
            }
        }
    }

    private fun setUnClickable(btn: AppCompatButton) {
        btn.isClickable = false
        btn.text = btn.hint.toString()
    }


    @BindingAdapter("bind_message")
    @JvmStatic
    fun bindMessage(view: MessageView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            view.binding.tvMessage.text = item.value!!.message
        }
    }

    @BindingAdapter("bind_price")
    @JvmStatic
    fun bindPrice(view: PriceView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            val order = item.value!!
            var price = order.deliveryCharge ?: 0
            for (store in order.stores!!) {
                price += store.cost ?: 0
            }
            view.binding.tvPrice.text = numberFormat.format(price) + " 원"
        }
    }

    @BindingAdapter("bind_delivery_time")
    @JvmStatic
    fun bindDeliveryTime(view: TextView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            val order = item.value!!
            view.text = if (order.status == Status.DELIVERY_COMPLETE) {
                SimpleDateFormat("yyyy-MM-dd (EEE) hh:mm 배달 완료").format(order.deliveryTime)
            } else {
                SimpleDateFormat("yyyy-MM-dd (EEE) hh:mm 도착 예정").format(order.deliveryTime)
            }
        }
    }

    @BindingAdapter("bind_cost")
    @JvmStatic
    fun bindCost(view: Button, item: Int?) {
        if (item != null) {
            view.isClickable = false
            view.text = numberFormat.format(item)
        }
    }

}

