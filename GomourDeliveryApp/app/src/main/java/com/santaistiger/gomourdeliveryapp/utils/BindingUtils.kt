package com.santaistiger.gomourdeliveryapp.utils

import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.model.Status
import com.santaistiger.gomourdeliveryapp.ui.adapter.OrderDetailStoreAdapter
import com.santaistiger.gomourdeliveryapp.ui.customview.DetailDestinationView
import com.santaistiger.gomourdeliveryapp.ui.customview.DetailMessageView
import com.santaistiger.gomourdeliveryapp.ui.customview.DetailPriceView
import java.text.SimpleDateFormat


object BindingUtils {
    private const val TAG = "BindingUtils"

    @BindingAdapter("bind_detail_store_list")
    @JvmStatic
    fun bindStoreList(recyclerView: RecyclerView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            val order = item.value!!
            val stores = order.stores

            if (recyclerView.adapter == null) {
                recyclerView.layoutManager =
                    LinearLayoutManager(recyclerView.context)
                recyclerView.adapter = OrderDetailStoreAdapter(order)
            }
            (recyclerView.adapter as OrderDetailStoreAdapter).items = stores ?: ArrayList()
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    @BindingAdapter("bind_detail_destination")
    @JvmStatic
    fun bindDestination(view: DetailDestinationView, item: MutableLiveData<Order>) {
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


    @BindingAdapter("bind_detail_message")
    @JvmStatic
    fun bindMessage(view: DetailMessageView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            view.binding.tvMessage.text = item.value!!.message
        }
    }

    @BindingAdapter("bind_detail_price")
    @JvmStatic
    fun bindPrice(view: DetailPriceView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            val order = item.value!!
            var price = order.deliveryCharge ?: 0
            for (store in order.stores!!) {
                price += store.cost ?: 0
            }
            view.binding.tvPrice.text = numberFormat.format(price) + " 원"
        }
    }

    @BindingAdapter("bind_detail_delivery_time")
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

    @BindingAdapter("bind_detail_cost")
    @JvmStatic
    fun bindCost(view: Button, item: Int?) {
        if (item != null) {
            view.isClickable = false
            view.text = numberFormat.format(item)
        }
    }
}

