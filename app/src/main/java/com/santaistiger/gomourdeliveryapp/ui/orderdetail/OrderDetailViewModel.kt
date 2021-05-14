package com.santaistiger.gomourdeliveryapp.ui.orderdetail

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.model.Status
import com.santaistiger.gomourdeliveryapp.data.repository.Repository
import com.santaistiger.gomourdeliveryapp.data.repository.RepositoryImpl
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

val TAG = "OrderDetailViewModel"

class OrderDetailViewModel(orderId: String) : ViewModel() {
    val order: MutableLiveData<Order> = liveData(Dispatchers.IO) {
        emit(repository.getOrderDetail(orderId))
    } as MutableLiveData<Order>

    val isPickupCompleteBtnClick = MutableLiveData<Boolean>()
    val isDeliveryCompleteBtnClick = MutableLiveData<Boolean>()
    val isCallBtnClick = MutableLiveData<Boolean>()
    val isTextBtnClick = MutableLiveData<Boolean>()

    private val repository: Repository = RepositoryImpl

    private fun notifyOrderChange() {
        order.value = order.value
    }

    fun onPickupCompleteBtnClick() {
        isPickupCompleteBtnClick.value = true
    }

    fun donePickupCompleteBtnClick() {
        isPickupCompleteBtnClick.value = false
    }

    fun completePickup() {
        order.value!!.status = Status.PICKUP_COMPLETE
        notifyOrderChange()
        repository.updateOrder(order.value!!)
    }

    fun onDeliveryCompleteBtnClick() {
        isDeliveryCompleteBtnClick.value = true
    }

    fun doneDeliveryCompleteBtnClick() {
        isDeliveryCompleteBtnClick.value = false
    }

    fun completeDelivery() {
        order.value!!.status = Status.DELIVERY_COMPLETE
        notifyOrderChange()
        repository.updateOrder(order.value!!)
    }

    fun onCallBtnClick() {
        isCallBtnClick.value = true
    }

    fun doneCallBtnClick() {
        isCallBtnClick.value = false
    }

    fun onTextBtnClick() {
        isTextBtnClick.value = true
    }

    fun doneTextBtnClick() {
        isTextBtnClick.value = false
    }

    fun getCustomerUid() = order.value!!.customerUid!!
}