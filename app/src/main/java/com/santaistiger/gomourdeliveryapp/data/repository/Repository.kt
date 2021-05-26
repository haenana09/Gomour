package com.santaistiger.gomourdeliveryapp.data.repository

import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.ui.adapter.OrderListAdapter

interface Repository {
    suspend fun getOrderDetail(orderId: String): Order?
    suspend fun getCustomerPhone(customerUid: String): String?
    fun updateOrder(order: Order)
    fun getUid(): String
    fun readOrderList(deliveryManUid: String): Query
}