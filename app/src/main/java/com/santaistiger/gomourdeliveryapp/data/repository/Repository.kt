package com.santaistiger.gomourdeliveryapp.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.santaistiger.gomourdeliveryapp.data.model.Customer
import com.santaistiger.gomourdeliveryapp.data.model.DeliveryMan
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.model.OrderRequest

interface Repository {
    suspend fun getOrderDetail(orderId: String): Order?
    suspend fun getCustomerPhone(customerUid: String): String?
    suspend fun readDeliveryManInfo(deliveryManUid:String): DeliveryMan?
    suspend fun login(firebaseAuth: FirebaseAuth, email:String, password:String): AuthResult?
    suspend fun join(firebaseAuth: FirebaseAuth, email:String, password:String): AuthResult?
    suspend fun checkJoinable(email:String):Boolean
    fun updateAuthPassword(password:String)
    fun updateFireStorePassword(deliveryManUid: String,password:String)
    fun updatePhone(deliveryManUid: String,phone:String)
    fun deleteAuthDeliveryMan()
    fun deleteFireStoreDeliveryMan(deliveryManUid: String)
    fun writeFireStoreDeliveryMan(deliveryMan:DeliveryMan)
    fun writeAuthDeliveryMan(email:String,password: String)
    fun updateOrder(order: Order)
    fun getUid(): String

}