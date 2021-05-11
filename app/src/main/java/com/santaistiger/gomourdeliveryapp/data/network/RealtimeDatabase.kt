package com.santaistiger.gomourdeliveryapp.data.network

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourdeliveryapp.data.model.Order


private val TAG = "RealtimeDatabase"

object RealtimeDatabase {
    private var database: DatabaseReference = Firebase.database.reference
    private var orderTable = database.child("order")

    fun getOrderDetail(orderId: String): DatabaseReference = orderTable.child(orderId)

    fun updateOrder(key: String, order: Order) {
        orderTable.child(key).setValue(order)
    }
}