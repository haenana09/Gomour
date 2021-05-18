package com.santaistiger.gomourdeliveryapp.data.repository

import com.google.firebase.database.DatabaseReference
import com.santaistiger.gomourdeliveryapp.data.model.Order

interface Repository {
    suspend fun getOrderDetail(orderId: String): Order?
    suspend fun getCustomerPhone(customerUid: String): String?
    fun updateOrder(order: Order)
    fun getUid(): String

}