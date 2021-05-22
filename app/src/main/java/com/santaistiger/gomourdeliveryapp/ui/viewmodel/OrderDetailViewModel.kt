package com.santaistiger.gomourdeliveryapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.model.Status
import com.santaistiger.gomourdeliveryapp.data.network.database.RealtimeApi
import com.santaistiger.gomourdeliveryapp.data.repository.Repository
import com.santaistiger.gomourdeliveryapp.data.repository.RepositoryImpl
import com.santaistiger.gomourdeliveryapp.utils.NotEnteredException
import com.santaistiger.gomourdeliveryapp.utils.StatusException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch



class OrderDetailViewModel(orderId: String) : ViewModel() {
    companion object {
        private const val TAG = "OrderDetailViewModel"
    }

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
        updateOrder()
    }

    fun checkCostInput() {
        // 모든 가게에 가격이 입력되지 않으면 오류
        for (store in order.value!!.stores!!) {
            if (store.cost == null) {
                throw NotEnteredException("모든 가게에 대하여 가격을 입력해야합니다.")
            }
        }
    }

    fun onDeliveryCompleteBtnClick() {
        isDeliveryCompleteBtnClick.value = true
    }

    fun doneDeliveryCompleteBtnClick() {
        isDeliveryCompleteBtnClick.value = false
    }

    fun completeDelivery() {
        order.value!!.status = Status.DELIVERY_COMPLETE
        updateOrder()
    }

    fun updateOrder() {
        notifyOrderChange()
        repository.updateOrder(order.value!!)
    }

    fun checkStatus() {
        if (order.value!!.status != Status.PICKUP_COMPLETE) {
            throw StatusException("먼저 픽업을 완료해주세요.")
        }
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