package com.santaistiger.gomourdeliveryapp.utils

import androidx.databinding.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.santaistiger.gomourdeliveryapp.data.model.Place
import com.santaistiger.gomourdeliveryapp.data.model.Store
import com.santaistiger.gomourdeliveryapp.ui.customview.DestinationView
import com.santaistiger.gomourdeliveryapp.ui.customview.MessageView
import com.santaistiger.gomourdeliveryapp.ui.customview.PriceView
import com.santaistiger.gomourdeliveryapp.ui.orderdetail.StoreAdapter

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
}

