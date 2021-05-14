package com.santaistiger.gomourdeliveryapp.data.network.firebase

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourdeliveryapp.data.model.Customer
import kotlinx.coroutines.tasks.await
import java.lang.Exception

object FirestoreApiService {
    private val database = Firebase.firestore
    private val customerTable = database.collection("customer")
    private val deliveryManTable = database.collection("deliveryMan")

    suspend fun getCustomer(customerUid: String): CustomerResponse {
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