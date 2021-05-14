package com.santaistiger.gomourdeliveryapp.data.repository

import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.network.firebase.FirestoreApiService
import com.santaistiger.gomourdeliveryapp.data.network.firebase.RealtimeDBApiService

object RepositoryImpl : Repository {
    override suspend fun getOrderDetail(orderId: String): Order? {
        val response =  RealtimeDBApiService.getOrderDetail(orderId)
        return response.order
    }

    override suspend fun getCustomerPhone(customerUid: String): String? {
        val response = FirestoreApiService.getCustomer(customerUid)
        val customer = response.customer
        return customer?.phone
    }

    override fun updateOrder(order: Order) {
        RealtimeDBApiService.updateOrder(order.orderId!!, order)
    }

}