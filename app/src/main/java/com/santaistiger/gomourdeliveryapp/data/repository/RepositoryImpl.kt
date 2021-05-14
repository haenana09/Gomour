package com.santaistiger.gomourdeliveryapp.data.repository

import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.network.firebase.FirestoreApi
import com.santaistiger.gomourdeliveryapp.data.network.firebase.RealtimeDBApi

object RepositoryImpl : Repository {
    override suspend fun getOrderDetail(orderId: String): Order? {
        val response =  RealtimeDBApi.getOrderDetail(orderId)
        return response.order
    }

    override suspend fun getCustomerPhone(customerUid: String): String? {
        val response = FirestoreApi.getCustomer(customerUid)
        val customer = response.customer
        return customer?.phone
    }

    override fun updateOrder(order: Order) {
        RealtimeDBApi.updateOrder(order.orderId!!, order)
    }

}