package com.santaistiger.gomourdeliveryapp.data.network

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourdeliveryapp.data.model.Customer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object FirestoreApi {
    val database = Firebase.firestore
    val customerTable = database.collection("customer")
    val deliveryManTable = database.collection("deliveryMan")

//    fun getCustomerPhone(customerUid: String): String? {
//        val docRef = customerTable.document(customerUid)
//        var phone: String? = null
//        CoroutineScope(Dispatchers.IO).launch {
//            docRef.get().addOnSuccessListener { documentSnapshot ->
//                val customer = documentSnapshot.toObject<Customer>()
//                async {
//                    phone = customer?.phone }
//            }
//            phone.await()
//        }
//    }
}