package com.santaistiger.gomourdeliveryapp.data.repository

import com.google.firebase.database.DatabaseReference
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.network.RealtimeDatabase
import com.santaistiger.gomourdeliveryapp.ui.orderdetail.OrderDetailViewModel

object RepositoryImpl : Repository {
    override suspend fun getOrderDetail(orderId: String): DatabaseReference {
        return RealtimeDatabase.getOrderDetail(orderId)
    }

    override fun updateOrder(order: Order) {
        RealtimeDatabase.updateOrder(order.orderId!!, order)
    }
}