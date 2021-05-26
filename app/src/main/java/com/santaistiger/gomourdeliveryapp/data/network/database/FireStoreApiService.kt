package com.santaistiger.gomourdeliveryapp.data.network.firebase

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourdeliveryapp.data.model.Customer
import com.santaistiger.gomourdeliveryapp.data.model.DeliveryMan
import com.santaistiger.gomourdeliveryapp.data.network.database.CustomerResponse
import com.santaistiger.gomourdeliveryapp.data.network.database.DeliveryManResponse
import kotlinx.coroutines.tasks.await
import java.lang.Exception

object FireStoreApi {
    private val database = Firebase.firestore
    private val customerTable = database.collection("customer")
    private val deliveryManTable = database.collection("deliveryMan")

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
            response.deliveryMan = deliveryManTable.document(deliveryManUid)
                .get().await().toObject(DeliveryMan::class.java)
        } catch (e: Exception) {
            response.exception = e
        }
        return response
    }
    suspend fun checkJoinable(email:String): Boolean{
        return customerTable.whereEqualTo("email",email).get().await().isEmpty
    }

    fun updateFireStorePassword(deliveryManUid: String,password:String){
        deliveryManTable.document(deliveryManUid).update("password",password)
    }

    fun updatePhone(deliveryManUid: String,phone:String){
        deliveryManTable.document(deliveryManUid).update("phone",phone)
    }


    fun deleteFireStoreDeliveryMan(deliveryManUid: String){
        deliveryManTable.document(deliveryManUid).delete()
    }

    fun writeFireStoreDeliveryMan(deliveryMan: DeliveryMan){
        deliveryManTable.document(deliveryMan.uid!!).set(deliveryMan)
    }

}