package com.santaistiger.gomourdeliveryapp.data.network.database

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourdeliveryapp.data.model.Customer
import com.santaistiger.gomourdeliveryapp.data.network.database.CustomerResponse
import kotlinx.coroutines.tasks.await
import java.lang.Exception

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
}