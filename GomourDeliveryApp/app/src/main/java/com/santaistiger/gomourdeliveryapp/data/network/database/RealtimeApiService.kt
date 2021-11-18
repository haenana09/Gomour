package com.santaistiger.gomourdeliveryapp.data.network.database

import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.model.Status
import com.santaistiger.gomourdeliveryapp.ui.adapter.OrderListAdapter
import kotlinx.coroutines.tasks.await


object RealtimeApi {
    private const val TAG: String = "FirebaseApiService"
    private const val ORDER_REQUEST_TABLE = "order_request"
    private const val ORDER_TABLE = "order"

    private val database = Firebase.database
    private val orderRequestTable = database.getReference(ORDER_REQUEST_TABLE)
    private val orderTable = database.getReference(ORDER_TABLE)

    suspend fun readOrderDetail(orderId: String): OrderResponse {
        val response = OrderResponse()
        try {
            response.order = orderTable.child(orderId)
                .get().await().getValue(Order::class.java)

        } catch (e: Exception) {
            response.exception = e
            e.printStackTrace()
        }
        return response
    }

    fun updateOrder(key: String, order: Order) {
        orderTable.child(key).setValue(order)
    }

    // realtime database의 order 테이블에 있는 배달원의 배달 주문 목록을 받아와 해당 값을 반환한다.
    fun readOrderList(deliveryManUid: String): Query {
        val orderList = orderTable.orderByChild("deliveryManUid").equalTo(deliveryManUid)

        return orderList
    }
}