package com.santaistiger.gomourdeliveryapp.data.repository

import android.widget.TextView
import com.google.firebase.database.Query
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.network.database.AuthApi
import com.santaistiger.gomourdeliveryapp.data.network.database.RealtimeApi
import com.santaistiger.gomourdeliveryapp.data.network.firebase.FireStoreApi
import com.santaistiger.gomourdeliveryapp.ui.adapter.OrderListAdapter

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

    // realtime database의 order 테이블에 있는 배달원의 배달 주문 목록을 받아와 해당 값을 반환한다.
    override fun readOrderList(deliveryManUid: String): Query {
        val orderList = RealtimeApi.readOrderList(deliveryManUid)
        return orderList
    }
}