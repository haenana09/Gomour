package com.santaistiger.gomourdeliveryapp.data.repository

import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.network.database.AuthApi
import com.santaistiger.gomourdeliveryapp.data.network.database.RealtimeApi
import com.santaistiger.gomourdeliveryapp.data.network.firebase.FireStoreApi

object RepositoryImpl : Repository {
    override suspend fun getOrderDetail(orderId: String): Order? {
        val response =  RealtimeApi.readOrderDetail(orderId)
        return response.order
    }

    override suspend fun getCustomerPhone(customerUid: String): String? {
        val response = FireStoreApi.readCustomer(customerUid)
        val customer = response.customer
        return customer?.phone
    }

    override fun updateOrder(order: Order) {
        RealtimeApi.updateOrder(order.orderId!!, order)
    }

    override fun getUid(): String = AuthApi.readUid()?:String()

}