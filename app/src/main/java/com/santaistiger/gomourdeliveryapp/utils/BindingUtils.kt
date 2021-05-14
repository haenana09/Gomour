package com.santaistiger.gomourdeliveryapp.utils

import android.widget.TextView
import androidx.databinding.*
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.model.Place
import com.santaistiger.gomourdeliveryapp.data.model.Status
import com.santaistiger.gomourdeliveryapp.data.model.Store
import com.santaistiger.gomourdeliveryapp.ui.customview.DestinationView
import com.santaistiger.gomourdeliveryapp.ui.customview.MessageView
import com.santaistiger.gomourdeliveryapp.ui.customview.PriceView
import com.santaistiger.gomourdeliveryapp.ui.orderdetail.StoreAdapter
import java.text.SimpleDateFormat

val TAG = "BindingUtils"

object BindingUtils {
    @BindingAdapter("bind_store_list")
    @JvmStatic
    fun bindStoreList(recyclerView: RecyclerView, items: ObservableArrayList<Store>) {
        if (recyclerView.adapter == null) {
            recyclerView.layoutManager =
                LinearLayoutManager(recyclerView.context)
            recyclerView.adapter = StoreAdapter()
        }
        (recyclerView.adapter as StoreAdapter).items = items
        recyclerView.adapter?.notifyDataSetChanged()
    }

    @BindingAdapter("bind_destination")
    @JvmStatic
    fun bindDestination(view: DestinationView, item: ObservableParcelable<Place>) {
        view.binding.item = item.get()
    }

    @BindingAdapter("bind_message")
    @JvmStatic
    fun bindMessage(view: MessageView, item: ObservableField<String>) {
        view.binding.message = item.get()
    }

    @BindingAdapter("bind_price")
    @JvmStatic
    fun bindPrice(view: PriceView, item: ObservableInt) {
        view.binding.price = item.get()
    }

    @BindingAdapter("bind_delivery_time")
    @JvmStatic
    fun bindDeliveryTime(view: TextView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            val order = item.value!!
            view.text = if (order.status == Status.DELIVERY_COMPLETE) {
                SimpleDateFormat("yyyy-MM-dd hh:mm 도착").format(order.deliveryTime)
            } else {
                SimpleDateFormat("yyyy-MM-dd hh:mm 도착 예정").format(order.deliveryTime)
            }
        }
    }

}

