package com.santaistiger.gomourdeliveryapp.data.network.firebase

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.network.firebase.OrderResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.lang.Exception


private val TAG = "RealtimeDBApiService"

object RealtimeDBApiService {
    private var database: DatabaseReference = Firebase.database.reference
    private var orderTable = database.child("order")

    suspend fun getOrderDetail(orderId: String): OrderResponse {
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
}