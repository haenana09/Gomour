package com.santaistiger.gomourdeliveryapp.data.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.UploadTask
import com.santaistiger.gomourdeliveryapp.data.model.AccountInfo
import com.santaistiger.gomourdeliveryapp.data.model.DeliveryMan
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.ui.adapter.OrderListAdapter

interface Repository {
    suspend fun getOrderDetail(orderId: String): Order?
    suspend fun getCustomerPhone(customerUid: String): String?
    suspend fun readDeliveryManInfo(): DeliveryMan?
    suspend fun login(firebaseAuth: FirebaseAuth, email:String, password:String): AuthResult?
    suspend fun join(firebaseAuth: FirebaseAuth, email:String, password:String): AuthResult?
    suspend fun checkJoinable(email:String):Boolean
    fun updateAccountInfo(deliveryManUid: String,accountInfo: AccountInfo)
    fun updateAuthPassword(password:String)
    fun updateFireStorePassword(deliveryManUid: String,password:String)
    fun updatePhone(deliveryManUid: String,phone:String)
    fun deleteAuthDeliveryMan()
    fun deleteFireStoreDeliveryMan(deliveryManUid: String)
    fun writeFireStoreDeliveryMan(deliveryMan:DeliveryMan)
    fun writeAuthDeliveryMan(email:String,password: String)
    fun updateOrder(order: Order)
    fun getUid(): String
    fun uploadImage(file: Uri): UploadTask
    fun readOrderList(deliveryManUid: String): Query
}