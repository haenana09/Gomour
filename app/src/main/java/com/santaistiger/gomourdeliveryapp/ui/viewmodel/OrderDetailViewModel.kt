/**
 * created by Kang Gumsil
 */
package com.santaistiger.gomourdeliveryapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.model.Status
import com.santaistiger.gomourdeliveryapp.data.repository.Repository
import com.santaistiger.gomourdeliveryapp.data.repository.RepositoryImpl
import com.santaistiger.gomourdeliveryapp.utils.NotEnteredException
import com.santaistiger.gomourdeliveryapp.utils.StatusException
import kotlinx.coroutines.Dispatchers


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
        // 모든 주문 장소의 cost가 입력되지 않으면 오류
        for (store in order.value!!.stores!!) {
            if (store.cost == null) {
                throw NotEnteredException("먼저 상품의 가격을 입력해주세요")
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
        order.value!!.deliveryTime = System.currentTimeMillis()
        updateOrder()
    }

    private fun updateOrder() {
        notifyOrderChange()
        repository.updateOrder(order.value!!)
    }

    fun checkStatus() {
        if (order.value!!.status != Status.PICKUP_COMPLETE) {
            throw StatusException("먼저 픽업을 완료해주세요")
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