package com.santaistiger.gomourdeliveryapp.data.repository

import android.net.Uri
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.UploadTask
import com.santaistiger.gomourdeliveryapp.data.model.AccountInfo
import com.santaistiger.gomourdeliveryapp.data.model.DeliveryMan
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.network.database.AuthApi
import com.santaistiger.gomourdeliveryapp.data.network.database.RealtimeApi
import com.santaistiger.gomourdeliveryapp.data.network.database.StorageApi
import com.santaistiger.gomourdeliveryapp.data.network.database.FireStoreApi

object RepositoryImpl : Repository {
    override suspend fun getOrderDetail(orderId: String): Order? {
        val response = RealtimeApi.readOrderDetail(orderId)
        return response.order
    }

    override suspend fun getCustomerPhone(customerUid: String): String? {
        val response = FireStoreApi.readCustomer(customerUid)
        val customer = response.customer
        return customer?.phone
    }

    override suspend fun readDeliveryManInfo(): DeliveryMan? {
        val response = FireStoreApi.readDeliveryMan(getUid())
        return response.deliveryMan
    }

    override suspend fun login(
        firebaseAuth: FirebaseAuth,
        email: String,
        password: String
    ): AuthResult? {
        val response = AuthApi.login(firebaseAuth, email, password)
        return response.authResult
    }

    override suspend fun join(
        firebaseAuth: FirebaseAuth,
        email: String,
        password: String
    ): AuthResult? {
        val response = AuthApi.join(firebaseAuth, email, password)
        return response.authResult
    }

    // 가입할 수 있으면 true, 가입할 수 없으면 false
    override suspend fun checkJoinable(email: String): Boolean = FireStoreApi.checkJoinable(email)

    override fun updateAccountInfo(deliveryManUid: String, accountInfo: AccountInfo) {
        FireStoreApi.updateAccountInfo(deliveryManUid,accountInfo)
    }

    override fun updateAuthPassword(password: String) {
        AuthApi.updateAuthPassword(password)
    }

    override fun updateFireStorePassword(deliveryManUid: String, password: String) {
        FireStoreApi.updateFireStorePassword(deliveryManUid, password)
    }

    override fun updatePhone(deliveryManUid: String, phone: String) {
        FireStoreApi.updatePhone(deliveryManUid, phone)
    }

    override fun deleteAuthDeliveryMan() {
        AuthApi.deleteAuthDeliveryMan()
    }

    override fun deleteFireStoreDeliveryMan(deliveryManUid: String) {
        FireStoreApi.deleteFireStoreDeliveryMan(deliveryManUid)
    }

    override fun writeFireStoreDeliveryMan(deliveryMan: DeliveryMan) {
        FireStoreApi.writeFireStoreDeliveryMan(deliveryMan)
    }

    override fun writeAuthDeliveryMan(email: String, password: String) {
        AuthApi.writeAuthDeliveryMan(email, password)
    }

    override fun updateOrder(order: Order) {
        RealtimeApi.updateOrder(order.orderId!!, order)
    }

    override fun getUid(): String = AuthApi.readUid() ?: String()

    override fun uploadImage(file: Uri): UploadTask =
        StorageApi.uploadImage(getUid(), file)

}