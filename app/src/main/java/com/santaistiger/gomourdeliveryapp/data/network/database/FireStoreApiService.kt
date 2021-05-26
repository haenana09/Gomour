package com.santaistiger.gomourdeliveryapp.data.network.database

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourdeliveryapp.data.model.AccountInfo
import com.santaistiger.gomourdeliveryapp.data.model.Customer
import com.santaistiger.gomourdeliveryapp.data.model.DeliveryMan
import kotlinx.coroutines.tasks.await

object FireStoreApi {
    private const val TAG = "FireStoreApiService"
    private const val CUSTOMER_TABLE = "customer"
    private const val DELIVERY_MAN_TABLE = "deliveryMan"

    private val database = Firebase.firestore
    private val customerTable = database.collection(CUSTOMER_TABLE)
    private val deliveryManTable = database.collection(DELIVERY_MAN_TABLE)


    suspend fun readCustomer(customerUid: String): CustomerResponse {
        val response = CustomerResponse()
        try {
            response.customer = customerTable.document(customerUid)
                .get().await().toObject(Customer::class.java)
        } catch (e: Exception) {
            response.exception = e
        }
        return response
    }

    suspend fun readDeliveryMan(deliveryManUid: String): DeliveryManResponse {
        val response = DeliveryManResponse()
        try {
            Log.i(TAG, "deliveryManUid = $deliveryManUid")
            response.deliveryMan = deliveryManTable.document(deliveryManUid)
                .get().await().toObject(DeliveryMan::class.java)
        } catch (e: Exception) {
            response.exception = e
            Log.i(TAG, e.toString())
        }
        return response
    }

    suspend fun checkJoinable(email: String): Boolean {
        return deliveryManTable.whereEqualTo("email", email).get().await().isEmpty
    }

    fun updateAccountInfo(deliveryManUid: String,accountInfo: AccountInfo){
        deliveryManTable.document(deliveryManUid).update("accountInfo",accountInfo)
    }

    fun updateFireStorePassword(deliveryManUid: String, password: String) {
        deliveryManTable.document(deliveryManUid).update("password", password)
    }

    fun updatePhone(deliveryManUid: String, phone: String) {
        deliveryManTable.document(deliveryManUid).update("phone", phone)
    }


    fun deleteFireStoreDeliveryMan(deliveryManUid: String) {
        deliveryManTable.document(deliveryManUid).delete()
    }

    fun writeFireStoreDeliveryMan(deliveryMan: DeliveryMan) {
        deliveryManTable.document(deliveryMan.uid!!).set(deliveryMan)
    }

}